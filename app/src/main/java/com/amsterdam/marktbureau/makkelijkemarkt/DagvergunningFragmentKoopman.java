/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentKoopman extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentKoopman.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.search_erkenningsnummer) EditText mErkenningsnummerEditText;
    @Bind(R.id.search_sollicitatienummer) TextView mSollicitatienummerEditText;
    @Bind(R.id.scan_barcode_button) Button mScanBarcodeButton;
    @Bind(R.id.scan_nfctag_button) Button mScanNfcTagButton;
    @Bind(R.id.koopman_detail) LinearLayout mKoopmanDetail;

    // bind dagvergunning list item layout include elements
    @Bind(R.id.koopman_foto) ImageView mKoopmanFotoImage;
    @Bind(R.id.koopman_voorletters_achternaam) TextView mKoopmanVoorlettersAchternaamText;
    @Bind(R.id.dagvergunning_registratie_datumtijd) TextView mRegistratieDatumtijdText;
    @Bind(R.id.erkenningsnummer) TextView mErkenningsnummerText;
    @Bind(R.id.sollicitatie_sollicitatie_nummer) TextView mSollicitatienummerText;
    @Bind(R.id.sollicitatie_status) TextView mSollicitatieStatusText;
    @Bind(R.id.dagvergunning_totale_lente) TextView mTotaleLengte;
    @Bind(R.id.account_naam) TextView mAccountNaam;
    @Bind(R.id.aanwezig_spinner) Spinner mAanwezigSpinner;

    // string array and adapter for the aanwezig spinner
    private String[] mAanwezigValues;
    private ArrayAdapter<CharSequence> mAanwezigAdapter;
    private String mAanwezigSelectedValue;

    /**
     * Constructor
     */
    public DagvergunningFragmentKoopman() {
    }

    /**
     * Callback interface so we can talk to the activity
     */
    public interface Callback {
        void onKoopmanFragmentReady();
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
        View view = inflater.inflate(R.layout.dagvergunning_fragment_koopman, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // disable uppercasing of the button text
        mScanBarcodeButton.setTransformationMethod(null);
        mScanNfcTagButton.setTransformationMethod(null);

        // get the aanwezig values from a resource array with aanwezig values
        mAanwezigValues = getResources().getStringArray(R.array.array_aanwezig_value);

        // populate the aanwezig spinner from a resource array with aanwezig titles
        mAanwezigAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_aanwezig_title,
                android.R.layout.simple_spinner_dropdown_item);
        mAanwezigAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        mAanwezigSpinner.setAdapter(mAanwezigAdapter);

        // inform the activity that the koopman fragment is ready so it can be manipulated by the
        // dagvergunning fragment
        ((Callback) getActivity()).onKoopmanFragmentReady();

        return view;
    }

    /**
     * On selecting an item from the spinner update the member var with the value
     * @param position the selected position
     */
    @OnItemSelected(R.id.aanwezig_spinner)
    public void onItemSelected(int position) {
        mAanwezigSelectedValue = mAanwezigValues[position];
        Utility.log(getContext(), LOG_TAG, "Aanwezig: "+ mAanwezigSelectedValue);
    }

    /**
     * Select an item by value in the aanwezig spinner
     * @param value the aanwezig value
     */
    public void setAanwezig(CharSequence value) {
        for(int i=0 ; i<mAanwezigValues.length; i++) {
            if (mAanwezigValues[i].equals(value)) {
                mAanwezigSpinner.setSelection(i);
                break;
            }
        }
    }
}