/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.amsterdam.marktbureau.makkelijkemarkt.model.Account;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiGetAccounts extends Api implements Callback<List<Account>> {

    /**
     *
     * @param context
     */
    public ApiGetAccounts(Context context) {
        super(context);
    }

    /**
     *
     */
    public void execute() {

        // set the api function to call for loading the accounts
        Call<List<Account>> call = mMakkelijkeMarktApi.loadAccounts();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     * Response from the loadAccounts method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<Account>> response) {

        // check the response and update the database
        if (response != null && response.body() != null && response.body().size() > 0) {
            ContentValues[] ContentValuesArray = new ContentValues[response.body().size()];

            for (int i = 0; i < response.body().size(); i++) {
                Account account = response.body().get(i);

                // copy the values and add the to a contentvalues array that can be used in the
                // contentprovider bulkinsert method
                ContentValues accountValues = new ContentValues();
                accountValues.put(MakkelijkeMarktProvider.Account.COL_ID, account.getId());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_NAAM, account.getNaam());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_EMAIL, account.getEmail());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_USERNAME, account.getUsername());
                accountValues.put(MakkelijkeMarktProvider.Account.COL_ROLE, account.getRolesAsString());
                ContentValuesArray[i] = accountValues;
            }

            // delete existing accounts from db
            int deleted = mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriAccount, null, null);
            Utility.log(mContext, LOG_TAG, "Accounts deleted: " + deleted);

            // insert downloaded accounts into db
            int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriAccount, ContentValuesArray);
            Utility.log(mContext, LOG_TAG, "Accounts inserted: " + inserted);
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