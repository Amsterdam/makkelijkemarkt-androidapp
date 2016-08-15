/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.amsterdam.marktbureau.makkelijkemarkt.tasks.TimerTaskCheckUserActivity;
import com.amsterdam.marktbureau.makkelijkemarkt.tasks.TimerTaskGetDagvergunningen;
import com.amsterdam.marktbureau.makkelijkemarkt.tasks.TimerTaskGetNotities;
import com.amsterdam.marktbureau.makkelijkemarkt.tasks.TimerTaskGetSollicitaties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

/**
 * Service that will call the Makkelijke Markt Api on certain intervals
 * @author marcolangebeeke
 */
public class MakkelijkeMarktApiService extends Service {

    // use classname when logging
    private static final String LOG_TAG = MakkelijkeMarktApiService.class.getSimpleName();

    // keep a state indicating the service is started
    private boolean mIsStarted = false;

    // timer object for loading the dagvergunningen
    private Timer mGetDagvergunningenTimer;
    private int mGetDagvergunningenInterval;

    // timer object for loading the notities
    private Timer mGetNotitiesTimer;
    private int mGetNotitiesInterval;

    // timer object for loading the sollicitaties
    private Timer mGetSollicitatiesTimer;
    private int mGetSollicitatiesStartDelay;
    private int mGetSollicitatiesInterval;

    // timer object for checking app activity
    private Timer mCheckUserActivityTimer;
    private int mCheckUserActivityInterval;

    /**
     * Called only once, upon initial creation
     */
    @Override
    public void onCreate() {

        // create a timer instance for loading the dagvergunningen on interval
        mGetDagvergunningenTimer = new Timer();
        mGetDagvergunningenInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_api_service_getdagvergunningen_interval_seconds) * 1000;

        // create a timer instance for loading the notities on interval
        mGetNotitiesTimer = new Timer();
        mGetNotitiesInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_api_service_getnotities_interval_seconds) * 1000;

        // create a timer instance for loading the sollicitaties on interval
        mGetSollicitatiesTimer = new Timer();
        mGetSollicitatiesStartDelay = getResources().getInteger(
                R.integer.makkelijkemarkt_api_service_getsollicitaties_startdelay_seconds) * 1000;
        mGetSollicitatiesInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_api_service_getsollicitaties_interval_seconds) * 1000;

        // create a timer instance for checking user activity
        mCheckUserActivityTimer = new Timer();
        mCheckUserActivityInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_api_service_appactivitycheck_interval_seconds) * 1000;

        super.onCreate();
    }

    /**
     * Called every time the service is started with startService
     * @param intent intent given when starting the service
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // only started if we are not started yet, or the service was stopped by the system
        if (!mIsStarted || intent == null) {
            if (intent == null) {
                Log.i(LOG_TAG, "=========> service intent = null, were we restarted after a shutdown by the system?");
            }

            // update started state to know we are started and should not be started again
            mIsStarted = true;

            // get final reference to the context
            final Context context = getApplicationContext();

            // get the date of today for the dag param
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
            String mDagToday = sdf.format(new Date());

            // delete all dagvergunningen older than today from local database
            context.getContentResolver().delete(
                    MakkelijkeMarktProvider.mUriDagvergunning,
                    MakkelijkeMarktProvider.Dagvergunning.COL_DAG + " <> ? ",
                    new String[] { mDagToday });

            // delete all notities older than today from local database
            context.getContentResolver().delete(
                    MakkelijkeMarktProvider.mUriNotitie,
                    MakkelijkeMarktProvider.Notitie.COL_DAG + " <> ? ",
                    new String[] { mDagToday });

            // create a timer task that will retrieve the notities on given interval
            mGetDagvergunningenTimer.scheduleAtFixedRate(
                    new TimerTaskGetDagvergunningen(context),
                    mGetDagvergunningenInterval,
                    mGetDagvergunningenInterval);

            // create a timer task that will retrieve the notities on given interval
            mGetNotitiesTimer.scheduleAtFixedRate(
                    new TimerTaskGetNotities(context),
                    mGetNotitiesInterval,
                    mGetNotitiesInterval);

            // add a tasktimer that will download the sollicitaties that have been downloaded before,
            // but have not been downloaded in the last 12 hours
            mGetSollicitatiesTimer.scheduleAtFixedRate(
                    new TimerTaskGetSollicitaties(context),
                    mGetSollicitatiesStartDelay,
                    mGetSollicitatiesInterval);

            // create a timertask that will check the app activity and logout the user after a timeout
            mCheckUserActivityTimer.scheduleAtFixedRate(
                    new TimerTaskCheckUserActivity(context),
                    mCheckUserActivityInterval,
                    mCheckUserActivityInterval);
        }

        // return sticky so in case the service is shutdown by the system it will be restarted with
        // an empty intent that we can check to see if we need to restart the timer task
        return START_STICKY;
    }

    /**
     * On destroy cancel the timers to stop calling the api and set the started indicator to false
     */
    @Override
    public void onDestroy() {

        // reset the started indicator
        mIsStarted = false;

        // cancel the timers
        mGetDagvergunningenTimer.cancel();
        mGetNotitiesTimer.cancel();
        mGetSollicitatiesTimer.cancel();
        mCheckUserActivityTimer.cancel();

        super.onDestroy();
    }

    /**
     * Called every time the service is started with bindService (not used in our case)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // not used
        return null;
    }
}