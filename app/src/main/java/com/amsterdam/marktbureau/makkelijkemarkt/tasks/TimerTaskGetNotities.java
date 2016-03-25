/**
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetNotities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * A tasktimer object that will fetch the notities for selected markt from the api, if we have a
 * network, and if a markt is selected
 * @author marcolangebeeke
 */
public class TimerTaskGetNotities extends TimerTask {

    // use classname when logging
    private static final String LOG_TAG = TimerTaskGetNotities.class.getSimpleName();

    // the context we are constructed from
    private Context mContext;

    /**
     * Constructor
     * @param context from where we're called
     */
    public TimerTaskGetNotities(Context context) {
        mContext = context;
    }

    /**
     * Runnable
     */
    @Override
    public void run() {

        Log.i(LOG_TAG, "=========> Get notities!");

        // get the markt id from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        int marktId = settings.getInt(mContext.getString(R.string.sharedpreferences_key_markt_id), 0);

        if (marktId > 0 && Utility.isNetworkAvailable(mContext)) {

            // get the date of today for the dag param
            SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.date_format_dag));
            String dag = sdf.format(new Date());

            // fetch notities for selected markt
            ApiGetNotities getNotities = new ApiGetNotities(mContext);
            getNotities.setMarktId(String.valueOf(marktId));
            getNotities.setDag(dag);
            getNotities.enqueue();
        }
    }
}