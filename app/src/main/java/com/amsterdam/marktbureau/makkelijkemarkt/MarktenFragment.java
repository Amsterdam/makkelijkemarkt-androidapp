/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetMarkten;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiMarkt;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class MarktenFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        Callback<List<ApiMarkt>> {

    // use classname when logging
    private static final String LOG_TAG = MarktenFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.listview_markten) ListView mMarktenListView;
    @Bind(R.id.progressbar_markten) ProgressBar mMarktenProgressBar;

    // unique id for the markten loader
    private static final int MARKTEN_LOADER = 2;

    // cursoradapter for populating the markten litsview with markten from the database
    private SimpleCursorAdapter mMarktenAdapter;

    // common toast object
    protected Toast mToast;

    /**
     * Constructor
     */
    public MarktenFragment() {
    }

    /**
     * Inflate the markten_fragment layout containing the markten listview
     * @param inflater LayoutInflator
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the markten fragment
        View mainView = inflater.inflate(R.layout.markten_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // create an adapter for the markten listview
        mMarktenAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.markten_list_item,
                null,
                new String[] { MakkelijkeMarktProvider.Markt.COL_NAAM },
                new int[] { R.id.markt_naam },
                0);

        // attach the adapter to the markten listview
        mMarktenListView.setAdapter(mMarktenAdapter);

        if (savedInstanceState == null) {

            // check time in hours since last fetched the markten
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            long diffInHours = getResources().getInteger(R.integer.makkelijkemarkt_api_markten_fetch_interval_hours);
            if (settings.contains(getContext().getString(R.string.sharedpreferences_key_markten_last_fetched))) {
                long lastFetchTimestamp = settings.getLong(getContext().getString(R.string.sharedpreferences_key_markten_last_fetched), 0);
                long differenceMs  = new Date().getTime() - lastFetchTimestamp;
                diffInHours = TimeUnit.MILLISECONDS.toHours(differenceMs);
            }

            // update the local markten by reloading them from the api (with an http client containing
            // an interceptor that will modify the response to transform the aanwezigeopties object
            // into an array of strings)
            if (diffInHours >= getResources().getInteger(R.integer.makkelijkemarkt_api_markten_fetch_interval_hours)) {

                // show the progressbar
                mMarktenProgressBar.setVisibility(View.VISIBLE);

                ApiGetMarkten getMarkten = new ApiGetMarkten(getContext());
                getMarkten.addAanwezigeOptiesInterceptor();
                getMarkten.enqueue(this);
            }
        }

        // inititate loading the markten from the database
        getLoaderManager().initLoader(MARKTEN_LOADER, null, this);

        return mainView;
    }

    /**
     * When selecting a markt in the listview get the markt id and naam and store it in the
     * shared preferences
     * @param position the selected position in the markten listview
     */
    @OnItemClick(R.id.listview_markten)
    public void onItemClick(int position) {

        // get the markt id, naam and producten from the selected item
        Cursor selectedMarkt = (Cursor) mMarktenAdapter.getItem(position);
        int id = selectedMarkt.getInt(selectedMarkt.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_ID));
        String naam = selectedMarkt.getString(selectedMarkt.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_NAAM));
        String producten = selectedMarkt.getString(selectedMarkt.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_AANWEZIGE_OPTIES));

        // add markt id, naam and producten to the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(getString(R.string.sharedpreferences_key_markt_id), id);
        editor.putString(getString(R.string.sharedpreferences_key_markt_naam), naam);
        editor.putString(getString(R.string.sharedpreferences_key_markt_producten), producten);
        editor.apply();

        // open the dagvergunningen activity
        Intent dagvergunningenIntent = new Intent(getActivity(), DagvergunningenActivity.class);
        startActivity(dagvergunningenIntent);
    }

    /**
     * Create the loader to get the markten from the database
     * @param id the unique id for this loader
     * @param args the arguments given in the initloader call in oncreateview
     * @return a cursor containing the markten
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriMarkt);
        loader.setProjection(new String[] {
                MakkelijkeMarktProvider.Markt.COL_ID,
                MakkelijkeMarktProvider.Markt.COL_NAAM,
                MakkelijkeMarktProvider.Markt.COL_AANWEZIGE_OPTIES
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Markt.COL_NAAM + " ASC"
        );

        return loader;
    }

    /**
     * When done loading update the adapter of the listview with the loaded markten
     * @param loader the loader object
     * @param data a cursor containing the markten
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMarktenAdapter.swapCursor(data);
    }

    /**
     * Clear the markten from the listview adapter
     * @param loader the loader object
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMarktenAdapter.swapCursor(null);
    }

    /**
     * Response from the getMarkten method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiMarkt>> response) {

        // hide progressbar
        mMarktenProgressBar.setVisibility(View.GONE);

        if (response.body() != null && response.body().size() > 0) {

            // copy the values to a contentvalues array that can be used in the
            // contentprovider bulkinsert method
            ContentValues[] contentValues = new ContentValues[response.body().size()];
            for (int i = 0; i < response.body().size(); i++) {
                contentValues[i] = response.body().get(i).toContentValues();
            }

            // delete existing markten and insert downloaded marken into db
            if (contentValues.length > 0) {
                getContext().getContentResolver().delete(MakkelijkeMarktProvider.mUriMarkt, null, null);
                getContext().getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriMarkt, contentValues);

                // when we are done, remember when we last fetched the markten
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(
                        getContext().getString(R.string.sharedpreferences_key_markten_last_fetched),
                        new Date().getTime());
                editor.apply();
            }
        }
    }

    /**
     * On failure of the getMarkten method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {

        // hide progressbar
        mMarktenProgressBar.setVisibility(View.GONE);
        mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_markets_fetch_failed) + ": " + t.getMessage());
    }
}