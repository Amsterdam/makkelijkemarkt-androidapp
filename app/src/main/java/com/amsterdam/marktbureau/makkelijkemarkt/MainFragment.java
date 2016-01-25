/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
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
    public interface Callback
    {
        // replace a fragment
        void replaceFragment(Fragment fragment);
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

        // disable allcaps for the button title
        mLoginButton.setTransformationMethod(null);

        return mainView;
    }

    /**
     * Onclick open the login fragment using a callback to the activity
     */
    @OnClick(R.id.login_button)
    public void openLoginFragment() {

        // use a callback in mainactivity to replace the fragment in the container
        ((Callback) getActivity()).replaceFragment(new LoginFragment());
    }
}
