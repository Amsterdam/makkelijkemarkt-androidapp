/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 *
 * @author marcolangebeeke
 */
public class MarktenActivity extends BaseActivity {

    // use classname when logging
    private static final String LOG_TAG = MarktenActivity.class.getSimpleName();

    // create unique marktenfragent instance tag
    private static final String MARKTEN_FRAGMENT_TAG = LOG_TAG + MarktenFragment.class.getSimpleName() + "_TAG";

    // the marktenfragment
    private MarktenFragment mMarktenFragment;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title in the toolbar
        setToolbarTitle(getString(R.string.markten));

        // add the markten fragment to the container
        if (savedInstanceState == null) {
            mMarktenFragment = new MarktenFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mMarktenFragment,
                    MARKTEN_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mMarktenFragment = (MarktenFragment) getSupportFragmentManager().findFragmentByTag(
                    MARKTEN_FRAGMENT_TAG);
        }
    }
}
