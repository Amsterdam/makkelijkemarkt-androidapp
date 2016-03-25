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
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetDagvergunningen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * A tasktimer object that will fetch the dagvergunningen for selected markt from the api, if we
 * have a network, and if a markt is selected
 * @author marcolangebeeke
 */
public class TimerTaskGetDagvergunningen extends TimerTask {

    // use classname when logging
    private static final String LOG_TAG = TimerTaskGetDagvergunningen.class.getSimpleName();

    // the context we are constructed from
    private Context mContext;

    /**
     * Constructor
     * @param context from where we're called
     */
    public TimerTaskGetDagvergunningen(Context context) {
        mContext = context;
    }

    /**
     * Runnable
     */
    @Override
    public void run() {

        Log.i(LOG_TAG, "=========> Get dagvergunningen!");

        // get the markt id from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        int marktId = settings.getInt(mContext.getString(R.string.sharedpreferences_key_markt_id), 0);

        if (marktId > 0 && Utility.isNetworkAvailable(mContext)) {

            // get the date of today for the dag param
            SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.date_format_dag));
            String dag = sdf.format(new Date());

            // fetch dagvergunningen for selected markt
            ApiGetDagvergunningen getDagvergunningen = new ApiGetDagvergunningen(mContext);
            getDagvergunningen.setMarktId(String.valueOf(marktId));
            getDagvergunningen.setDag(dag);
            getDagvergunningen.enqueue();

        }
    }
}