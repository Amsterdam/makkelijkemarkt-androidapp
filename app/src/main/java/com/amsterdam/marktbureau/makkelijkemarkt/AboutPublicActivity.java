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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class AboutPublicActivity extends AppCompatActivity {

    // use classname when logging
    private static final String LOG_TAG = AboutPublicActivity.class.getSimpleName();

    // create unique aboutfragment instance tag
    private static final String ABOUT_FRAGMENT_TAG = LOG_TAG + AboutFragment.class.getSimpleName() + "_TAG";

    // the aboutfragment
    private AboutFragment mAboutFragment;

    // bind layout elements
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mTitleView;

    /**
     * Set the about_activity layout and add the aboutfragment
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout
        setContentView(R.layout.about_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // set the toolbar as supportactionbar, with default title disabled and homebutton enabled
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // set the title in the toolbar
            mTitleView.setText(R.string.about);
        }

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