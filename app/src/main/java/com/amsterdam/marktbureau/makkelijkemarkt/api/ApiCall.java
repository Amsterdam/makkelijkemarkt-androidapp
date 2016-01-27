/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.amsterdam.marktbureau.makkelijkemarkt.R;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Base class for makkelijkemarkt api calls using retrofit and gson converter
 * @author marcolangebeeke
 */
public class ApiCall {

    // use classname when logging
    protected static final String LOG_TAG = ApiCall.class.getSimpleName();

    // context
    protected Context mContext;

    // the api base url
    private String mBaseUrl;

    // the optional client to be used by retrofit
    protected OkHttpClient mClient;

    // retrofit api interface
    protected MakkelijkeMarktApi mMakkelijkeMarktApi;

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
}