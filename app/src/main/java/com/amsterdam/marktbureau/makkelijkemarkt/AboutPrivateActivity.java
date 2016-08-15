/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 * @author marcolangebeeke
 */
public class AboutPrivateActivity extends BaseActivity {

    // use classname when logging
    private static final String LOG_TAG = AboutPrivateActivity.class.getSimpleName();

    // create unique aboutfragment instance tag
    private static final String ABOUT_FRAGMENT_TAG = LOG_TAG + AboutFragment.class.getSimpleName() + "_TAG";

    // the aboutfragment
    private AboutFragment mAboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title and subtitle in the toolbar
        setToolbarTitle(getString(R.string.about));

        // create new or get existing instance of aboutfragment
        if (savedInstanceState == null) {
            mAboutFragment = new AboutFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mAboutFragment,
                    ABOUT_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mAboutFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag(
                    ABOUT_FRAGMENT_TAG);
        }
    }
}