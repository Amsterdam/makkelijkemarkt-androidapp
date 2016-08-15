/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * @author marcolangebeeke
 */
public class MainFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.login_button) Button mLoginButton;

    /**
     * Callback interface
     */
    public interface Callback {

        // replace a fragment
        void replaceLoginFragment();
    }

    /**
     * Constructor
     */
    public MainFragment() {
    }

    /**
     * Set the fragment layout and attach a click listener to the login button
     * @param inflater inflater object to inflate the layout
     * @param container the parent view container
     * @param savedInstanceState fragment state bundle
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the main_fragment layout
        View mainView = inflater.inflate(R.layout.main_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // disable all caps for the button title
        mLoginButton.setTransformationMethod(null);

        return mainView;
    }

    /**
     * Onclick open the login fragment using a callback to the activity
     */
    @OnClick(R.id.login_button)
    public void openLoginFragment() {

        // use a callback in mainactivity to replace the fragment in the container
        ((Callback) getActivity()).replaceLoginFragment();
    }
}
