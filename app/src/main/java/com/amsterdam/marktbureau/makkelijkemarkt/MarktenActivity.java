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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class MarktenActivity extends AppCompatActivity {

    // use classname when logging
    private static final String LOG_TAG = MarktenActivity.class.getSimpleName();

    // create unique marktenfragent instance tag
    private static final String MARKTEN_FRAGMENT_TAG = LOG_TAG + MarktenFragment.class.getSimpleName() + "_TAG";

    // create unique update dialog fragment instance tag
    private static final String UPDATE_FRAGMENT_TAG = LOG_TAG + UpdateAvailableDialogFragment.class.getSimpleName() + "_TAG";

    // bind layout elements
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mTitleView;

    // the marktenfragment
    private MarktenFragment mMarktenFragment;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the base activity layout containing the toolbar
        setContentView(R.layout.markten_activity);

        // bind the elements to the view
        ButterKnife.bind(this);

        // set the toolbar as supportactionbar, with default title disabled and homebutton enabled
        setSupportActionBar(mToolbar);

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

    /**
     * Add the actions menu to the actionbar
     * @param menu the menu object to create the options in
     * @return always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu options as defined in the private actions menu xml
        getMenuInflater().inflate(R.menu.private_actions_menu, menu);

        return true;
    }

    /**
     * Handle option menu item selection
     * @param item MenuItem
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // log the user out
        if (id == R.id.action_logout) {
            Utility.logout(this, true);
            return true;
        }

        // check if there is a newer build of the app available
        if (id == R.id.action_check_update) {
            Utility.checkForUpdate(this, UPDATE_FRAGMENT_TAG, false);
            return true;
        }

        // open the about activity
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutPrivateActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the title in the textview of the custom toolbar layout
     * @param title string containing the title
     */
    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            mTitleView.setText(title);
        }
    }

    /**
     * On start of the activity log activity timestamp
     */
    @Override
    public void onStart() {
        super.onStart();

        // keep track of app activity
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(
                getString(R.string.sharedpreferences_key_app_activity_timestamp),
                new Date().getTime());
        editor.apply();
    }
}