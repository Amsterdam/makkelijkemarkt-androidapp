/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import android.content.Context;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

/**
 *
 * @author marcolangebeeke
 */
public class ApiPostLoginBasicId extends ApiCall {

    // use classname when logging
    private static final String LOG_TAG = ApiPostLoginBasicId.class.getSimpleName();

    /**
     * Call the superclass constructor to set the context
     * @param context the context
     */
    public ApiPostLoginBasicId(Context context) {
        super(context);
    }

    /**
     * Enqueue the async post
     * @param callback the object that will process the api response
     */
    @Override
    public boolean enqueue(Callback callback) {
        if (super.enqueue(callback)) {

            // set the api function to call for loading the accounts
            if (callback != null && mPayload != null) {

                // get details about the device and app and add them to the post request payload
                addClientInfo();

                // create the call
                Call<JsonObject> call = mMakkelijkeMarktApi.postLoginBasicId(mPayload);

                // call the api asynchronously
                call.enqueue(callback);
            }

            return true;
        }

        return false;
    }

    /**
     * Get details about the device and app and add them to the call payload
     */
    private void addClientInfo() {
        if (mPayload == null) {
            mPayload = new JsonObject();
        }

        // add device serialnumber
        String deviceSerialNumber = Utility.getSerialNumber();
        if (deviceSerialNumber != null && !deviceSerialNumber.equals("")) {
            mPayload.addProperty(mContext.getString(R.string.makkelijkemarkt_api_login_payload_device_uuid_name), deviceSerialNumber);
        }

        // add client app
        mPayload.addProperty(
                mContext.getString(R.string.makkelijkemarkt_api_login_payload_client_app_name),
                Utility.getAppName(mContext));

        // add client version
        mPayload.addProperty(
                mContext.getString(R.string.makkelijkemarkt_api_login_payload_client_version_name),
                Utility.getAppVersion(mContext));
    }
}