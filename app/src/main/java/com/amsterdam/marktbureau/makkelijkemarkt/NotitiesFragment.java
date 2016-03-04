/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * @author marcolangebeeke
 */
public class NotitiesFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = NotitiesFragment.class.getSimpleName();

    /**
     * Constructor
     */
    public NotitiesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the dagvergunningen fragment
        View mainView = inflater.inflate(R.layout.notities_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);




        return mainView;
    }
}