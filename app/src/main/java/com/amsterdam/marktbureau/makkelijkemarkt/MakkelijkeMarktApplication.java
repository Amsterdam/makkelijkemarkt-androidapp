/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 *
 * @author marcolangebeeke
 */
public class MakkelijkeMarktApplication extends Application {

    private static GoogleAnalytics mAnalytics;

    private static Tracker mTracker;

    public static GoogleAnalytics analytics() {
        return mAnalytics;
    }

    public static Tracker tracker() {
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAnalytics = GoogleAnalytics.getInstance(this);

        // create tracker with our tracker id
        mTracker = mAnalytics.newTracker("UA-75939009-1");

        // provide unhandled exceptions reports
        mTracker.enableExceptionReporting(true);

        // Enable automatic activity tracking for your app
        mTracker.enableAutoActivityTracking(true);

        // Enable automatic activity tracking for your app
        mTracker.enableAutoActivityTracking(true);
    }
}