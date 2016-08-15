/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Check if the user has been active in a given timeout, and if not logout the user from the app
 * @author marcolangebeeke
 */
public class TimerTaskCheckUserActivity extends TimerTask {

    // use classname when logging
    private static final String LOG_TAG = TimerTaskCheckUserActivity.class.getSimpleName();

    // the context we are constructed from
    private Context mContext;

    /**
     * Constructor
     * @param context from where we're called
     */
    public TimerTaskCheckUserActivity(Context context) {
        mContext = context;
    }

    /**
     * Runnable
     */
    @Override
    public void run() {

        Log.i(LOG_TAG, "=========> Checking app activity");

        // get the latest app activity timestamp from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        long appActivityTimestamp = settings.getLong(mContext.getString(R.string.sharedpreferences_key_app_activity_timestamp), 0);
        long differenceMs  = new Date().getTime() - appActivityTimestamp;
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceMs);

        // check if inactivity is longer than timeout
        if (appActivityTimestamp > 0 && diffInMinutes >= mContext.getResources().getInteger(R.integer.app_inactivity_logout_timeout_minutes)) {

            // logout
            Utility.logout(mContext, false);
        }
    }
}