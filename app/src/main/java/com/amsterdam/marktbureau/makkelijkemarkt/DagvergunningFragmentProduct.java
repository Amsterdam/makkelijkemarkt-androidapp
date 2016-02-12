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
public class DagvergunningFragmentProduct extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentProduct.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.product_test) TextView mProductTest;

    /**
     * Constructor
     */
    public DagvergunningFragmentProduct() {
    }

    /**
     * Callback interface so we can talk back to the activity
     */
    public interface Callback {
        void onProductFragmentReady();
    }

    // TODO: Load the markt with the available products
    // - create a loader that will load the markt details containing the products
    // - create a layout for the products (listview containing an item for each product type?)
    // - populate the local member vars onloadfinished
    // - let the dagvergunning fragment know we are done loading, so we can receive the previously
    //      selected products (for an existing dagvergunning and/or previously entered data)
    // - populate the layout onloadfinished

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagvergunning_fragment_product, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Inform the activity that the product fragment is ready so it can be manipulated by the
     * dagvergunning fragment
     * @param savedInstanceState saved fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Utility.log(getContext(), LOG_TAG, "onActivityCreated called");

        // call the activity
        ((Callback) getActivity()).onProductFragmentReady();
    }
}