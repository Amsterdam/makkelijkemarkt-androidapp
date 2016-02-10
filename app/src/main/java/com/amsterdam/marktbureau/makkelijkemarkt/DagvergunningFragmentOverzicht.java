/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentOverzicht extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentOverzicht.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.overzicht_test) TextView mOverzichtTest;

    /**
     * Constructor
     */
    public DagvergunningFragmentOverzicht() {
    }

    /**
     * Callback interface so we can talk back to the activity
     */
    public interface Callback {
        void onOverzichtFragmentReady();
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
        View view = inflater.inflate(R.layout.dagvergunning_fragment_overzicht, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Inform the activity that the overzicht fragment is ready so it can be manipulated by the
     * dagvergunning fragment
     * @param savedInstanceState saved fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Utility.log(getContext(), LOG_TAG, "onActivityCreated called");

        // call the activity
        ((Callback) getActivity()).onOverzichtFragmentReady();
    }
}