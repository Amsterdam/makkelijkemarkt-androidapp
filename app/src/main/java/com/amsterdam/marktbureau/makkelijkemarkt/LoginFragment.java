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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetAccounts;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPostLoginBasicId;
import com.amsterdam.marktbureau.makkelijkemarkt.api.MakkelijkeMarktApiService;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    @Bind(R.id.account) Spinner mAccount;
    @Bind(R.id.password) TextView mPassword;
    @Bind(R.id.login_button) Button mLoginButton;
    @Bind(R.id.progressbar_accounts) ProgressBar mAccountsProgressBar;

    // unique id for the accounts loader
    private static final int ACCOUNTS_LOADER = 1;

    // cursoradapter for populating the account spinner with accounts from the database
    private SimpleCursorAdapter mAccountsAdapter;

    // keep the selected account id
    private int mSelectedAccountId;

    // keep a reference to toast messages
    private Toast mToast;

    // progress dialog for during authentication
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

        if (savedInstanceState == null) {

            // TODO: if there is no internet connection and accounts were never loaded: keep checking for an internet connection and try again

            // check time in hours since last fetched the accounts
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            long diffInHours = getResources().getInteger(R.integer.makkelijkemarkt_api_accounts_fetch_interval_hours);
            if (settings.contains(getContext().getString(R.string.sharedpreferences_key_accounts_last_fetched))) {
                long lastFetchTimestamp = settings.getLong(getContext().getString(R.string.sharedpreferences_key_accounts_last_fetched), 0);
                long differenceMs = new Date().getTime() - lastFetchTimestamp;
                diffInHours = TimeUnit.MILLISECONDS.toHours(differenceMs);
            }

            // update the local accounts by reloading them from the api
            if (diffInHours >= getResources().getInteger(R.integer.makkelijkemarkt_api_accounts_fetch_interval_hours)) {

                // show the progressbar
                mAccountsProgressBar.setVisibility(View.VISIBLE);

                // call the api
                ApiGetAccounts getAccounts = new ApiGetAccounts(getContext());
                if (!getAccounts.enqueue()) {
                    mAccountsProgressBar.setVisibility(View.GONE);
                }
            }
        }

        // create an adapter for the account spinner
        mAccountsAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_list_item_activated_1,
                null,
                new String[] { MakkelijkeMarktProvider.Account.COL_NAAM },
                new int[] { android.R.id.text1 },
                0);

        // attach the adapter to the account spinner
        mAccount.setAdapter(mAccountsAdapter);

        // initiate loading the accounts from the database
        getLoaderManager().initLoader(ACCOUNTS_LOADER, null, this);

        // disable all caps for the button title
        mLoginButton.setTransformationMethod(null);

        // create the login progress dialog
        mLoginProcessDialog = new ProgressDialog(getContext());
        mLoginProcessDialog.setIndeterminate(true);
        mLoginProcessDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_circle));
        mLoginProcessDialog.setMessage(getString(R.string.login) + "...");
        mLoginProcessDialog.setCancelable(false);

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

            // prepare the json payload for the post request from the entered login details
            JsonObject auth = new JsonObject();
            auth.addProperty(getString(R.string.makkelijkemarkt_api_login_payload_accound_id_name), String.valueOf(mSelectedAccountId));
            auth.addProperty(getString(R.string.makkelijkemarkt_api_login_payload_password_name), mPassword.getText().toString());

            // show progress dialog
            mLoginProcessDialog.show();

            // create a login post request and add the account details as json
            ApiPostLoginBasicId postLogin = new ApiPostLoginBasicId(getContext());
            postLogin.setPayload(auth);
            if (!postLogin.enqueue(this)) {
                mLoginProcessDialog.dismiss();
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
        int accountId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_account_id), -1);
        if (accountId != -1) {

            // update selected account id member var
            mSelectedAccountId = accountId;

            // select the account in the spinner dropdown
            if (data.moveToFirst()) {
                while (!data.isAfterLast()) {
                    if (data.getLong(data.getColumnIndex(MakkelijkeMarktProvider.Account.COL_ID)) == mSelectedAccountId) {
                        mAccount.setSelection(data.getPosition());
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

            // start the api service
            Intent apiServiceIntent = new Intent(getContext(), MakkelijkeMarktApiService.class);
            getContext().startService(apiServiceIntent);

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
        mToast = Utility.showToast(getContext(), mToast, getString(R.string.notice_login_failed_connect));
    }

    /**
     * Handle response event from api get accounts request onresponse method to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onGetAccountsResponseEvent(ApiGetAccounts.OnResponseEvent event) {

        // hide progressbar or show an error
        mAccountsProgressBar.setVisibility(View.GONE);
        if (event.mAccountCount == -1) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_accounts_fetch_failed) + ": " + event.mMessage);
        }
    }

    /**
     * Register eventbus handlers
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unregister eventbus handlers
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}