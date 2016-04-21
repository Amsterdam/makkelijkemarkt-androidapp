/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningActivity extends BaseActivity implements
        DagvergunningFragmentKoopman.Callback,
        DagvergunningFragmentProduct.Callback,
        DagvergunningFragmentOverzicht.Callback {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningActivity.class.getSimpleName();

    // create unique dagvergunningfragent instance tag
    private static final String DAGVERGUNNING_FRAGMENT_TAG = LOG_TAG + DagvergunningFragment.class.getSimpleName() + "_TAG";

    // the dagvergunningfragment
    private DagvergunningFragment mDagvergunningFragment;

    /**
     * Get markt naam from the shared prefs, set the title and subtitle, and instantiate the
     * dagvergunning fragment
     * @param savedInstanceState the saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title in the toolbar
        Intent intent = getIntent();
        if ((intent != null) && (intent.hasExtra(MakkelijkeMarktProvider.mTableDagvergunning +
                MakkelijkeMarktProvider.Dagvergunning.COL_ID))) {
            setToolbarTitle(getString(R.string.dagvergunning_edit));
        } else {
            setToolbarTitle(getString(R.string.dagvergunning_add));
        }

        // get selected markt naam from sharedpreferences and set the subtitle in the toolbar
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");
        setToolbarSubtitle(marktNaam);

        // create new or get existing instance of dagvergunningfragment
        if (savedInstanceState == null) {
            mDagvergunningFragment = new DagvergunningFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mDagvergunningFragment, DAGVERGUNNING_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mDagvergunningFragment = (DagvergunningFragment) getSupportFragmentManager().findFragmentByTag(
                    DAGVERGUNNING_FRAGMENT_TAG);
        }

        // set the active drawer menu option
        if (mDrawerFragment.isAdded()) {
            mDrawerFragment.checkItem(mDrawerFragment.DRAWER_POSITION_DAGVERGUNNINGEN);
        }

        // replace the drawer hamburger with the back-arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * DagvergunningFragmentKoopman callback to inform the dagvergunningfragment that it's ready
     */
    public void onKoopmanFragmentReady() {
        mDagvergunningFragment.koopmanFragmentReady();
    }

    /**
     * DagvergunningFragmentKoopman callback to retrieve the changed koopman fragment data and
     * populate it again based on the new data
     */
    public void onKoopmanFragmentUpdated() {
        mDagvergunningFragment.getAndSetKoopmanFragmentValues();
    }

    /**
     * DagvergunningFragmentKoopman callback to update meldingen based on loaded koopman
     */
    public void onMeldingenUpdated() {
        mDagvergunningFragment.populateMeldingen();
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

    /**
     * Set the visibility of the progressbar in the dagvergunningfragment
     * @param visibility the visibility as View.VISIBLE | View.GONE | View.INVISIBLE
     */
    public void setProgressbarVisibility(int visibility){
        mDagvergunningFragment.setProgressbarVisibility(visibility);
    }
}