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

    /**
     * Create the Google Analytics tracker
     */
    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

        // create tracker with our tracker id
        Tracker tracker = analytics.newTracker("UA-75939009-1");

        // provide unhandled exceptions reports
        tracker.enableExceptionReporting(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);
    }
}