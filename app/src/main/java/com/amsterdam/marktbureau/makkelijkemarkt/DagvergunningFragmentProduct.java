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
public class DagvergunningFragmentProduct extends Fragment {

    /**
     * Constructor
     */
    public DagvergunningFragmentProduct() {
    }

    // callback interface so we can talk back to the activity
    public interface OnReadyListener {
        void onProductFragmentReady();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagvergunning_fragment_product, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // inform the activity that the product fragment is ready so it can be manipulated by the
        // dagvergunning fragment
        ((OnReadyListener) getActivity()).onProductFragmentReady();

        return view;
    }
}