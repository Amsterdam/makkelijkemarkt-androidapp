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
public class DagvergunningenFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningenFragment.class.getSimpleName();

    // bind layout elements
    // ..

    /**
     * Constructor
     */
    public DagvergunningenFragment() {
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the dagvergunningen fragment
        View mainView = inflater.inflate(R.layout.dagvergunningen_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);







        return mainView;
    }
}