/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Get a list of koopmannen from the api, select by status and optionally call multiple times
 * with an offset
 * @author marcolangebeeke
 */
public class ApiGetKoopmannen extends ApiCall implements Callback<List<ApiKoopman>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetKoopmannen.class.getSimpleName();

    // call parameters
    private int mStatus = -1;
    private int mListOffset = 0;
    private int mListLength = 500;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetKoopmannen(Context context) {
        super(context);
    }

    /**
     * Set the status of the koopmannen we want
     * @param status status of the koopmannen
     */
    public void setStatus(int status) {
        mStatus = status;
    }

    /**
     * Enqueue an async call to load the koopmannen
     */
    @Override
    public boolean enqueue() {
        if (super.enqueue()) {

            // set the api function to call for loading the koopmannen
            Call<List<ApiKoopman>> call = mMakkelijkeMarktApi.getKoopmannen(
                    String.valueOf(mStatus),
                    String.valueOf(mListOffset),
                    String.valueOf(mListLength));

            // call the api asynchronously
            call.enqueue(this);

            return true;
        }

        return false;
    }

    /**
     * When we receive the response from the api we store the koopmannen in the database and
     * optionally we call again for more
     * @param response api response containing a list of ApiKoopman objects
     */
    @Override
    public void onResponse(Response<List<ApiKoopman>> response) {
        if (response.body() != null) {
            if (response.body().size() > 0) {

                // get http headers from response
                Headers headers = response.headers();

                // get listsize header
                if (headers.get(mContext.getString(R.string.makkelijkemarkt_api_x_listsize_header_name)) != null) {
                    try {

                        // if there is still more to fetch, increase the offset and enqueue again
                        int totalListSize = Integer.valueOf(headers.get(mContext.getString(R.string.makkelijkemarkt_api_x_listsize_header_name)));
                        if (totalListSize > mListOffset + mListLength) {
                            mListOffset += mListLength;

                            // enqueue again
                            enqueue();

                        } else {

                            // when we are done, remember when we last fetched the koopmannen for
                            // selected status in shared prefs
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putLong(
                                    mContext.getString(R.string.sharedpreferences_key_koopmannen_last_fetched) + mStatus,
                                    new Date().getTime());
                            editor.apply();

                            // inform subscribers that we completed loading all koopmannen for selected status
                            EventBus.getDefault().post(new OnCompletedEvent(totalListSize, null));
                        }
                    } catch (NumberFormatException e) {

                        // on missing listsize field send an error message
                        EventBus.getDefault().post(new OnCompletedEvent(-1, "Failed to get X-Api-ListSize"));
                    }
                }

                // copy the values to a contentvalues array that can be used in the
                // contentprovider bulkinsert method
                ContentValues[] koopmanValues = new ContentValues[response.body().size()];
                for (int i = 0; i < response.body().size(); i++) {
                    ApiKoopman koopman = response.body().get(i);
                    koopmanValues[i] = koopman.toContentValues();
                }

                // insert downloaded koopmannen into db
                if (koopmanValues.length > 0) {
                    mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriKoopman, koopmanValues);
                }
            } else {

                // on empty list send an error message
                EventBus.getDefault().post(new OnCompletedEvent(-1, mContext.getString(R.string.notice_koopmannen_empty)));
            }
        } else {

            // on empty body send an error message
            EventBus.getDefault().post(new OnCompletedEvent(-1, "Empty response body"));
        }
    }

    /**
     * On failure of the getSollicitaties method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        EventBus.getDefault().post(new OnCompletedEvent(-1, t.getMessage()));
    }

    /**
     * Event to inform subscribers that we completed receiving koopmannen from the api
     */
    public class OnCompletedEvent {
        public final int mKoopmannenCount;
        public final String mMessage;

        public OnCompletedEvent(int koopmannenCount, String message) {
            mKoopmannenCount = koopmannenCount;
            mMessage = message;
        }
    }
}