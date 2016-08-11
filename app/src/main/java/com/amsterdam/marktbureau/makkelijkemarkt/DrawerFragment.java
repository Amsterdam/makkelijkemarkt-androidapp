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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author marcolangebeeke
 */
public class DrawerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = DrawerFragment.class.getSimpleName();

    // constants linking the position of the drawer menu to an activity
    public final int DRAWER_POSITION_MARKTEN = 0;
    public final int DRAWER_POSITION_DAGVERGUNNINGEN = 1;
    public final int DRAWER_POSITION_NOTITIES = 2;

    // unique id for the uitgegeven dagvergunningen loader
    private static final int DAGVERGUNNINGEN_LOADER = 7;

    // bind layout elements
    @Bind(R.id.drawer_account_naam) TextView mAccountNaam;
    @Bind(R.id.drawer_menu) ListView mDrawerMenu;
    @Bind(R.id.dagvergunningen_uitgegeven) TextView mDagvergunningenUitgegeven;
    @Bind(R.id.date_today) TextView mDateToday;

    /**
     * Constructor
     */
    public DrawerFragment() {
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

        // get the drawer fragment
        View view = inflater.inflate(R.layout.drawer_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // get the account naam from the shared prefs
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        mAccountNaam.setText(settings.getString(getContext().getString(R.string.sharedpreferences_key_account_naam), ""));

        // get the date of today
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_datum_dag));
        mDateToday.setText(Utility.capitalize(sdf.format(new Date())));

        // populate the drawer menu options
        mDrawerMenu.setAdapter(new ArrayAdapter<>(getContext(),
                R.layout.drawer_fragment_list_item,
                android.R.id.text1,
                new String[]{
                        getString(R.string.markten),
                        getString(R.string.dagvergunningen),
                        getString(R.string.notities)}));

        // initiate loading the dagvergunningen from the database
        getLoaderManager().initLoader(DAGVERGUNNINGEN_LOADER, null, this);

        return view;
    }

    /**
     * Set the active state of the drawer menu
     * @param position which position to activate
     */
    public void checkItem(int position) {
        mDrawerMenu.setItemChecked(position, true);
    }

    /**
     * Handle clicks on the drawer menuitems for selecting the corresponding activity
     * @param position the clicked menuitem position
     */
    @OnItemClick(R.id.drawer_menu)
    public void selectItem(int position) {
        Intent intent;

        switch (position) {
            case DRAWER_POSITION_MARKTEN:
                intent = new Intent(getActivity(), MarktenActivity.class);
                startActivity(intent);
                break;
            case DRAWER_POSITION_DAGVERGUNNINGEN:
                intent = new Intent(getActivity(), DagvergunningenActivity.class);
                startActivity(intent);
                break;
            case DRAWER_POSITION_NOTITIES:
                intent = new Intent(getActivity(), NotitiesActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * Create a loader to get the total amount of dagvergunnigen for today
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // get the id of selected markt from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

        // get the date of today for the dag param
        SimpleDateFormat dagSdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        String dag = dagSdf.format(new Date());

        // create the loader
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriDagvergunning);
        loader.setSelection(
                MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? AND " +
                        MakkelijkeMarktProvider.Dagvergunning.COL_DAG + " = ? "
        );
        loader.setSelectionArgs(new String[] {
                String.valueOf(marktId),
                dag
        });

        return loader;
    }

    /**
     * Populate the drawer textview with the dagvergunning count, if greater than 0
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mDagvergunningenUitgegeven.setText(getString(R.string.drawer_permit_count_label) + ": " + data.getCount() + " totaal");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}