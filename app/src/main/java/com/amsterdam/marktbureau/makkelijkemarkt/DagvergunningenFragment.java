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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.adapters.DagvergunningenAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetDagvergunningen;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    // unique id for the dagvergunningen loader
    private static final int DAGVERGUNNINGEN_LOADER = 3;

    // bind layout elements
    @Bind(R.id.listview_dagvergunningen) ListView mDagvergunningenListView;
    @Bind(R.id.progressbar_dagvergunningen) ProgressBar mDagvergunningenProgressBar;
    @Bind(R.id.listview_empty) TextView mListViewEmptyTextView;

    // cursoradapter for populating the dagvergunningen litsview with dagvergunningen from the database
    private DagvergunningenAdapter mDagvergunningenAdapter;

    // common toast object
    private Toast mToast;

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
        int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

//        // crashlytics
//        Answers.getInstance().logContentView(new ContentViewEvent()
//                .putContentName(LOG_TAG)
//                .putContentId(String.valueOf(marktId)));

        // get the date of today for the dag param
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        String dag = sdf.format(new Date());

        if (savedInstanceState == null) {

            // fetch dagvergunningen for selected markt
            ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(getContext());
            getDagvergunningen.setMarktId(String.valueOf(marktId));
            getDagvergunningen.setDag(dag);
            getDagvergunningen.enqueue();
        }

        // create an adapter for the dagvergunningen listview
        mDagvergunningenAdapter = new DagvergunningenAdapter(getActivity());
        mDagvergunningenListView.setAdapter(mDagvergunningenAdapter);

        // pass markt id and dag as arguments bundle to the cursorloader
        Bundle args = new Bundle();
        args.putString(getString(R.string.sharedpreferences_key_markt_id), String.valueOf(marktId));
        args.putString(getString(R.string.sharedpreferences_key_date_today), dag);

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
        startActivity(intent);
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
                MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD + " DESC"
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

        // show the progressbar if we have no dagvergunningen in the db yet
        mDagvergunningenProgressBar.setVisibility(data.getCount() == 0 ? View.VISIBLE : View.GONE);

        // show the empty notice if we have not dagvergunningen in the db yet
        mListViewEmptyTextView.setVisibility(data.getCount() == 0 ? View.VISIBLE : View.GONE);

        mDagvergunningenAdapter.swapCursor(data);
    }

    /**
     * Clear the dagvergunningen from the listview adapter
     * @param loader the loader object
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListViewEmptyTextView.setVisibility(View.VISIBLE);
        mDagvergunningenAdapter.swapCursor(null);
    }

    /**
     * Handle response event from api get dagvergunningen request onresponse method to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onGetDagvergunningenResponseEvent(ApiGetDagvergunningen.OnResponseEvent event) {

        // hide progressbar or show an error
        mDagvergunningenProgressBar.setVisibility(View.GONE);
        if (event.mDagvergunningCount == -1) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_dagvergunningen_fetch_failed) + ": " + event.mMessage);
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