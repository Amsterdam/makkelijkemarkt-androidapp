/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.model.ApiMarkt;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiGetMarkten extends ApiAbstractMethod implements Callback<List<ApiMarkt>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetMarkten.class.getSimpleName();

    /**
     *
     * @param context
     */
    public ApiGetMarkten(Context context) {
        super(context);
    }

    /**
     *
     */
    public void execute() {

        // set the api function to call for loading the markten
        Call<List<ApiMarkt>> call = mMakkelijkeMarktApi.loadMarkten();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     *
     * @param response
     */
    @Override
    public void onResponse(Response<List<ApiMarkt>> response) {
        if (response != null && response.body() != null && response.body().size() > 0) {

            // create array for the bulkinsert
            ContentValues[] ContentValuesArray = new ContentValues[response.body().size()];

            Utility.log(mContext, LOG_TAG, "Markten gevonden: " + response.body().size());





//            // delete existing markten from db
//            int deleted = mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriMarkt, null, null);
//            Utility.log(mContext, LOG_TAG, "Markten deleted: " + deleted);

//            // insert downloaded markten into db
//            int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriMarkt, ContentValuesArray);
//            Utility.log(mContext, LOG_TAG, "Markten inserted: " + inserted);
        }
    }

    /**
     *
     * @param t
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: " + t.getMessage());
    }
}