/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiGetNotities extends ApiCall implements Callback<List<ApiNotitie>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetNotities.class.getSimpleName();

    // markt id for the querystring param
    private String mMarktId;

    // date of today formatted for the querystring param
    private String mDag;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetNotities(Context context) {
        super(context);
    }

    /**
     * Set the markt id path param
     * @param marktId string
     */
    public void setMarktId(String marktId) {
        mMarktId = marktId;
    }

    /**
     * Set the dag path param
     * @param dag string
     */
    public void setDag(String dag) {
        mDag = dag;
    }

    /**
     * Enqueue an async call that will request the notities for selected markt and day
     */
    @Override
    public void enqueue() {
        super.enqueue();

        // set the api function to call for loading the notities
        if (mMarktId != null && mDag != null) {
            Call<List<ApiNotitie>> call = mMakkelijkeMarktApi.getNotities(mMarktId, mDag, "-1");

            // call the api asynchronously
            call.enqueue(this);
        }
    }

    /**
     * Response from the getNotities method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiNotitie>> response) {
        if (response.body() != null) {
            if (response.body().size() > 0) {
                List<ContentValues> notitieValues = new ArrayList<>();

                // copy the values to a contentvalues list that can be used in the contentprovider bulkinsert method
                for (int i = 0; i < response.body().size(); i++) {
                    ApiNotitie notitie = response.body().get(i);

                    // add notitie values to list for bulkinsert later
                    if (notitie != null) {
                        notitieValues.add(notitie.toContentValues());
                    }
                }

                // replace downloaded notities into db using our custom bulkinsert
                if (notitieValues.size() > 0) {
                    mContext.getContentResolver().delete(
                            MakkelijkeMarktProvider.mUriNotitie,
                            MakkelijkeMarktProvider.Notitie.COL_MARKT_ID + " = ? ",
                            new String[]{mMarktId});
                    mContext.getContentResolver().bulkInsert(
                            MakkelijkeMarktProvider.mUriNotitie,
                            notitieValues.toArray(new ContentValues[notitieValues.size()]));
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
     * On failure of the getNotities method send an error message
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
        public final int mNotitieCount;
        public final String mMessage;

        public OnResponseEvent(int notitieCount, String message) {
            mNotitieCount = notitieCount;
            mMessage = message;
        }
    }
}