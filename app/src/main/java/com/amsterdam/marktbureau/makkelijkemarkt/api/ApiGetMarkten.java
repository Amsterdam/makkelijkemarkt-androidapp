/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.ContentValues;
import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
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
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *
 * @author marcolangebeeke
 */
//public class ApiGetMarkten extends ApiCall implements Callback<List<ApiMarkt>> {
public class ApiGetMarkten implements Callback<List<ApiMarkt>> {

    // use classname when logging
    private static final String LOG_TAG = ApiGetMarkten.class.getSimpleName();

    // context
    protected Context mContext;

    // api interface
    protected MakkelijkeMarktApi mMakkelijkeMarktApi;

    /**
     *
     * @param context
     */
    public ApiGetMarkten(Context context) {
//        super(context);

        // get the context
        mContext = context;



        // @todo refactor to use ApiCall as base class and pass in the httpclient/interceptor optionally in the constructor



        Interceptor convertAanwezigeOpties = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());

                if (response.isSuccessful()) {
                    String aanwezigeOptiesObjectName = "aanwezigeOpties";
                    try {
                        MediaType contentType = response.body().contentType();
                        JSONArray markten = new JSONArray(response.body().string());

                        for(int i = 0; i < markten.length(); i++) {
                            JSONObject markt = markten.getJSONObject(i);
                            Object object = markt.get(aanwezigeOptiesObjectName);
                            if (object instanceof JSONObject) {
                                JSONObject aanwezigeOpties = (JSONObject) object;
                                Iterator<String> iterator = aanwezigeOpties.keys();
                                JSONArray opties = new JSONArray();
                                while (iterator.hasNext()) {
                                    opties.put(iterator.next());
                                }
                                markt.remove(aanwezigeOptiesObjectName);
                                markt.put(aanwezigeOptiesObjectName, opties);
                            }
                        }

                        ResponseBody body = ResponseBody.create(contentType, markten.toString());
                        response = response.newBuilder().body(body).build();

                    } catch (JSONException e) {
                        Utility.log(mContext, LOG_TAG, "Exception creating JSONObject: " + e.getMessage());
                    }
                }

                return response;
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(convertAanwezigeOpties)
                .build();



        // create the retrofit builder with a gson converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.makkelijkemarkt_api_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        // apply the makkelijkemarkt api interface
        mMakkelijkeMarktApi = retrofit.create(MakkelijkeMarktApi.class);
    }

    /**
     *
     */
    public void execute() {

        // set the api function to call for loading the markten
        Call<List<ApiMarkt>> call = mMakkelijkeMarktApi.loadMarkten();

        // call the api asynchronously
        call.enqueue(this);
    }

    /**
     *
     * @param response
     */
    @Override
    public void onResponse(Response<List<ApiMarkt>> response) {
        if (response.body() != null && response.body().size() > 0) {

            // create array for the bulkinsert
            ContentValues[] ContentValuesArray = new ContentValues[response.body().size()];

            for (int i = 0; i < response.body().size(); i++) {
                ApiMarkt markt = response.body().get(i);

                // copy the values and add the to a contentvalues array that can be used in the
                // contentprovider bulkinsert method
                ContentValues marktValues = new ContentValues();
                marktValues.put(MakkelijkeMarktProvider.Markt.COL_ID, markt.getId());
                marktValues.put(MakkelijkeMarktProvider.Markt.COL_NAAM, markt.getNaam());



                // @todo rest van de velden toevoegen



                ContentValuesArray[i] = marktValues;
            }

            // delete existing markten from db
            int deleted = mContext.getContentResolver().delete(MakkelijkeMarktProvider.mUriMarkt, null, null);
            Utility.log(mContext, LOG_TAG, "Markten deleted: " + deleted);

            // insert downloaded markten into db
            int inserted = mContext.getContentResolver().bulkInsert(MakkelijkeMarktProvider.mUriMarkt, ContentValuesArray);
            Utility.log(mContext, LOG_TAG, "Markten inserted: " + inserted);
        }
    }

    /**
     *
     * @param t
     */
    @Override
    public void onFailure(Throwable t) {
        Utility.log(mContext, LOG_TAG, "onFailure message: " + t.getMessage());
    }
}