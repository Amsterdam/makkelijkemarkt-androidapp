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
public class NotitiesActivity extends BaseActivity {

    // use classname when logging
    private static final String LOG_TAG = NotitiesActivity.class.getSimpleName();

    // create unique notitiesfragment instance tag
    private static final String NOTITIES_FRAGMENT_TAG = LOG_TAG + NotitiesFragment.class.getSimpleName() + "_TAG";

    // the notitiesfragment
    private NotitiesFragment mNotitiesFragment;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get selected markt naam from sharedpreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");

        // set the title and subtitle in the toolbar
        setToolbarTitle(getString(R.string.notities));
        setToolbarSubtitle(marktNaam);

        // add the about fragment to the container
        if (savedInstanceState == null) {
            mNotitiesFragment = new NotitiesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mNotitiesFragment,
                    NOTITIES_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mNotitiesFragment = (NotitiesFragment) getSupportFragmentManager().findFragmentByTag(
                    NOTITIES_FRAGMENT_TAG);
        }

        // set the active drawer menu option
        if (mDrawerFragment.isAdded()) {
            mDrawerFragment.checkItem(mDrawerFragment.DRAWER_POSITION_NOTITIES);
        }
    }
}