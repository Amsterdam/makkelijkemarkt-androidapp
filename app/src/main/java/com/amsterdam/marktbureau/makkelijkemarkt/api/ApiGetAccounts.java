/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiAccount;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Load accounts from the makkelijkemarkt api and store them in the database
 * @author marcolangebeeke
 */
public class ApiGetAccounts extends ApiCall implements Callback<List<ApiAccount>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetAccounts.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetAccounts(Context context) {
        super(context);
    }

    /**
     * Enqueue an async call to load the accounts
     */
    @Override
    public void enqueue() {
        super.enqueue();

        // set the api function to call for loading the accounts
        Call<List<ApiAccount>> call = mMakkelijkeMarktApi.loadAccounts();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     * Response from the loadAccounts method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiAccount>> response) {
        if (response.body() != null && response.body().size() > 0) {

            // copy the values to a contentvalues array that can be used in the
            // contentprovider bulkinsert method
            ContentValues[] contentValues = new ContentValues[response.body().size()];
            for (int i = 0; i < response.body().size(); i++) {
                contentValues[i] = response.body().get(i).toContentValues();
            }

            // delete existing accounts and insert downloaded accounts into db
            if (contentValues.length > 0) {

                int deleted = mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriAccount, null, null);
                Utility.log(mContext, LOG_TAG, "Accounts deleted: " + deleted);

                int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriAccount, contentValues);
                Utility.log(mContext, LOG_TAG, "Accounts inserted: " + inserted);
            }
        }
    }

    /**
     * On failure of the loadAccounts method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: "+ t.getMessage());
    }
}