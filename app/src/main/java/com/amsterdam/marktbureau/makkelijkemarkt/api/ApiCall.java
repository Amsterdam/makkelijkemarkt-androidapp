package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.R;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 *
 * @author marcolangebeeke
 */
public class ApiCall {

    // use classname when logging
    protected static final String LOG_TAG = ApiCall.class.getSimpleName();

    // context
    protected Context mContext;

    // api interface
    protected MakkelijkeMarktApi mMakkelijkeMarktApi;

    /**
     *
     * @param context
     */
    public ApiCall(Context context) {

        // get the context
        mContext = context;

        // create the retrofit builder with a gson converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.makkelijkemarkt_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // apply the makkelijkemarkt api interface
        mMakkelijkeMarktApi = retrofit.create(MakkelijkeMarktApi.class);
    }
}