/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;
import android.net.Uri;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiPutNotitie extends ApiCall implements Callback<ApiNotitie> {

    // use classname when logging
    private static final String LOG_TAG = ApiPutNotitie.class.getSimpleName();

    // call parameters
    private String mId;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiPutNotitie(Context context) {
        super(context);
    }

    /**
     * Set the id of the notitie
     * @param id notitie id
     */
    public void setId(int id) {
        if (id > 0) {
            mId = String.valueOf(id);
        }
    }

    /**
     * Enqueue the async post
     */
    @Override
    public boolean enqueue() {
        if (super.enqueue()) {

            // if we have a payload we set the function to call and enqueue the async request
            if (mId != null && mPayload != null) {
                Call<ApiNotitie> call = mMakkelijkeMarktApi.putNotitie(mId, mPayload);

                // call the api asynchronously
                call.enqueue(this);
            }

            return true;
        }

        return false;
    }

    /**
     *
     * @param response
     */
    @Override
    public void onResponse(Response<ApiNotitie> response) {
        if (response.body() != null) {
            Uri notitieUri = mContext.getContentResolver().insert(MakkelijkeMarktProvider.mUriNotitie, response.body().toContentValues());
            if (notitieUri != null) {

                // send event to subscribers that the notitie updated successful
                EventBus.getDefault().post(new OnResponseEvent(response.body(), null));
            }
        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnResponseEvent(null, "Empty response body"));
        }
    }

    /**
     *
     * @param t
     */
    @Override
    public void onFailure(Throwable t) {
        EventBus.getDefault().post(new OnResponseEvent(null, t.getMessage()));
    }

    /**
     * Event to inform subscribers that we received a response from the api
     */
    public class OnResponseEvent {
        public final ApiNotitie mNotitie;
        public final String mMessage;

        public OnResponseEvent(ApiNotitie notitie, String message) {
            mNotitie = notitie;
            mMessage = message;
        }
    }
}