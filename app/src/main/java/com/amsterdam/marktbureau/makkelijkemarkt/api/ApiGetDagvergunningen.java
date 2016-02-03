/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author marcolangebeeke
 */
public class ApiGetDagvergunningen extends ApiCall implements Callback<List<ApiDagvergunning>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetDagvergunningen.class.getSimpleName();

    // markt id for the querystring param
    private String mMarktId;

    // date of today formatted for the querystring param
    private String mDag;

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetDagvergunningen(Context context) {
        super(context);
    }

    /**
     * Set the markt id querystring param
     * @param marktId string
     */
    public void setMarktId(String marktId) {
        mMarktId = marktId;
    }

    /**
     * Set the dag querystring param
     * @param dag string
     */
    public void setDag(String dag) {
        mDag = dag;
    }

    /**
     * Enqueue an async call that will request the dagvergunningen for selected markt and day
     */
    @Override
    public void enqueue() {
        super.enqueue();

        // set the api function to call for loading the dagvergunningen
        Call<List<ApiDagvergunning>> call = mMakkelijkeMarktApi.getDagvergunningen(mMarktId, mDag);

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     * Response from the getDagvergunningen method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiDagvergunning>> response) {
        if (response.body() != null && response.body().size() > 0) {

            // copy the values to a contentvalues array that can be used in the
            // contentprovider bulkinsert method
            ContentValues[] contentValues = new ContentValues[response.body().size()];
            for (int i = 0; i < response.body().size(); i++) {
                ApiDagvergunning dagvergunning = response.body().get(i);

                // add dagvergunningen values to array for bulkinsert later
                contentValues[i] = dagvergunning.toContentValues();

                // insert or update the koopman
                ApiKoopman koopman = dagvergunning.getKoopman();
                if (koopman != null) {
                    try {

                        Uri koopmanUri = mContext.getContentResolver().insert(
                                MakkelijkeMarktProvider.mUriKoopman,
                                koopman.toContentValues()
                        );
                        Utility.log(mContext, LOG_TAG, "Inserted koopman: " + koopman.getId() + ", get it here: " + koopmanUri.toString());

                    } catch (SQLiteConstraintException e) {

                        // update the existing query record
                        int updated = mContext.getContentResolver().update(
                                MakkelijkeMarktProvider.mUriKoopman,
                                koopman.toContentValues(),
                                MakkelijkeMarktProvider.Koopman.COL_ID + " = ?",
                                new String[]{String.valueOf(koopman.getId())}
                        );
                        Utility.log(mContext, LOG_TAG, "Updated koopman: " + koopman.getId());
                    }
                }
            }

            // delete existing dagvergunningen and insert downloaded dagvergunningen into db
            if (contentValues.length > 0) {

                // delete all dagvergunningen voor selected markt and dag
                int deleted = mContext.getContentResolver().delete(
                        MakkelijkeMarktProvider.mUriDagvergunning,
                        MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? AND " + MakkelijkeMarktProvider.Dagvergunning.COL_DAG + " = ?",
                        new String[]{ mMarktId, mDag });
                Utility.log(mContext, LOG_TAG, "Dagvergunningen deleted: " + deleted);

                // insert new dagvergunningen
                int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriDagvergunning, contentValues);
                Utility.log(mContext, LOG_TAG, "Dagvergunningen inserted: " + inserted);
            }
        }
    }

    /**
     * On failure of the getDagvergunningen method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: " + t.getMessage());
    }
}