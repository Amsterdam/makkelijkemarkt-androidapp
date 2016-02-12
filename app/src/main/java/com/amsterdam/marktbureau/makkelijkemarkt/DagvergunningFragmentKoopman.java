/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentKoopman extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentKoopman.class.getSimpleName();

    // unique id for the koopman loader
    private static final int KOOPMAN_LOADER = 5;

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
    @Bind(R.id.dagvergunning_totale_lente) TextView mTotaleLengte;
    @Bind(R.id.account_naam) TextView mAccountNaam;
    @Bind(R.id.aanwezig_spinner) Spinner mAanwezigSpinner;

    // string array and adapter for the aanwezig spinner
    private String[] mAanwezigValues;
    private ArrayAdapter<CharSequence> mAanwezigAdapter;
    String mAanwezigSelectedValue;

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
        View mainView = inflater.inflate(R.layout.dagvergunning_fragment_koopman, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

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

        return mainView;
    }

    /**
     * Inform the activity that the koopman fragment is ready so it can be manipulated by the
     * dagvergunning fragment
     * @param savedInstanceState saved fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Utility.log(getContext(), LOG_TAG, "onActivityCreated called");

        // call the activity
        ((Callback) getActivity()).onKoopmanFragmentReady();
    }

    /**
     * On selecting an item from the spinner update the member var with the value
     * @param position the selected position
     */
    @OnItemSelected(R.id.aanwezig_spinner)
    public void onItemSelected(int position) {
        mAanwezigSelectedValue = mAanwezigValues[position];
    }

    /**
     * Initialize the loader with given koopman id
     * @param koopmanId id of the koopman
     */
    public void setKoopman(int koopmanId) {
        Bundle args = new Bundle();
        args.putInt(MakkelijkeMarktProvider.Koopman.COL_ID, koopmanId);
        getLoaderManager().initLoader(KOOPMAN_LOADER, args, this);
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

    /**
     * Create the cursor loader that will load the koopman from the database
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // load the koopman with given id in the arguments bundle and where doorgehaald is false
        if (args != null && args.getInt(MakkelijkeMarktProvider.Koopman.COL_ID, 0) != 0) {
            CursorLoader loader = new CursorLoader(getActivity());
            loader.setUri(MakkelijkeMarktProvider.mUriKoopmanJoined);
            loader.setSelection(
                    MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID + " = ? AND " +
                    MakkelijkeMarktProvider.mTableSollicitatie + "." + MakkelijkeMarktProvider.Sollicitatie.COL_DOORGEHAALD + " = ? "
            );
            loader.setSelectionArgs(new String[]{
                    String.valueOf(args.getInt(MakkelijkeMarktProvider.Koopman.COL_ID, 0)),
                    String.valueOf(0)
            });

            return loader;
        }

        return null;
    }

    /**
     * Populate the koopman fragment details item from the db call
     * @param loader the cursor loader
     * @param data data object containing one or more koopman rows with joined sollicitatie data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // get the markt id from the sharedprefs
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

            // make the koopman details visible
            mKoopmanDetail.setVisibility(View.VISIBLE);

            // koopman photo
            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_MEDIUM_URL)))
                    .error(R.drawable.no_koopman_image)
                    .into(mKoopmanFotoImage);

            // koopman naam
            String naam =
                    data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS)) + " " +
                    data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
            mKoopmanVoorlettersAchternaamText.setText(naam);

            // koopman erkenningsnummer
            String erkenningsnummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER));
            mErkenningsnummerText.setText(erkenningsnummer);

            // koopman sollicitaties
            View view = getView();
            if (view != null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout placeholderLayout = (LinearLayout) view.findViewById(R.id.sollicitaties_placeholder);
                placeholderLayout.removeAllViews();

                // add multiple markt sollicitatie views
                while (!data.isAfterLast()) {
                    View childLayout = layoutInflater.inflate(R.layout.dagvergunning_koopman_item_sollicitatie, null);

                    // highlight the sollicitatie for the current markt
                    if (data.getCount() > 1 && marktId > 0 && marktId == data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID))) {
                        childLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
                    }

                    // markt afkorting
                    String marktAfkorting = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_AFKORTING));
                    TextView marktAfkortingText = (TextView) childLayout.findViewById(R.id.sollicitatie_markt_afkorting);
                    marktAfkortingText.setText(marktAfkorting);

                    // koopman sollicitatienummer
                    String sollicitatienummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
                    TextView sollicitatienummerText = (TextView) childLayout.findViewById(R.id.sollicitatie_sollicitatie_nummer);
                    sollicitatienummerText.setText(sollicitatienummer);

                    // koopman sollicitatie status
                    String sollicitatieStatus = data.getString(data.getColumnIndex("sollicitatie_status"));
                    TextView sollicitatieStatusText = (TextView) childLayout.findViewById(R.id.sollicitatie_status);
                    if (sollicitatieStatus != null && !sollicitatieStatus.equals("?") && !sollicitatieStatus.equals("")) {
                        sollicitatieStatusText.setText(sollicitatieStatus);
                        sollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(
                                getContext(),
                                Utility.getSollicitatieStatusColor(getContext(), sollicitatieStatus)));
                    }

                    // add view and move cursor to next
                    placeholderLayout.addView(childLayout, data.getPosition());
                    data.moveToNext();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}