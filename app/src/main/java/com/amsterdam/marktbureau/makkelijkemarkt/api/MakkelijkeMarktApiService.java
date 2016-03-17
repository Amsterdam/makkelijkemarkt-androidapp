/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    // timer object for checking app activity
    private Timer mAppActivityTimer;
    private int mCheckAppActivityInterval;

    /**
     * Called only once, upon initial creation
     */
    @Override
    public void onCreate() {

        // create a timer instance for loading the dagvergunningen on interval
        mGetDagvergunningenTimer = new Timer();
        mGetDagvergunningenInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_service_getdagvergunningen_interval_seconds) * 1000;

        // create a timer instance for loading the notities on interval
        mGetNotitiesTimer = new Timer();
        mGetNotitiesInterval = getResources().getInteger(
                R.integer.makkelijkemarkt_service_getnotities_interval_seconds) * 1000;

        // create a timer instance for checking app activity on interval
        mAppActivityTimer = new Timer();
        mCheckAppActivityInterval = getResources().getInteger(
                R.integer.app_activity_check_interval_seconds) * 1000;

        super.onCreate();
    }

    /**
     * Called every time the service is started with startService
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // only started if we are not started yet, or the service was stopped by the system
        if (!mIsStarted || intent == null) {
            if (intent == null) {
                Log.i(LOG_TAG, "=========> service intent = null, were we restarted after a shutdown by the system?");
            }

            // update state to know we are started and should not be started again
            mIsStarted = true;

            // get final reference to the context
            final Context ctx = getApplicationContext();

            // create a timer task that will start the first time after the given interval and
            // execute on the same interval afterwards
            mGetDagvergunningenTimer.scheduleAtFixedRate(new TimerTask() {
                /**
                 * Start a seperate thread that will retrieve the dagvergunningen
                 */
                @Override
                public void run() {

                    Log.i(LOG_TAG, "=========> Get dagvergunningen!");

                    // get the markt id from the shared preferences
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
                    int marktId = settings.getInt(ctx.getString(R.string.sharedpreferences_key_markt_id), 0);

                    if (Utility.isNetworkAvailable(ctx) && marktId > 0) {

                        // get the date of today for the dag param
                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
                        String dag = sdf.format(new Date());

                        // fetch dagvergunningen for selected markt
                        ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(ctx);
                        getDagvergunningen.setMarktId(String.valueOf(marktId));
                        getDagvergunningen.setDag(dag);
                        getDagvergunningen.enqueue();

                    }
                }
            }, mGetDagvergunningenInterval, mGetDagvergunningenInterval);

            // create a timer task that will start the first time after the given interval and
            // execute on the same interval afterwards
            mGetNotitiesTimer.scheduleAtFixedRate(new TimerTask() {
                /**
                 * Start a seperate thread that will retrieve the notities
                 */
                @Override
                public void run() {

                    Log.i(LOG_TAG, "=========> Get notities!");

                    // get the markt id from the shared preferences
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
                    int marktId = settings.getInt(ctx.getString(R.string.sharedpreferences_key_markt_id), 0);

                    if (Utility.isNetworkAvailable(ctx) && marktId > 0) {

                        // get the date of today for the dag param
                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
                        String dag = sdf.format(new Date());

                        // fetch notities for selected markt
                        ApiGetNotities getNotities = new ApiGetNotities(ctx);
                        getNotities.setMarktId(String.valueOf(marktId));
                        getNotities.setDag(dag);
                        getNotities.enqueue();

                    }
                }
            }, mGetNotitiesInterval, mGetNotitiesInterval);

            // create a timertask that will check if the app is actively being used and if not
            // logout the user after a certain amount of time
            mAppActivityTimer.scheduleAtFixedRate(new TimerTask() {
                /**
                 * Start a seperate thread that will check the app activity
                 */
                @Override
                public void run() {

                    Log.i(LOG_TAG, "=========> Checking app activity");

                    // get the latest app activity timestamp from the shared preferences
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
                    long appActivityTimestamp = settings.getLong(ctx.getString(R.string.sharedpreferences_key_app_activity_timestamp), 0);
                    long differenceMs  = new Date().getTime() - appActivityTimestamp;
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceMs);

                    // check if inactivity is longer than timeout
                    if (appActivityTimestamp > 0 && diffInMinutes >= getResources().getInteger(R.integer.app_inactivity_logout_timeout_minutes)) {

                        // logout
                        Utility.logout(ctx, false);
                    }
                }
            }, mCheckAppActivityInterval, mCheckAppActivityInterval);

            // TODO: add a timertask that will download the sollicitaties:
            // - on a 1 hour interval
            // - for each markt
            // - that has not yet been downloaded in the last 12 hours
            // - only if we are on wifi

        }

        // return sticky so in case the service is shutdown by the system it will be restarted with
        // an empty intent that we can check on to see if we need to restart the timer task
        return START_STICKY;
    }

    /**
     * On destroy cancel the timer top stop calling the api and set the started indicator to false
     */
    @Override
    public void onDestroy() {

        // reset the started indicator
        mIsStarted = false;

        // stop the timers
        mGetDagvergunningenTimer.cancel();
        mGetNotitiesTimer.cancel();
        mAppActivityTimer.cancel();

        super.onDestroy();
    }

    /**
     * Called every time the service is started with bindService
     */
    @Override
    public IBinder onBind(Intent intent) {
        // not used
        return null;
    }
}