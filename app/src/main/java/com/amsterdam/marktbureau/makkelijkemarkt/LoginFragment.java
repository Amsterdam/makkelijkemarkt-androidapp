/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author marcolangebeeke
 */
public class LoginFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.account) Spinner mAcount;
    @Bind(R.id.password) TextView mPassword;
    @Bind(R.id.login_button) Button mLoginButton;

    // cursoradapter for populating the account spinner with accounts from the database
    public CursorAdapter mAccountsAdapter;

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



        mAccountsAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_list_item_activated_1,
                null,
                new String[] { MakkelijkeMarktProvider.Account.COL_NAAM },
                new int[] { android.R.id.text1 },
                0);

        mAcount.setAdapter(mAccountsAdapter);

        getLoaderManager().initLoader(0, null, this);






        // disable allcaps for the button title
        mLoginButton.setTransformationMethod(null);

        return mainView;
    }

    /**
     * Authenticate the user
     */
    @OnClick(R.id.login_button)
    public void authenticateAccount() {

        Utility.log(getContext(), LOG_TAG, "authenticateAccount");

        // @todo handle the login submit with selected account and password

    }





    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MakkelijkeMarktProvider.mUriAccount,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAccountsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccountsAdapter.swapCursor(null);
    }
}

