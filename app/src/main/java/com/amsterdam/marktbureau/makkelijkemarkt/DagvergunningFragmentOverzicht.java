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
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentOverzicht extends Fragment {

    /**
     * Constructor
     */
    public DagvergunningFragmentOverzicht() {
    }

    // callback interface so we can talk back to the activity
    public interface OnReadyListener {
        void onOverzichtFragmentReady();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagvergunning_fragment_overzicht, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // inform the activity that the overzicht fragment is ready so it can be manipulated by the
        // dagvergunning fragment
        ((OnReadyListener) getActivity()).onOverzichtFragmentReady();

        return view;
    }
}