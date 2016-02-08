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
public class DagvergunningActivity extends BaseActivity implements
        DagvergunningFragmentKoopman.OnReadyListener,
        DagvergunningFragmentProduct.OnReadyListener,
        DagvergunningFragmentOverzicht.OnReadyListener {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningActivity.class.getSimpleName();

    // create unique dagvergunningfragent instance tag
    private static final String DAGVERGUNNING_FRAGMENT_TAG = LOG_TAG + DagvergunningFragment.class.getSimpleName() + "_TAG";

    // the dagvergunningfragment
    private DagvergunningFragment mDagvergunningFragment;

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

        // create new or get existing instance of dagvergunningfragment
        if (savedInstanceState == null) {
            mDagvergunningFragment = new DagvergunningFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mDagvergunningFragment,
                    DAGVERGUNNING_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mDagvergunningFragment = (DagvergunningFragment) getSupportFragmentManager().findFragmentByTag(
                    DAGVERGUNNING_FRAGMENT_TAG);
        }
    }

    /**
     * DagvergunningFragmentKoopman callback to inform the dagvergunningfragment that it's ready
     */
    public void onKoopmanFragmentReady() {
        mDagvergunningFragment.koopmanFragmentReady();
    }

    /**
     * DagvergunningFragmentProduct callback to inform the dagvergunningfragment that it's ready
     */
    public void onProductFragmentReady() {
        mDagvergunningFragment.productFragmentReady();
    }

    /**
     * DagvergunningFragmentOverzicht callback to inform the dagvergunningfragment that it's ready
     */
    public void onOverzichtFragmentReady() {
        mDagvergunningFragment.overzichtFragmentReady();
    }
}