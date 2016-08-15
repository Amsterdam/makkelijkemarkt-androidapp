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
    public boolean enqueue(Callback callback) {
        if (super.enqueue(callback)) {

            // set the api function to call for loading the accounts
            Call<JsonObject> call = mMakkelijkeMarktApi.getLogout();

            // call the api asynchronously
            call.enqueue(callback);

            return true;
        }

        return false;
    }
}