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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

/**
 * @author marcolangebeeke
 */
public class NotitieActivity extends BaseActivity {

    // use classname when logging
    private static final String LOG_TAG = NotitieActivity.class.getSimpleName();

    // create unique notitiefragment instance tag
    private static final String NOTITIE_FRAGMENT_TAG = LOG_TAG + NotitieFragment.class.getSimpleName() + "_TAG";

    // the notitiefragment
    private NotitieFragment mNotitieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title in the toolbar
        Intent intent = getIntent();
        if ((intent != null) && (intent.hasExtra(MakkelijkeMarktProvider.mTableNotitie +
                MakkelijkeMarktProvider.Notitie.COL_ID))) {
            setToolbarTitle(getString(R.string.notitie_edit));
        } else {
            setToolbarTitle(getString(R.string.notitie_add));
        }

        // get selected markt naam from sharedpreferences and set the subtitle in the toolbar
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String marktNaam = settings.getString(getString(R.string.sharedpreferences_key_markt_naam), "");
        setToolbarSubtitle(marktNaam);

        // create new or get existing instance of notitiefragment
        if (savedInstanceState == null) {
            mNotitieFragment = new NotitieFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(
                    R.id.container,
                    mNotitieFragment,
                    NOTITIE_FRAGMENT_TAG);
            transaction.commit();
        } else {
            mNotitieFragment = (NotitieFragment) getSupportFragmentManager().findFragmentByTag(
                    NOTITIE_FRAGMENT_TAG);
        }

        // set the active drawer menu option
        if (mDrawerFragment.isAdded()) {
            mDrawerFragment.checkItem(mDrawerFragment.DRAWER_POSITION_NOTITIES);
        }

        // replace the drawer hamburger with the back-arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}