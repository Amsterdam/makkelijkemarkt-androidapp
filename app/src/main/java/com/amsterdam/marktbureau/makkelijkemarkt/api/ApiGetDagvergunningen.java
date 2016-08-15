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

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiSollicitatie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author marcolangebeeke
 */
public class ApiGetDagvergunningen extends ApiCall implements Callback<List<ApiDagvergunning>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetDagvergunningen.class.getSimpleName();

    // markt id for the querystring param
    private String mMarktId;

    // date of today formatted for the querystring param
    private String mDag;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetDagvergunningen(Context context) {
        super(context);
    }

    /**
     * Set the markt id querystring param
     * @param marktId string
     */
    public void setMarktId(String marktId) {
        mMarktId = marktId;
    }

    /**
     * Set the dag querystring param
     * @param dag string
     */
    public void setDag(String dag) {
        mDag = dag;
    }

    /**
     * Enqueue an async call that will request the dagvergunningen for selected markt and day
     */
    @Override
    public boolean enqueue() {
        if (super.enqueue()) {

            // set the api function to call for loading the dagvergunningen
            if (mMarktId != null && mDag != null) {
                Call<List<ApiDagvergunning>> call = mMakkelijkeMarktApi.getDagvergunningen(mMarktId, mDag);

                // call the api asynchronously
                call.enqueue(this);
            }

            return true;
        }

        return false;
    }

    /**
     * Response from the getDagvergunningen method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiDagvergunning>> response) {
        if (response.body() != null) {
            if (response.body().size() > 0) {

                List<ContentValues> dagvergunningValues = new ArrayList<>();
                List<ContentValues> koopmanValues = new ArrayList<>();
                List<ContentValues> sollicitatieValues = new ArrayList<>();

                // copy the values to a contentvalues list that can be used in the contentprovider bulkinsert method
                for (int i = 0; i < response.body().size(); i++) {
                    ApiDagvergunning dagvergunning = response.body().get(i);

                    if (dagvergunning != null) {

                        // add dagvergunning values to list for bulkinsert later
                        dagvergunningValues.add(dagvergunning.toContentValues());

                        // add koopman values to list for bulkinsert later
                        ApiKoopman koopman = dagvergunning.getKoopman();
                        if (koopman != null) {
                            koopmanValues.add(koopman.toContentValues());

                            // add sollicitatie values to list for bulkinsert later
                            ApiSollicitatie sollicitatie = dagvergunning.getSollicitatie();
                            if (sollicitatie != null) {
                                sollicitatie.setKoopmanId(koopman.getId());
                                sollicitatieValues.add(sollicitatie.toContentValues());
                            }
                        }
                    }
                }

                // update downloaded koopmannen into db using our custom bulkinsert
                if (koopmanValues.size() > 0) {
                    mContext.getContentResolver().bulkInsert(
                            MakkelijkeMarktProvider.mUriKoopman,
                            koopmanValues.toArray(new ContentValues[koopmanValues.size()]));
                }

                // update downloaded sollicitaties into db using our custom bulkinsert
                if (sollicitatieValues.size() > 0) {
                    mContext.getContentResolver().bulkInsert(
                            MakkelijkeMarktProvider.mUriSollicitatie,
                            sollicitatieValues.toArray(new ContentValues[sollicitatieValues.size()]));
                }

                // replace downloaded dagvergunningen into db using our custom bulkinsert
                if (dagvergunningValues.size() > 0) {
                    mContext.getContentResolver().delete(
                            MakkelijkeMarktProvider.mUriDagvergunning,
                            MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? ",
                            new String[]{mMarktId});
                    mContext.getContentResolver().bulkInsert(
                            MakkelijkeMarktProvider.mUriDagvergunning,
                            dagvergunningValues.toArray(new ContentValues[dagvergunningValues.size()]));
                }
            }

            // send event to subscribers that we retrieved a response
            EventBus.getDefault().post(new OnResponseEvent(response.body().size(), null));

        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnResponseEvent(-1, "Empty response body"));
        }
    }

    /**
     * On failure of the getDagvergunningen method send an error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        EventBus.getDefault().post(new OnResponseEvent(-1, t.getMessage()));
    }

    /**
     * Event to inform subscribers that we received a response from the api
     */
    public class OnResponseEvent {
        public final int mDagvergunningCount;
        public final String mMessage;

        public OnResponseEvent(int dagvergunningCount, String message) {
            mDagvergunningCount = dagvergunningCount;
            mMessage = message;
        }
    }
}