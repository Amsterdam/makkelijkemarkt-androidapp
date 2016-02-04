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
public class ApiGetLogout extends ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiGetLogout.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetLogout(Context context) {
        super(context);
    }

    /**
     *
     * @param callback the object that will process the response
     */
    @Override
    public void enqueue(Callback callback) {
        super.enqueue(callback);

        // set the api function to call for loading the accounts
        Call<JsonObject> call = mMakkelijkeMarktApi.getLogout();

        // call the api asynchronously
        call.enqueue(callback);
    }
}