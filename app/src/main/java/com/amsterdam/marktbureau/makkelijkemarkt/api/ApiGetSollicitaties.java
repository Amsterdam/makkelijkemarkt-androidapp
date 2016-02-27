/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiSollicitatie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.Date;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class ApiGetSollicitaties extends ApiCall implements Callback<List<ApiSollicitatie>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetSollicitaties.class.getSimpleName();

    // call parameters
    private int mMarktId = -1;
    private int mListOffset = 0;
    private int mListLength = 1000;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetSollicitaties(Context context) {
        super(context);
    }

    /**
     * Set the id of the markt we want the sollicitaties for
     * @param marktId id of the markt
     */
    public void setMarktId(int marktId) {
        mMarktId = marktId;
    }

    /**
     * Enqueue an async call to load the accounts
     */
    @Override
    public void enqueue() {
        super.enqueue();
        if (mMarktId != -1) {

            // set the api function to call for loading the sollicitaties
            Call<List<ApiSollicitatie>> call = mMakkelijkeMarktApi.getSollicitaties(
                    String.valueOf(mMarktId),
                    String.valueOf(mListOffset),
                    String.valueOf(mListLength));

            // call the api asynchronously
            call.enqueue(this);

        } else {
            Utility.log(mContext, LOG_TAG, "Call failed, markt id not set!");
        }
    }

    /**
     * When we receive the response from the api we store the sollicitaties in the database and
     * optionally we call again for more
     * @param response api response containing a list of ApiSollicitatie objects
     */
    @Override
    public void onResponse(Response<List<ApiSollicitatie>> response) {
        if (response.body() != null && response.body().size() > 0) {

            // get http headers from response
            Headers headers = response.headers();

            // get listsize header
            if (headers.get("X-Api-ListSize") != null) {
                try {
                    int totalListSize = Integer.valueOf(headers.get("X-Api-ListSize"));
                    Utility.log(mContext, LOG_TAG, "Sollicitaties received! Response list size = " + response.body().size() + " Total list size = " + totalListSize);

                    // if there is still more to fetch, increase the offset and enqueue again
                    if (totalListSize > mListOffset + mListLength) {
                        mListOffset += mListLength;

                        // enqueue again
                        enqueue();

                    } else {

                        // when we are done, remember when we last fetched the sollicitaties for
                        // selected markt in shared prefs
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putLong(
                                mContext.getString(R.string.sharedpreferences_key_sollicitaties_last_fetched) + mMarktId,
                                new Date().getTime());
                        editor.apply();

                    }
                } catch (NumberFormatException e) {
                    Utility.log(mContext, LOG_TAG, "Failed to retrieve listsize: " + e.getMessage());
                }
            }

            // copy the values to a contentvalues array that can be used in the
            // contentprovider bulkinsert method
            ContentValues[] sollicitatieValues = new ContentValues[response.body().size()];
            ContentValues[] koopmanValues = new ContentValues[response.body().size()];
            for (int i = 0; i < response.body().size(); i++) {
                ApiSollicitatie sollicitatie = response.body().get(i);
                ApiKoopman koopman = sollicitatie.getKoopman();

                // TODO: remove temporary fake NFC UID once we receive it from the api
                if (koopman.getErkenningsnummer().equals("1957051001")) {
                    koopman.setNfcUid("407fe606");
                } else if (koopman.getErkenningsnummer().equals("1973120702")) {
                    koopman.setNfcUid("7c5d1e40");
                } else if (koopman.getErkenningsnummer().equals("1970032002")) {
                    koopman.setNfcUid("8c481740");
                } else {
                    koopman.setNfcUid(Utility.getRandomHexString(8));
                }

                koopmanValues[i] = koopman.toContentValues();
                sollicitatie.setKoopmanId(koopman.getId());
                sollicitatieValues[i] = sollicitatie.toContentValues();
            }

            // insert downloaded sollicitaties into db
            if (sollicitatieValues.length > 0) {
                int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriSollicitatie, sollicitatieValues);
                Utility.log(mContext, LOG_TAG, "Sollicitaties inserted: " + inserted);
            }

            // insert downloaded koopmannen into db
            if (koopmanValues.length > 0) {
                int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriKoopman, koopmanValues);
                Utility.log(mContext, LOG_TAG, "Koopmannen inserted: " + inserted);
            }
        }
    }

    /**
     * On failure of the getSollicitaties method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: "+ t.getMessage());
    }
}