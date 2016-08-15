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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiSollicitatie;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiVervanger;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiGetSollicitaties extends ApiCall implements Callback<List<ApiSollicitatie>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetSollicitaties.class.getSimpleName();

    // call parameters
    private int mMarktId = -1;
    private int mListOffset = 0;
    private int mListLength = 1000;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetSollicitaties(Context context) {
        super(context);
    }

    /**
     * Set the id of the markt we want the sollicitaties for
     * @param marktId id of the markt
     */
    public void setMarktId(int marktId) {
        mMarktId = marktId;
    }

    /**
     * Enqueue an async call to load the accounts
     */
    @Override
    public boolean enqueue() {
        if (super.enqueue()) {
            if (mMarktId != -1) {

                // set the api function to call for loading the sollicitaties
                Call<List<ApiSollicitatie>> call = mMakkelijkeMarktApi.getSollicitaties(
                        String.valueOf(mMarktId),
                        String.valueOf(mListOffset),
                        String.valueOf(mListLength));

                // call the api asynchronously
                call.enqueue(this);

            } else {
                Utility.log(mContext, LOG_TAG, "Call failed, markt id not set!");
            }

            return true;
        }

        return false;
    }

    /**
     * When we receive the response from the api we store the sollicitaties in the database and
     * optionally we call again for more
     * @param response api response containing a list of ApiSollicitatie objects
     */
    @Override
    public void onResponse(Response<List<ApiSollicitatie>> response) {
        if (response.body() != null) {
            if (response.body().size() > 0) {

                // get http headers from response
                Headers headers = response.headers();

                // get listsize header
                if (headers.get(mContext.getString(R.string.makkelijkemarkt_api_x_listsize_header_name)) != null) {
                    try {

                        // if there is still more to fetch, increase the offset and enqueue again
                        int totalListSize = Integer.valueOf(headers.get(mContext.getString(R.string.makkelijkemarkt_api_x_listsize_header_name)));
                        if (totalListSize > mListOffset + mListLength) {
                            mListOffset += mListLength;

                            // enqueue again
                            enqueue();

                        } else {

                            // when we are done, remember when we last fetched the sollicitaties for
                            // selected markt in shared prefs
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putLong(
                                    mContext.getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + mMarktId,
                                    new Date().getTime());
                            editor.apply();

                            // inform subscribers that we completed loading all sollicitaties for selected markt
                            EventBus.getDefault().post(new OnCompletedEvent(totalListSize, null));
                        }
                    } catch (NumberFormatException e) {

                        // on missing listsize field send an error message
                        EventBus.getDefault().post(new OnCompletedEvent(-1, "Failed to get X-Api-ListSize"));
                    }
                }

                // copy the values to a contentvalues array that can be used in the
                // contentprovider bulkinsert method
                ContentValues[] sollicitatieValues = new ContentValues[response.body().size()];
                ContentValues[] koopmanValues = new ContentValues[response.body().size()];
                ArrayList<ContentValues> vervangerList = new ArrayList<>();
                for (int i = 0; i < response.body().size(); i++) {
                    ApiSollicitatie sollicitatie = response.body().get(i);

                    // get koopman
                    ApiKoopman koopman = sollicitatie.getKoopman();
                    koopmanValues[i] = koopman.toContentValues();

                    // get sollicitatie
                    sollicitatie.setKoopmanId(koopman.getId());
                    sollicitatieValues[i] = sollicitatie.toContentValues();

                    // get vervangers
                    if (koopman.getVervangers() != null && koopman.getVervangers().size() > 0) {
                        for (int j = 0; j < koopman.getVervangers().size(); j++) {
                            ApiVervanger vervanger = koopman.getVervangers().get(j);
                            vervanger.setId(koopman.getId() + "-" + vervanger.getVervangerId());
                            vervanger.setKoopmanId(koopman.getId());
                            vervangerList.add(vervanger.toContentValues());
                        }
                    }
                }

                // insert downloaded sollicitaties into db
                if (sollicitatieValues.length > 0) {
                    mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriSollicitatie,
                            sollicitatieValues);
                }

                // insert downloaded koopmannen into db
                if (koopmanValues.length > 0) {
                    mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriKoopman,
                            koopmanValues);
                }

                // insert downloaded vervangers into db
                if (vervangerList.size() > 0) {
                    mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriVervanger,
                            vervangerList.toArray(new ContentValues[vervangerList.size()]));
                }
            } else {

                // on empty list send an error message
                EventBus.getDefault().post(new OnCompletedEvent(-1, mContext.getString(R.string.notice_sollicitaties_empty)));
            }
        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnCompletedEvent(-1, "Empty response body"));
        }
    }

    /**
     * On failure of the getSollicitaties method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        EventBus.getDefault().post(new OnCompletedEvent(-1, t.getMessage()));
    }

    /**
     * Event to inform subscribers that we completed receiving sollicitaties from the api
     */
    public class OnCompletedEvent {
        public final int mSollicitatiesCount;
        public final String mMessage;

        public OnCompletedEvent(int sollicitatiesCount, String message) {
            mSollicitatiesCount = sollicitatiesCount;
            mMessage = message;
        }
    }
}