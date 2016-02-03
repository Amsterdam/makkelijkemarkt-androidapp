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
import android.support.v4.widget.SimpleCursorAdapter;
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

/**
 * @author marcolangebeeke
 */
public class DagvergunningenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningenFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.listview_dagvergunningen) ListView mDagvergunningenListView;

    // the id of the selected markt
    private int mMarktId;

    // the date of today in a formatted string
    private String mDag;

    // unique id for the markten loader
    private static final int DAGVERGUNNINGEN_LOADER = 3;

    // cursoradapter for populating the dagvergunningen litsview with dagvergunningen from the database
    private SimpleCursorAdapter mDagvergunningenAdapter;

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
        ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(getContext());
        getDagvergunningen.setMarktId(String.valueOf(mMarktId));
        getDagvergunningen.setDag(mDag);
        getDagvergunningen.enqueue();

        // @todo custom DagvergunningenAdapter maken met viewholder en bindview

        // create an adapter for the dagvergunningen listview
//        mDagvergunningenAdapter = new SimpleCursorAdapter(
//                getContext(),
//                R.layout.dagvergunningen_list_item,
//                null,
//                new String[] {
//                        MakkelijkeMarktProvider.Dagvergunning.COL_ID,
//                        MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE,
//                        MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD,
//                        MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE
//                },
//                new int[] {
//                        R.id.dagvergunning_id,
//                        R.id.dagvergunning_erkenningsnummer,
//                        R.id.dagvergunning_datumtijd,
//                        R.id.dagvergunning_totale_lengte
//                },
//                0);
        mDagvergunningenAdapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.dagvergunningen_list_item,
                null,
                new String[] {
                        MakkelijkeMarktProvider.Dagvergunning.COL_ID,
                        "koopman_id",
                        MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD,
                        MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM,
//                        MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE,
                },
                new int[] {
                        R.id.dagvergunning_id,
                        R.id.dagvergunning_erkenningsnummer,
                        R.id.dagvergunning_datumtijd,
                        R.id.dagvergunning_totale_lengte
                },
                0);

        // attach the adapter to the dagvergunningen listview
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
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader that will load the dagvergunningen with koopman data for selected
        // markt for today sorted descending on aanmaak tijd
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriDagvergunningKoopman);
//        loader.setProjection(new String[]{
//                "dagvergunning."+ MakkelijkeMarktProvider.Dagvergunning.COL_ID,
//                "koopman._id",
////                MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE,
//                MakkelijkeMarktProvider.mTableDagvergunning +"."+ MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD,
////                MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE,
//                "koopman."+ MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM
//        });
        loader.setSelection(
                MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? AND " +
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

        String[] columns = data.getColumnNames();
        for (int i=0; i<columns.length; i++) {
            String column = columns[i];
            Utility.log(getContext(), LOG_TAG, column);
        }

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