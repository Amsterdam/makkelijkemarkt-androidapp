/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPostNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class NotitieFragment extends Fragment implements Callback<ApiNotitie> {

    // use classname when logging
    private static final String LOG_TAG = NotitieFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.notitie_bericht) EditText mBerichtEditText;
    @Bind(R.id.notitie_cancel) Button mCancelButton;
    @Bind(R.id.notitie_save) Button mSaveButton;

    // progress dialog for during saving
    private ProgressDialog mProgressDialog;

    // common toast object
    protected Toast mToast;

    /**
     * Constructor
     */
    public NotitieFragment() {
    }

    /**
     * Inflate the notitie_fragment layout containing the about text from strings resource
     * @param inflater LayoutInflator
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the notitie fragment
        View view = inflater.inflate(R.layout.notitie_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // disable upper-casing the menu buttons
        mCancelButton.setTransformationMethod(null);
        mSaveButton.setTransformationMethod(null);

        // create the save progress dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_circle));
        mProgressDialog.setMessage(getString(R.string.notice_notitie_saving) + "...");
        mProgressDialog.setCancelable(false);

        return view;
    }

    /**
     * Onclick cancel and go back
     */
    @OnClick(R.id.notitie_cancel)
    public void cancelNotitie() {
        getActivity().finish();
    }

    /**
     * Onclick save the notitie
     */
    @OnClick(R.id.notitie_save)
    public void saveNotitie() {
        if (!mBerichtEditText.getText().toString().trim().isEmpty()) {
            JsonObject notitiePayload = new JsonObject();

            // show progress dialog
            mProgressDialog.show();

            // bericht
            String bericht = mBerichtEditText.getText().toString().trim();
            notitiePayload.addProperty(getString(R.string.makkelijkemarkt_api_notitie_payload_bericht), bericht);

            // get the markt id from the shared preferences
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            int marktId = settings.getInt(getString(R.string.sharedpreferences_key_markt_id), 0);
            notitiePayload.addProperty(getString(R.string.makkelijkemarkt_api_notitie_payload_markt_id), marktId);

            // get the date of today for the dag param
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
            String dag = sdf.format(new Date());
            notitiePayload.addProperty(getString(R.string.makkelijkemarkt_api_notitie_payload_dag), dag);

            // send post call to the api
            ApiPostNotitie postNotitie = new ApiPostNotitie(getContext());
            postNotitie.setPayload(notitiePayload);
            postNotitie.enqueue(this);

        } else {
            Utility.showToast(getContext(), mToast, getString(R.string.notice_notitie_enter_text));
        }
    }

    /**
     * On response from the api post call save the notitie to the database and return
     * @param response resulting response apinotitie object
     */
    @Override
    public void onResponse(Response<ApiNotitie> response) {

        // hide progress dialog
        mProgressDialog.dismiss();

        if (response.isSuccess() && response.body() != null) {

            // get resulting notitie as ApiNotitie object from response and save it to the database
            ApiNotitie notitie = response.body();
            ContentValues notitieValues = notitie.toContentValues();
            Uri notitieUri = getContext().getContentResolver().insert(
                    MakkelijkeMarktProvider.mUriNotitie, notitieValues);

            // on success close current activity and go back to notities activity
            if (notitieUri != null) {
                getActivity().finish();
                Utility.showToast(getContext(), mToast, getString(R.string.notice_notitie_save_success));
            } else {
                Utility.showToast(getContext(), mToast, getString(R.string.notice_notitie_save_failed));
            }
        } else {
            Utility.showToast(getContext(), mToast, getString(R.string.notice_notitie_save_failed));
        }
    }

    /**
     * On failure show a toast
     * @param t the error thrown
     */
    @Override
    public void onFailure(Throwable t) {

        // hide progress dialog
        mProgressDialog.dismiss();

        Utility.showToast(getContext(), mToast, getString(R.string.notice_notitie_save_failed));
    }
}