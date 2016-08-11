/**
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetSollicitaties;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * A tasktimer object that will download the sollicitaties that have been downloaded before, but
 * have not been downloaded in the last 12 hours
 * @author marcolangebeeke
 */
public class TimerTaskGetSollicitaties extends TimerTask {

    // use classname when logging
    private static final String LOG_TAG = TimerTaskGetSollicitaties.class.getSimpleName();

    // the context we are constructed from
    private Context mContext;

    // a list of all markt ids
    private ArrayList<Integer> mMarktIds = new ArrayList<>();

    /**
     * Constructor
     * @param context from where we're called
     */
    public TimerTaskGetSollicitaties(Context context) {

        // set the context
        mContext = context;

        // load the markten from the db
        Cursor markten = mContext.getContentResolver().query(
                MakkelijkeMarktProvider.mUriMarkt,
                new String[] { MakkelijkeMarktProvider.Markt.COL_ID },
                null,
                null,
                MakkelijkeMarktProvider.Markt.COL_NAAM + " ASC");

        // get the markt ids and close the cursor
        if (markten != null) {
            while (markten.moveToNext()) {
                mMarktIds.add(markten.getInt(markten.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_ID)));
            }
            markten.close();
        }
    }

    /**
     * Runnable
     */
    @Override
    public void run() {

        Log.i(LOG_TAG, "=========> Get sollicitaties");

        int marktId = -1;

        // loop through all markten
        for (int id : mMarktIds) {

            // check if sollicitaties have been downloaded for markt before
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (preferences.contains(mContext.getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + id)) {

                // check if sollicitaties have been loaded in the last 12 hours
                if (Utility.isTimedOut(mContext,
                        mContext.getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + id,
                        R.integer.makkelijkemarkt_api_markten_fetch_interval_hours)) {

                    // this is the markt we want to be downloading
                    marktId = id;
                    break;
                }
            }
        }

        // found a markt to download the sollicitaties for
        if (marktId != -1 && Utility.isNetworkAvailable(mContext)) {
            ApiGetSollicitaties getSollicitaties = new ApiGetSollicitaties(mContext);
            getSollicitaties.setMarktId(marktId);
            getSollicitaties.enqueue();
        }
    }
}