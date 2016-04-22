/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    // keep a reference to toast messages
    private Toast mToast;

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
    public MakkelijkeMarktApi build() {

        // create the retrofit builder with a gson converter
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(mBaseUrl);
        builder.addConverterFactory(GsonConverterFactory.create());

        // get api-key from shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        String apiKey = settings.getString(mContext.getString(R.string.sharedpreferences_key_uuid), null);

        // if we have an api-key
        if (apiKey != null) {

            // add header interceptor to add the api-key to the authorisation header
            final String authHeaderValue = mContext.getString(R.string.makkelijkemarkt_api_authorization_header_prefix) +" "+ apiKey;
            Interceptor addAuthorizationHeaderInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();

                    // add Authorisation header
                    requestBuilder.addHeader(
                            mContext.getString(R.string.makkelijkemarkt_api_authorization_header_name),
                            authHeaderValue);

                    // build the request
                    Request request = requestBuilder.build();

                    return chain.proceed(request);
                }};
            mClientBuilder.addInterceptor(addAuthorizationHeaderInterceptor);

            // add an interceptor that will detect for a 401-Unauthorised response and send an event
            // to be handled in the base activity
            Interceptor handleUnauthorizedInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    final int responseCode = response.code();

                    // detect 401-Unauthorised http response
                    if (responseCode == 401) {

                        // get a reference to the main thread and post a runnable that will post our event
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postAtFrontOfQueue(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new OnUnauthorizedEvent(
                                        responseCode, mContext.getString(R.string.notice_api_unauthorised)));
                            }
                        });
                    }

                    return response;
                }};
            mClientBuilder.addInterceptor(handleUnauthorizedInterceptor);
        }

        // add header interceptor to create our custom x-serial-number header
        Interceptor addDeviceSerialHeaderInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();

                requestBuilder.addHeader(
                        mContext.getString(R.string.makkelijkemarkt_api_x_serial_number_header_name),
                        Build.SERIAL);

                // build the request
                Request request = requestBuilder.build();

                return chain.proceed(request);
            }};
        mClientBuilder.addInterceptor(addDeviceSerialHeaderInterceptor);

        // add header interceptor to create our custom user-agent header
        Interceptor addUserAgentHeaderInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();

                // replace User-Agent header
                String appName = Utility.getAppName(mContext);
                String appVersion = Utility.getAppVersion(mContext);
                String httpUserAgent = okhttp3.internal.Version.userAgent();
                if (appName != null && appVersion != null) {
                    requestBuilder.removeHeader(
                            mContext.getString(R.string.makkelijkemarkt_api_user_agent_header_name));
                    requestBuilder.addHeader(
                            mContext.getString(R.string.makkelijkemarkt_api_user_agent_header_name),
                            appName + " - Version " + appVersion + " - " + httpUserAgent);
                }

                // build the request
                Request request = requestBuilder.build();

                return chain.proceed(request);
            }};
        mClientBuilder.addInterceptor(addUserAgentHeaderInterceptor);

        // build and attach okhttpclient to retrofit
        builder.client(mClientBuilder.build());

        // build retrofit
        Retrofit retrofit = builder.build();

        // apply the makkelijkemarkt api interface
        return retrofit.create(MakkelijkeMarktApi.class);
    }

    /**
     * Enqueue async api method that will be overriden in the subclass, and make sure its superclass
     * method is always called
     */
    @CallSuper
    public boolean enqueue() {
        boolean ready = false;

        // check network availability
        if (Utility.isNetworkAvailable(mContext)) {

            // build api
            if (mMakkelijkeMarktApi == null) {
                mMakkelijkeMarktApi = build();
            }

            if (mMakkelijkeMarktApi != null) {
                ready = true;
            }
        } else {
            mToast = Utility.showToast(mContext, mToast, mContext.getString(R.string.notice_network_required));
        }

        return ready;
    }

    /**
     * Enqueue async api method with given callback
     * @param callback the object that will process the response
     */
    @CallSuper
    public boolean enqueue(Callback callback) {
        return enqueue();
    }

    /**
     * Event to inform the base activity that we are not authorized to use the api and need to
     * logout the user from the app
     */
    public class OnUnauthorizedEvent {
        public final int mCode;
        public final String mMessage;

        public OnUnauthorizedEvent(int code, String message) {
            mCode = code;
            mMessage = message;
        }
    }
}