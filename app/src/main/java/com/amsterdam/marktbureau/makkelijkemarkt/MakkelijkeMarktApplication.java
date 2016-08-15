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

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Makkelijke Markt Application object
 * @author marcolangebeeke
 */
public class MakkelijkeMarktApplication extends Application {

    // google analytics object
    private static GoogleAnalytics mAnalytics;

    // google analytics tracker object
    private static Tracker mTracker;

    /**
     * Create the Google Analytics tracker
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // instantiate the google analytics object
        GoogleAnalytics mAnalytics = GoogleAnalytics.getInstance(this);

        // create the tracker with our tracker configuration
        if (mTracker == null) {
            Tracker mTracker = mAnalytics.newTracker(R.xml.analytics_tracker_config);
        }
    }
}