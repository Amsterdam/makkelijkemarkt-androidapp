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
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentKoopman extends Fragment {

    // bind layout elements
    @Bind(R.id.erkenningsnummer) TextView mErkenningsnummerText;
    @Bind(R.id.sollicitatienummer) TextView mSollicitatienummerText;
    @Bind(R.id.scan_barcode_button) Button mScanBarcodeButton;
    @Bind(R.id.scan_nfctag_button) Button mScanNfcTagButton;

    /**
     * Constructor
     */
    public DagvergunningFragmentKoopman() {
    }

    // callback interface so we can talk to the activity
    public interface OnReadyListener {
        void onKoopmanFragmentReady();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagvergunning_fragment_koopman, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // disable uppercasing of the button text
        mScanBarcodeButton.setTransformationMethod(null);
        mScanNfcTagButton.setTransformationMethod(null);

        // inform the activity that the koopman fragment is ready so it can be manipulated by the
        // dagvergunning fragment
        ((OnReadyListener) getActivity()).onKoopmanFragmentReady();

        return view;
    }
}