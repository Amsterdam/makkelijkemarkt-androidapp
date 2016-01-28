/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Base class for makkelijkemarkt api calls using retrofit and gson converter
 * @author marcolangebeeke
 */
public class ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiCall.class.getSimpleName();

    // context
    protected Context mContext;

    // the api base url
    protected String mBaseUrl;

    // retrofit api interface
    protected MakkelijkeMarktApi mMakkelijkeMarktApi;

    // an optional different client to be used by retrofit
    protected OkHttpClient mClient;

    // an optional gson payload to send with the request
    protected JsonObject mPayload;

    /**
     * Constructor setting the given context and a default api base url
     * @param context the context
     */
    public ApiCall(Context context) {
        mContext = context;
        setBaseUrl(mContext.getString(R.string.makkelijkemarkt_api_base_url));
    }

    /**
     * Constructor setting the given context and api base url
     * @param context the context
     * @param baseUrl the api base url
     */
    public ApiCall(Context context, String baseUrl) {
        mContext = context;
        setBaseUrl(baseUrl);
    }

    /**
     * Set the api base url
     * @param baseUrl the base url string
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * Set the http client that retrofit wil use
     * @param client an okhttp client
     */
    public void setClient(OkHttpClient client) {
        mClient = client;
    }

    /**
     * Set a json payload that can be sent with the request
     * @param payload the gson
     */
    public void setPayload(JsonObject payload) {
        mPayload = payload;
    }

    /**
     * Build the retrofit object, optionally with a custom client
     */
    public void build() {

        // create the retrofit builder with a gson converter
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(mBaseUrl);
        builder.addConverterFactory(GsonConverterFactory.create());

        // set a custom client if specified
        if (mClient != null) {
            builder.client(mClient);
        }

        // build retrofit
        Retrofit retrofit = builder.build();

        // apply the makkelijkemarkt api interface
        mMakkelijkeMarktApi = retrofit.create(MakkelijkeMarktApi.class);
    }

    /**
     * Enqueue async api method that will be overriden in the subclass, and make sure its superclass
     * method is always called
     */
    @CallSuper
    public void enqueue() {
        if (mMakkelijkeMarktApi == null) {
            build();
        }
    }

    /**
     * Enqueue async api method with given callback
     * @param callback the object that will process the response
     */
    @CallSuper
    public void enqueue(Callback callback) {
        if (mMakkelijkeMarktApi == null) {
            build();
        }
    }

}