/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetSollicitaties;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    // unique id for the markten loader
    private static final int DAGVERGUNNINGEN_LOADER = 3;

    // bind layout elements
    @Bind(R.id.listview_dagvergunningen) ListView mDagvergunningenListView;
    @Bind(R.id.progressbar_dagvergunningen) ProgressBar mDagvergunningenProgressBar;
    @Bind(R.id.listview_empty) TextView mListViewEmptyTextView;

    // the id of the selected markt
    private int mMarktId;

    // the date of today in a formatted string
    private String mDag;

    // cursoradapter for populating the dagvergunningen litsview with dagvergunningen from the database
    private DagvergunningenAdapter mDagvergunningenAdapter;

    // progress dialog for during retrieving sollicitaties
    private ProgressDialog mGetSollicitatiesProcessDialog;

    // common toast object
    protected Toast mToast;

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

        // create progress dialog for loading the sollicitaties
        mGetSollicitatiesProcessDialog = new ProgressDialog(getContext());
        mGetSollicitatiesProcessDialog.setIndeterminate(true);
        mGetSollicitatiesProcessDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_circle));
        mGetSollicitatiesProcessDialog.setMessage(getString(R.string.notice_sollicitaties_loading) + "...");
        mGetSollicitatiesProcessDialog.setCancelable(false);

        if (savedInstanceState == null) {

            // @todo remove api call here and only call on interval basis in the service?
            // @todo or keep it here, so we can show a progressbar and start calling it on interval basis in the service once this call is finished?
            // @todo best way would be to call it here one time, so we use the progressbar, and on interval basis call it in the service
            // @todo: dagvergunningen: 1x per minute?

            // show the progressbar
            mDagvergunningenProgressBar.setVisibility(View.VISIBLE);

            // fetch dagvergunningen for selected markt
            ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(getContext());
            getDagvergunningen.setMarktId(String.valueOf(mMarktId));
            getDagvergunningen.setDag(mDag);
            getDagvergunningen.enqueue();

            // check time in hours since last fetched the sollicitaties for selected markt
            long diffInHours = getResources().getInteger(R.integer.makkelijkemarkt_api_sollicitaties_fetch_interval_hours);
            if (settings.contains(getContext().getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + mMarktId)) {
                long lastFetchTimestamp = settings.getLong(getContext().getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + mMarktId, 0);
                long differenceMs  = new Date().getTime() - lastFetchTimestamp;
                diffInHours = TimeUnit.MILLISECONDS.toHours(differenceMs);
            }

            // if last sollicitaties fetched more than 12 hours ago, fetch them again
            if (diffInHours >= getResources().getInteger(R.integer.makkelijkemarkt_api_sollicitaties_fetch_interval_hours)) {
                ApiGetSollicitaties getSollicitaties = new ApiGetSollicitaties(getContext());
                getSollicitaties.setMarktId(mMarktId);
                getSollicitaties.enqueue();

                // show progress dialog
                mGetSollicitatiesProcessDialog.show();
            }
        }

        // create an adapter for the dagvergunningen listview
        mDagvergunningenAdapter = new DagvergunningenAdapter(getActivity(), null, 0);
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
     * Handle response event from api get sollicitaties request completed to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onGetSollicitatiesCompletedEvent(ApiGetSollicitaties.OnCompletedEvent event) {

        // hide progress dialog
        mGetSollicitatiesProcessDialog.dismiss();
        if (event.mSollicitatiesCount == -1) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_sollicitaties_fetch_failed) + ": " + event.mMessage);
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