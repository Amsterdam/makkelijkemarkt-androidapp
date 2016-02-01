/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

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

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 *
 * @author marcolangebeeke
 */
public class MarktenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = MarktenFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.listview_markten) ListView mMarktenListView;

    // unique id for the markten loader
    private static final int MARKTEN_LOADER = 1;

    // cursoradapter for populating the markten litsview with markten from the database
    private SimpleCursorAdapter mMarktenAdapter;

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

        // get the markt id and naam from the selected item
        Cursor selectedMarkt = (Cursor) mMarktenAdapter.getItem(position);
        int id = selectedMarkt.getInt(selectedMarkt.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_ID));
        String naam = selectedMarkt.getString(selectedMarkt.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_NAAM));

        // add markt id and naam to the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(getString(R.string.sharedpreferences_key_markt_id), id);
        editor.putString(getString(R.string.sharedpreferences_key_markt_naam), naam);
        editor.apply();

        // open the dagvergunningen activity
        Intent intent = new Intent(getActivity(), DagvergunningenActivity.class);
        startActivity(intent);
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
        loader.setProjection(new String[]{
                MakkelijkeMarktProvider.Account.COL_ID,
                MakkelijkeMarktProvider.Account.COL_NAAM
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Account.COL_NAAM +" ASC"
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
}