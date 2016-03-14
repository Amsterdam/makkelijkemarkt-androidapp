/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

/**
 *
 * @author marcolangebeeke
 */
public class ApiPostLoginBasicId extends ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiPostLoginBasicId.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiPostLoginBasicId(Context context) {
        super(context);
    }

    /**
     * Enqueue the async post
     * @param callback the object that will process the api response
     */
    @Override
    public boolean enqueue(Callback callback) {
        if (super.enqueue(callback)) {

            // set the api function to call for loading the accounts
            if (callback != null && mPayload != null) {
                Call<JsonObject> call = mMakkelijkeMarktApi.postLoginBasicId(mPayload);

                // call the api asynchronously
                call.enqueue(callback);
            }

            return true;
        }

        return false;
    }
}