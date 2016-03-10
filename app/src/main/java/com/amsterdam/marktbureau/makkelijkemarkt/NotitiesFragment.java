/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.adapters.NotitiesAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetNotities;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPutNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author marcolangebeeke
 */
public class NotitiesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = NotitiesFragment.class.getSimpleName();

    // unique id for the notities loader
    private static final int NOTITIES_LOADER = 8;

    // bind layout elements
    @Bind(R.id.listview_notities) ListView mNotitiesListView;
    @Bind(R.id.progressbar_notities) ProgressBar mNotitiesProgressBar;
    @Bind(R.id.listview_empty) TextView mListViewEmptyTextView;

    // cursoradapter for populating the notities litsview with notities from the database
    private NotitiesAdapter mNotitiesAdapter;

    // common toast object
    protected Toast mToast;

    /**
     * Constructor
     */
    public NotitiesFragment() {
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the dagvergunningen fragment
        View mainView = inflater.inflate(R.layout.notities_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // get the id of selected market from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

        // get the date of today for the dag param
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        String dag = sdf.format(new Date());

        if (savedInstanceState == null) {

            // fetch notities for selected markt
            ApiGetNotities getNotities = new ApiGetNotities(getContext());
            getNotities.setMarktId(String.valueOf(marktId));
            getNotities.setDag(dag);
            getNotities.enqueue();
        }

        // create an adapter for the notities listview
        mNotitiesAdapter = new NotitiesAdapter(getActivity(), null, 0);
        mNotitiesListView.setAdapter(mNotitiesAdapter);

        // pass markt id and dag as arguments bundle to the cursorloader
        Bundle args = new Bundle();
        args.putString(getString(R.string.sharedpreferences_key_markt_id), String.valueOf(marktId));
        args.putString(getString(R.string.sharedpreferences_key_date_today), dag);

        // inititate loading the notities from the database with the selected markt id and date of today
        getLoaderManager().initLoader(NOTITIES_LOADER, args, this);

        return mainView;
    }

    @OnClick(R.id.fab_add_notitie)
    public void addNotitieClick() {
        Utility.log(getContext(), LOG_TAG, "FAB clicked!");

        // TODO: Implement NotitieActivity

    }

    /**
     * Get the non-removed notities for selected markt and dag
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader that will load the notities with joined table data for selected
        // markt for today sorted descending on aanmaak tijd
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriNotitie);
        loader.setSelection(
                MakkelijkeMarktProvider.Notitie.COL_VERWIJDERD + " = ? AND " +
                MakkelijkeMarktProvider.Notitie.COL_MARKT_ID + " = ? AND " +
                MakkelijkeMarktProvider.Notitie.COL_DAG + " = ?"
        );
        loader.setSelectionArgs(new String[] {
                "0",
                args.getString(getString(R.string.sharedpreferences_key_markt_id), null),
                args.getString(getString(R.string.sharedpreferences_key_date_today), "")
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Notitie.COL_AANGEMAAKT_DATUMTIJD + " DESC"
        );

        return loader;
    }

    /**
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // show the progressbar if we have no notities in the db yet
        mNotitiesProgressBar.setVisibility(data.getCount() == 0 ? View.VISIBLE : View.GONE);

        // show the empty notice if we have not notities in the db yet
        mListViewEmptyTextView.setVisibility(data.getCount() == 0 ? View.VISIBLE : View.GONE);

        mNotitiesAdapter.swapCursor(data);
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListViewEmptyTextView.setVisibility(View.VISIBLE);
        mNotitiesAdapter.swapCursor(null);
    }

    /**
     * Handle response event from api getnotities request onresponse method to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onGetNotitiesResponseEvent(ApiGetNotities.OnResponseEvent event) {

        // hide progressbar or show an error
        mNotitiesProgressBar.setVisibility(View.GONE);
        if (event.mNotitieCount == -1) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_notities_fetch_failed) + ": " + event.mMessage);
        }
    }

    /**
     * Handle update event when we start to update a notitie for showing the progressbar
     * @param event the received event
     */
    @Subscribe
    public void onUpdateNotitieEvent(NotitiesAdapter.OnUpdateEvent event) {
        mNotitiesProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Handle response event from api putnotitie request onresponse method to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onPutNotitieResponseEvent(ApiPutNotitie.OnResponseEvent event) {

        // hide progressbar or show an error
        mNotitiesProgressBar.setVisibility(View.GONE);
        if (event.mNotitie == null) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_notitie_update_failed) + ": " + event.mMessage);
        }
    }

    /**
     * Register eventbus handlers
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unregister eventbus handlers
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}