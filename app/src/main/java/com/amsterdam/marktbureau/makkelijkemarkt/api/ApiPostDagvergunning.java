/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;

import retrofit2.Call;
import retrofit2.Callback;

/**
 *
 * @author marcolangebeeke
 */
public class ApiPostDagvergunning extends ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiPostDagvergunning.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiPostDagvergunning(Context context) {
        super(context);
    }

    /**
     * Enqueue the async post
     * @param callback the object that will process the api response
     */
    @Override
    public void enqueue(Callback callback) {
        super.enqueue(callback);

        // if we have a payload we set the function to call and enqueue the async request
        if (callback != null && mPayload != null) {
            Call<ApiDagvergunning> call = mMakkelijkeMarktApi.postDagvergunning(mPayload);

            // call the api asynchronously
            call.enqueue(callback);
        }
    }
}