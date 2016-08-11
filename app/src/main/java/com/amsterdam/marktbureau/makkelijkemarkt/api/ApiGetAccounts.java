/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiAccount;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
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
    public boolean enqueue() {
        if (super.enqueue()) {

            // set the api function to call for loading the accounts
            Call<List<ApiAccount>> call = mMakkelijkeMarktApi.getAccounts();

            // call the api asynchronously
            call.enqueue(this);

            return true;
        }

        return false;
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
                mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriAccount, null, null);
                mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriAccount, contentValues);

                // when we are done, remember when we last fetched the accounts
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(
                        mContext.getString(R.string.sharedpreferences_key_accounts_last_fetched),
                        new Date().getTime());
                editor.apply();

                // send event to subscribers that we retrieved a response
                EventBus.getDefault().post(new OnResponseEvent(response.body().size(), null));
            }
        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnResponseEvent(-1, "Empty response body"));
        }
    }

    /**
     * On failure of the loadAccounts method send the error message
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
        public final int mAccountCount;
        public final String mMessage;

        public OnResponseEvent(int accountCount, String message) {
            mAccountCount = accountCount;
            mMessage = message;
        }
    }
}