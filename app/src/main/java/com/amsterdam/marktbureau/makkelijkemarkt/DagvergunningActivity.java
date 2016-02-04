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
        setToolbarTitle(getString(R.string.dagvergunningen));
        setToolbarSubtitle(marktNaam);

        // add the about fragment to the container
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, new DagvergunningFragment());
            transaction.commit();
        }
    }
}