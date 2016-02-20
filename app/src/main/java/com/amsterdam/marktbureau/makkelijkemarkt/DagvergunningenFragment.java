/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetDagvergunningen;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningenFragment.class.getSimpleName();

    static final int ADD_DAGVERGUNNING_REQUEST = 1;

    // bind layout elements
    @Bind(R.id.listview_dagvergunningen) ListView mDagvergunningenListView;
    @Bind(R.id.fab_add_dagvergunning) FloatingActionButton mFabAddDagvergunning;

    // the id of the selected markt
    private int mMarktId;

    // the date of today in a formatted string
    private String mDag;

    // unique id for the markten loader
    private static final int DAGVERGUNNINGEN_LOADER = 3;

    // cursoradapter for populating the dagvergunningen litsview with dagvergunningen from the database
    private DagvergunningenListAdapter mDagvergunningenAdapter;

    /**
     * Constructor
     */
    public DagvergunningenFragment() {
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
        View mainView = inflater.inflate(R.layout.dagvergunningen_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // get the id of selected market from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMarktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

        // get the date of today for the dag param
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        mDag = sdf.format(new Date());

        // @todo remove api call here and only call on interval basis in the service?
        // @todo or keep it here, so we can show a zandloper and start calling it on interval basis in the service once this call is finished?
        // @todo best way would be to call it here one time, so we use the progressbar, and on interval basis call it in the service
        // @todo in the service we need to see what the interval for eacht type of call should be:
        // @todo: dagvergunningen: 1x per minute?
        // @todo: keep the api session allive: 30sec? (is this even necceserry? or we can do this also by asking for something small?)

        if (savedInstanceState == null) {
            ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(getContext());
            getDagvergunningen.setMarktId(String.valueOf(mMarktId));
            getDagvergunningen.setDag(mDag);
            getDagvergunningen.enqueue();



//            // TODO: prevent multiple times downloading the sollicitaties for the same markt id in the same session/day (shared prefs?)
//            ApiGetSollicitaties getSollicitaties = new ApiGetSollicitaties(getContext());
//            getSollicitaties.setMarktId(mMarktId);
//            getSollicitaties.enqueue();



        }

        // create an adapter for the dagvergunningen listview
        mDagvergunningenAdapter = new DagvergunningenListAdapter(getActivity(), null, 0);
        mDagvergunningenListView.setAdapter(mDagvergunningenAdapter);

        // pass markt id and dag as arguments bundle to the cursorloader
        Bundle args = new Bundle();
        args.putString(getString(R.string.sharedpreferences_key_markt_id), String.valueOf(mMarktId));
        args.putString(getString(R.string.sharedpreferences_key_date_today), mDag);

        // inititate loading the dagvergunningen from the database with the selected markt id and date of today
        getLoaderManager().initLoader(DAGVERGUNNINGEN_LOADER, args, this);

        return mainView;
    }

    /**
     * Select an existing dagvergunning and open it in the dagvergunning activity
     * @param position the position in the listview
     */
    @OnItemClick(R.id.listview_dagvergunningen)
    public void onItemClick(int position) {

        // get the dagvergunning id from the adapter based on the selected item position
        Cursor selectedDagvergunning = (Cursor) mDagvergunningenAdapter.getItem(position);
        int id = selectedDagvergunning.getInt(selectedDagvergunning.getColumnIndex(
                        MakkelijkeMarktProvider.Dagvergunning.COL_ID));

        // open the dagvergunning activity to edit the selected dagvergunning
        Intent intent = new Intent(getActivity(), DagvergunningActivity.class);
        intent.putExtra(MakkelijkeMarktProvider.mTableDagvergunning +
                        MakkelijkeMarktProvider.Dagvergunning.COL_ID, id);
        startActivity(intent);
    }

    /**
     * Open the dagvergunning activity to add a new dagvergunning
     */
    @OnClick(R.id.fab_add_dagvergunning)
    public void addDagvergunningClick() {
        Intent intent = new Intent(getActivity(), DagvergunningActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, ADD_DAGVERGUNNING_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // were we adding a new dagvergunning?
        if (requestCode == ADD_DAGVERGUNNING_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                Utility.log(getContext(), LOG_TAG, "RESULT_OK");

                Bundle args = new Bundle();
                args.putString(getString(R.string.sharedpreferences_key_markt_id), String.valueOf(mMarktId));
                args.putString(getString(R.string.sharedpreferences_key_date_today), mDag);
                getLoaderManager().restartLoader(DAGVERGUNNINGEN_LOADER, args, this);
            }
        }
    }

    /**
     * Create a cursor loader to get the dagvergunningen from the db
     * @param id unique id for this loader
     * @param args the markt id and dag arguments for setting the selection
     * @return a cursor loader ready to be started
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader that will load the dagvergunningen with joined table data for selected
        // markt for today sorted descending on aanmaak tijd
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriDagvergunningJoined);
        loader.setSelection(
                MakkelijkeMarktProvider.mTableDagvergunning + "." + MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? AND " +
                MakkelijkeMarktProvider.Dagvergunning.COL_DAG + " = ?"
        );
        loader.setSelectionArgs(new String[]{
                args.getString(getString(R.string.sharedpreferences_key_markt_id), null),
                args.getString(getString(R.string.sharedpreferences_key_date_today), "")
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD +" DESC"
        );

        return loader;
    }

    /**
     * When done loading update the adapter of the listview with the loaded dagvergunningen
     * @param loader the loader object
     * @param data a cursor containing the dagvergunningen
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDagvergunningenAdapter.swapCursor(data);
    }

    /**
     * Clear the dagvergunningen from the listview adapter
     * @param loader the loader object
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDagvergunningenAdapter.swapCursor(null);
    }
}