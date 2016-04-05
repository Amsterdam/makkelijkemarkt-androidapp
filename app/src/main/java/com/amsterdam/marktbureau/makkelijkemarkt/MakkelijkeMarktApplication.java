/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
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