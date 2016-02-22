/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    // a client builder for creating our client to be used by retrofit
    protected OkHttpClient.Builder mClientBuilder;

    // an optional gson payload to send with the request
    protected JsonObject mPayload;

    /**
     * @todo act on '401 Unauthorized' response from api if we are logged out for some reason
     */

    /**
     * Constructor setting the given context and a default api base url
     * @param context the context
     */
    public ApiCall(Context context) {
        mContext = context;
        mClientBuilder = new OkHttpClient.Builder();
        setBaseUrl(mContext.getString(R.string.makkelijkemarkt_api_base_url));
    }

    /**
     * Constructor setting the given context and api base url
     * @param context the context
     * @param baseUrl the api base url
     */
    public ApiCall(Context context, String baseUrl) {
        mContext = context;
        mClientBuilder = new OkHttpClient.Builder();
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



        // @todo refactor to use real app version, app name, okhttp version, etc.

        // @todo refactor to get api-key somewhere more central (constructor?)

        // @todo refactor to create headers somewhere more central and only create an interceptor here and add all headers to it

        // @todo add language header? (Accept-Language: nl-NL,en-US;q=0.8)

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = settings.getString(mContext.getString(R.string.sharedpreferences_key_uuid), null);

        if (apiKey != null) {
//            Utility.log(mContext, LOG_TAG, "Stored api-key found: " + apiKey);

            final String authHeaderValue = mContext.getString(R.string.makkelijkemarkt_api_authorization_header_prefix) +" "+ apiKey;

            Interceptor addAuthorizationHeaderInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .removeHeader(
                                    mContext.getString(R.string.makkelijkemarkt_api_user_agent_header_name))
                            .addHeader(
                                    mContext.getString(R.string.makkelijkemarkt_api_user_agent_header_name),
                                    "Android App Makkelijke Markt v1.0 (okhttp/3.0.0-RC1)")
                            .addHeader(
                                    mContext.getString(R.string.makkelijkemarkt_api_authorization_header_name),
                                    authHeaderValue)
                            .build();

                    return chain.proceed(request);
                }};

            mClientBuilder.addInterceptor(addAuthorizationHeaderInterceptor);
        }



        // build and attach okhttpclient to retrofit
        builder.client(mClientBuilder.build());

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