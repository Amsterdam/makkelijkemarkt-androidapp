/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPostLoginBasicId;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.gson.JsonObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class LoginFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        Callback<JsonObject> {

    // use classname when logging
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.account) Spinner mAcount;
    @Bind(R.id.password) TextView mPassword;
    @Bind(R.id.login_button) Button mLoginButton;

    // unique id for the accounts loader
    private static final int ACCOUNTS_LOADER = 0;

    // cursoradapter for populating the account spinner with accounts from the database
    private SimpleCursorAdapter mAccountsAdapter;

    // keep the selected account id
    private int mSelectedAccountId;

    // keep a reference to toast messages
    private Toast mToast;

    private ProgressDialog mLoginProcessDialog;

    /**
     * Constructor
     */
    public LoginFragment() {
    }

    /**
     * Set the login fragment layout and initialize the login logic
     * @param inflater inflater object to inflate the layout
     * @param container the parent view container
     * @param savedInstanceState fragment state bundle
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the login fragment layout
        View mainView =  inflater.inflate(R.layout.login_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // create an adapter for the account spinner
        mAccountsAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_list_item_activated_1,
                null,
                new String[] { MakkelijkeMarktProvider.Account.COL_NAAM },
                new int[] { android.R.id.text1 },
                0);

        // attach the adapter to the account spinner
        mAcount.setAdapter(mAccountsAdapter);

        // inititate loading the accounts from the database
        getLoaderManager().initLoader(ACCOUNTS_LOADER, null, this);

        // disable allcaps for the button title
        mLoginButton.setTransformationMethod(null);

        // create the login progress dialog
        mLoginProcessDialog = new ProgressDialog(getContext());
        mLoginProcessDialog.setMessage(getString(R.string.login) + "...");
        mLoginProcessDialog.setCancelable(false);
        mLoginProcessDialog.setIndeterminate(true);

        return mainView;
    }

    /**
     * When selecting an account in the dropdown update the selected account id and naam
     * @param position position in the list
     */
    @OnItemSelected(R.id.account)
    public void onItemSelected(int position) {

        // get the account id and naam from the selected item
        Cursor selectedAccount = (Cursor) mAccountsAdapter.getItem(position);
        mSelectedAccountId = selectedAccount.getInt(selectedAccount.getColumnIndex(MakkelijkeMarktProvider.Account.COL_ID));
        String naam = selectedAccount.getString(selectedAccount.getColumnIndex(MakkelijkeMarktProvider.Account.COL_NAAM));

        // add account id and naam to the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(getString(R.string.sharedpreferences_key_account_id), mSelectedAccountId);
        editor.putString(getString(R.string.sharedpreferences_key_account_naam), naam);
        editor.apply();
    }

    /**
     * OnClick on the login button authenticate the user with the api
     */
    @OnClick(R.id.login_button)
    public void authenticateAccount() {

        // if user entered a password try to authenticate with the api
        if (mPassword.getText().toString().trim().equals("")) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.notice_login_enter_password));
        } else {

            // check if we have a network
            if (Utility.isNetworkAvailable(getContext())) {

                // @todo add deviceUuid, clientApp, and clientVersion in the post request (see login/basicId api doc)

                // prepare the json payload for the post request from the entered login details
                JsonObject auth = new JsonObject();
                auth.addProperty(getString(R.string.makkelijkemarkt_api_login_payload_accound_id_name), String.valueOf(mSelectedAccountId));
                auth.addProperty(getString(R.string.makkelijkemarkt_api_login_payload_password_name), mPassword.getText().toString());

                // show progress dialog
                mLoginProcessDialog.show();

                // create a login post request and add the account details as json
                ApiPostLoginBasicId postLogin = new ApiPostLoginBasicId(getContext());
                postLogin.setPayload(auth);
                postLogin.enqueue(this);

            } else {
                mToast = Utility.showToast(getActivity(), mToast, getString(R.string.network_required_notice));
            }
        }
    }

    /**
     * Create the cursorloader that will load the accounts from the db
     * @param id the unique id given in the initloader call
     * @param args the arguments given in the initloader call
     * @return the cursor containing the accounts when loaded from the db
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // create the loader
        CursorLoader loader = new CursorLoader(getActivity());
        loader.setUri(MakkelijkeMarktProvider.mUriAccount);
        loader.setProjection(new String[]{
                MakkelijkeMarktProvider.Account.COL_ID,
                MakkelijkeMarktProvider.Account.COL_NAAM
        });
        loader.setSortOrder(
                MakkelijkeMarktProvider.Account.COL_NAAM +" ASC"
        );

        return loader;
    }

    /**
     * Add the loaded accounts into the accounts adapter when done loading from the db
     * @param loader the accounts loader attached to the spinner
     * @param data the loaded data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // populate the adapter with the loaded accounts
        mAccountsAdapter.swapCursor(data);

        // get the id of previously selected account from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        int accountId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_account_id), 0);
        if (accountId != 0) {

            // update selected account id member var
            mSelectedAccountId = accountId;

            // select the account in the spinner dropdown
            if (data.moveToFirst()) {
                while (!data.isAfterLast()) {
                    if (data.getLong(data.getColumnIndex(MakkelijkeMarktProvider.Account.COL_ID)) == mSelectedAccountId) {
                        mAcount.setSelection(data.getPosition());
                        break;
                    }
                    data.moveToNext();
                }
            }
        }
    }

    /**
     * Clear the accounts from the loader
     * @param loader adapter containing the accounts
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccountsAdapter.swapCursor(null);
    }

    /**
     * Handle the response of the Api login post request
     * @param response the response of the post request, containing a gson object
     */
    @Override
    public void onResponse(Response<JsonObject> response) {

        // hide progress dialog
        mLoginProcessDialog.dismiss();

        if (response.isSuccess() && response.body() != null) {

            // get the api key from the response gson
            String uuid = response.body().get(getString(R.string.makkelijkemarkt_api_uuid_name)).getAsString();

            // store the uuid in the shared preferences
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(getString(R.string.sharedpreferences_key_uuid), uuid);
            editor.apply();

            // open the markten activity
            Intent intent = new Intent(getActivity(), MarktenActivity.class);
            startActivity(intent);

        } else {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.notice_login_password_invalid));
        }
    }

    /**
     *
     * @param t
     */
    @Override
    public void onFailure(Throwable t) {

        // hide progress dialog
        mLoginProcessDialog.dismiss();

        // @todo show an error ? => research in which possible cases onFailure is called

        Utility.log(getContext(), LOG_TAG, "onFailure message: "+ t.getMessage());
    }
}