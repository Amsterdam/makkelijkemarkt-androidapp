/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningActivity extends BaseActivity {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningActivity.class.getSimpleName();

    // the dagvergunningfragment
    DagvergunningFragment mDagvergunningFragment;

    /**
     * Get markt naam from the shared prefs, set the title and subtile, and instantiate the
     * dagvergunning fragment
     * @param savedInstanceState the saved activity statew
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get selected markt naam from sharedpreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");

        // set the title and subtitle in the toolbar
        setToolbarTitle(getString(R.string.dagvergunning));
        setToolbarSubtitle(marktNaam);

        // TODO: Instantiate fragments the same way in the other activities

        // create new or get existing instance of dagvergunningfragment
        if (savedInstanceState == null) {
            mDagvergunningFragment = new DagvergunningFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mDagvergunningFragment,
                    DagvergunningActivity.class.getSimpleName() + DagvergunningFragment.class.getSimpleName());
            transaction.commit();
        } else {
            mDagvergunningFragment = (DagvergunningFragment) getSupportFragmentManager().findFragmentByTag(
                    DagvergunningActivity.class.getSimpleName() + DagvergunningFragment.class.getSimpleName());
        }
    }
}