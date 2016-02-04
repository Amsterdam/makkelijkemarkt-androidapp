/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the title and subtitle in the toolbar
        setToolbarTitle(getString(R.string.about));

        // add the about fragment to the container
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, new AboutFragment());
            transaction.commit();
        }
    }
}