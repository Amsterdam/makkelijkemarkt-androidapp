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
import android.net.Uri;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiSollicitatie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author marcolangebeeke
 */
public class ApiGetKoopmanByErkenningsnummer extends ApiCall implements Callback<ApiKoopman> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetKoopmanByErkenningsnummer.class.getSimpleName();

    // keep a reference from who called
    private String mCaller;

    // erkenningsnummer of the koopman we are looking for
    private String mErkenningsnummer;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetKoopmanByErkenningsnummer(Context context, String caller) {
        super(context);
        mCaller = caller;
    }

    /**
     * Set the erkenningsnummer we need as path segment for calling the api
     * @param erkenningsnummer the erkenningsnummer
     */
    public void setErkenningsnummer(String erkenningsnummer) {
        mErkenningsnummer = erkenningsnummer;
    }

    /**
     * Enqueue an async call to load the koopman
     */
    @Override
    public boolean enqueue() {
        if (super.enqueue()) {
            if (mErkenningsnummer != null) {

                // set the api function to call for loading the accounts
                Call<ApiKoopman> call = mMakkelijkeMarktApi.getKoopmanByErkenningsnummer(mErkenningsnummer);

                // call the api asynchronously
                call.enqueue(this);

            } else {
                Utility.log(mContext, LOG_TAG, "Call failed, erkenningsnummer not set");
            }

            return true;
        }

        return false;
    }

    /**
     * Response from the getKoopman method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<ApiKoopman> response) {
        if (response.body() != null) {

            // create contentvalues array from sollicitaties
            if (response.body().getSollicitaties() != null && response.body().getSollicitaties().size() > 0) {
                ContentValues[] sollicitatieValues = new ContentValues[response.body().getSollicitaties().size()];
                for (int i = 0; i < response.body().getSollicitaties().size(); i++) {
                    ApiSollicitatie sollicitatie = response.body().getSollicitaties().get(i);
                    sollicitatie.setKoopmanId(response.body().getId());
                    sollicitatieValues[i] = sollicitatie.toContentValues();
                }

                // use bulkinsert to insert/update sollicitaties in db
                if (sollicitatieValues.length > 0) {
                    mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriSollicitatie, sollicitatieValues);
                }
            }

            // insert/update koopman in db
            ContentValues koopmanValues = response.body().toContentValues();
            if (koopmanValues != null) {
                Uri koopmanUri = mContext.getContentResolver().insert(MakkelijkeMarktProvider.mUriKoopman, koopmanValues);
                if (koopmanUri != null) {

                    // send event to subscribers that we retrieved the koopman succesfully
                    EventBus.getDefault().post(new OnResponseEvent(response.body(), null, mCaller));
                }
            }
        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnResponseEvent(null, "Empty response body", mCaller));
        }
    }

    /**
     * On failure of the getKoopman method send an error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        EventBus.getDefault().post(new OnResponseEvent(null, t.getMessage(), mCaller));
    }

    /**
     * Event to inform subscribers that we received a response from the api
     */
    public class OnResponseEvent {
        public final ApiKoopman mKoopman;
        public final String mMessage;
        public final String mCaller;

        public OnResponseEvent(ApiKoopman koopman, String message, String caller) {
            mKoopman = koopman;
            mMessage = message;
            mCaller = caller;
        }
    }
}