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

import retrofit2.Call;
import retrofit2.Callback;

/**
 *
 * @author marcolangebeeke
 */
public class ApiDeleteDagvergunning extends ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiDeleteDagvergunning.class.getSimpleName();

    // call parameters
    private String mId;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiDeleteDagvergunning(Context context) {
        super(context);
    }

    /**
     * Set the id of the dagvergunning
     * @param id dagvergunning id
     */
    public void setId(int id) {
        if (id > 0) {
            mId = String.valueOf(id);
        }
    }

    /**
     * Enqueue the async post
     * @param callback the object that will process the api response
     */
    @Override
    public boolean enqueue(Callback callback) {
        if (super.enqueue(callback)) {

            // if we have a payload we set the function to call and enqueue the async request
            if (callback != null && mId != null) {
                Call<String> call = mMakkelijkeMarktApi.deleteDagvergunning(mId);

                // call the api asynchronously
                call.enqueue(callback);

                return true;
            }
        }

        return false;
    }
}