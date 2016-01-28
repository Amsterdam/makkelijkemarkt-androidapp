/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiMarkt;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Load markten from the makkelijkemarkt api and store them in the database
 * @author marcolangebeeke
 */
public class ApiGetMarkten extends ApiCall implements Callback<List<ApiMarkt>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetMarkten.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiGetMarkten(Context context) {
        super(context);
    }

    /**
     * Enqueue an async call to load the markten
     */
    @Override
    public void enqueue() {
        super.enqueue();

        // set the api function to call for loading the markten
        Call<List<ApiMarkt>> call = mMakkelijkeMarktApi.getMarkten();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     * Create an okhttpclient with an interceptor that transforms the aanwezigeopties object in the
     * json response from a collection of objects into a comma-separated string before returning the
     * response to the caller
     * @return an okhttpclient containing the interceptor
     */
    public OkHttpClient buildClientWithInterceptor() {
        Interceptor convertAanwezigeOpties = new Interceptor() {

            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());

                // if we have a successful response modifiy it
                if (response.isSuccessful()) {
                    final String aanwezigeOptiesObjectName = "aanwezigeOpties";
                    try {

                        // get the contenttype so we can re-create it later the same
                        MediaType contentType = response.body().contentType();
                        JSONArray markten = new JSONArray(response.body().string());

                        // loop through markten array objects
                        for(int i = 0; i < markten.length(); i++) {
                            JSONObject markt = markten.getJSONObject(i);

                            // get the value of the aanwezigeopties object and check if it is of type
                            // jsonobject (instead of array as is sometimes the case)
                            Object object = markt.get(aanwezigeOptiesObjectName);
                            if (object instanceof JSONObject) {
                                JSONObject aanwezigeOpties = (JSONObject) object;

                                // get objects in aanwezige opties and add their string names to an array
                                Iterator<String> iterator = aanwezigeOpties.keys();
                                JSONArray opties = new JSONArray();
                                while (iterator.hasNext()) {
                                    opties.put(iterator.next());
                                }

                                // replace the existing object with the new jsonarray
                                markt.remove(aanwezigeOptiesObjectName);
                                markt.put(aanwezigeOptiesObjectName, opties);
                            }
                        }

                        // re-create the response
                        ResponseBody body = ResponseBody.create(contentType, markten.toString());
                        response = response.newBuilder().body(body).build();

                    } catch (JSONException e) {
                        Utility.log(mContext, LOG_TAG, "Exception creating JSONObject: " + e.getMessage());
                    }
                }

                // return the modified response object
                return response;
            }
        };

        // build client with created interceptor
        return new OkHttpClient.Builder()
                .addInterceptor(convertAanwezigeOpties)
                .build();
    }

    /**
     * Response from the loadMarkten method arrives here for updating the database
     * @param response response we received from the api
     */
    @Override
    public void onResponse(Response<List<ApiMarkt>> response) {
        if (response.body() != null && response.body().size() > 0) {

            // copy the values to a contentvalues array that can be used in the
            // contentprovider bulkinsert method
            ContentValues[] contentValues = new ContentValues[response.body().size()];
            for (int i = 0; i < response.body().size(); i++) {
                contentValues[i] = response.body().get(i).toContentValues();
            }

            // delete existing markten and insert downloaded marken into db
            if (contentValues.length > 0) {

                int deleted = mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriMarkt, null, null);
                Utility.log(mContext, LOG_TAG, "Markten deleted: " + deleted);

                int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriMarkt, contentValues);
                Utility.log(mContext, LOG_TAG, "Markten inserted: " + inserted);
            }
        }
    }

    /**
     * On failure of the loadMarkten method log the error message
     * @param t the thrown exception
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: " + t.getMessage());
    }
}