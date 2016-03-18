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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.adapters.ErkenningsnummerAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.adapters.SollicitatienummerAdapter;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
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

    public static final String KOOPMAN_SELECTION_METHOD_HANDMATIG = "handmatig";
    public static final String KOOPMAN_SELECTION_METHOD_SCAN_BARCODE = "scan-barcode";
    public static final String KOOPMAN_SELECTION_METHOD_SCAN_NFC = "scan-nfc";

    // bind layout elements
    @Bind(R.id.erkenningsnummer_layout) RelativeLayout mErkenningsnummerLayout;
    @Bind(R.id.search_erkenningsnummer) AutoCompleteTextView mErkenningsnummerEditText;
    @Bind(R.id.sollicitatienummer_layout) RelativeLayout mSollicitatienummerLayout;
    @Bind(R.id.search_sollicitatienummer) AutoCompleteTextView mSollicitatienummerEditText;
    @Bind(R.id.scanbuttons_layout) LinearLayout mScanbuttonsLayout;
    @Bind(R.id.scan_barcode_button) Button mScanBarcodeButton;
    @Bind(R.id.scan_nfctag_button) Button mScanNfcTagButton;
    @Bind(R.id.koopman_detail) LinearLayout mKoopmanDetail;

    // bind dagvergunning list item layout include elements
    @Bind(R.id.koopman_foto) ImageView mKoopmanFotoImage;
    @Bind(R.id.koopman_voorletters_achternaam) TextView mKoopmanVoorlettersAchternaamText;
    @Bind(R.id.dagvergunning_registratie_datumtijd) TextView mRegistratieDatumtijdText;
    @Bind(R.id.erkenningsnummer) TextView mErkenningsnummerText;
    @Bind(R.id.notitie) TextView mNotitieText;
    @Bind(R.id.dagvergunning_totale_lente) TextView mTotaleLengte;
    @Bind(R.id.account_naam) TextView mAccountNaam;
    @Bind(R.id.aanwezig_spinner) Spinner mAanwezigSpinner;

    // existing dagvergunning id
    private int mDagvergunningId = -1;

    // koopman id
    public int mKoopmanId = -1;

    // koopman erkenningsnummer
    public String mErkenningsnummer;

    // keep track of how we selected the koopman
    public String mKoopmanSelectionMethod;

    // string array and adapter for the aanwezig spinner
    private String[] mAanwezigKeys;
    String mAanwezigSelectedValue;

    // sollicitatie default producten data
    public HashMap<String, Integer> mProducten = new HashMap<>();

    // meldingen
    boolean mMeldingMultipleDagvergunningen = false;
    boolean mMeldingNoValidSollicitatie = false;
    boolean mMeldingVerwijderd = false;

    // TODO: melding toevoegen wanneer een koopman vandaag al een vergunning heeft op een andere dan de geselecteerde markt

    // common toast object
    protected Toast mToast;

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
        void onKoopmanFragmentUpdated();
        void onMeldingenUpdated();
    }

    /**
     * Inflate the dagvergunning koopman fragment and initialize the view elements and its handlers
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

        // Create an onitemclick listener that will catch the clicked koopman from the autocomplete
        // lists (not using butterknife here because it does not support for @OnItemClick on
        // AutoCompleteTextView: https://github.com/JakeWharton/butterknife/pull/242
        AdapterView.OnItemClickListener autoCompleteItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor koopman = (Cursor) parent.getAdapter().getItem(position);
                mKoopmanId = koopman.getInt(koopman.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ID));

                // set the koopman selection method to handmatig
                mKoopmanSelectionMethod = KOOPMAN_SELECTION_METHOD_HANDMATIG;

                // reset the default amount of products before loading the koopman
                String[] productParams = getResources().getStringArray(R.array.array_product_param);
                for (String product : productParams) {
                    mProducten.put(product, -1);
                }

                // inform the dagvergunningfragment that the koopman has changed, get the new values,
                // and populate our layout with the new koopman
                ((Callback) getActivity()).onKoopmanFragmentUpdated();

                // hide the keyboard on item selection
                Utility.hideKeyboard(getActivity());
            }
        };

        // create the custom cursor adapter that will query for an erkenningsnummer and show an autocomplete list
        ErkenningsnummerAdapter searchErkenningAdapter = new ErkenningsnummerAdapter(getContext(), null, 0);
        mErkenningsnummerEditText.setAdapter(searchErkenningAdapter);
        mErkenningsnummerEditText.setOnItemClickListener(autoCompleteItemClickListener);

        // create the custom cursor adapter that will query for a sollicitatienummer and show an autocomplete list
        SollicitatienummerAdapter searchSollicitatieAdapter = new SollicitatienummerAdapter(getContext(), null, 0);
        mSollicitatienummerEditText.setAdapter(searchSollicitatieAdapter);
        mSollicitatienummerEditText.setOnItemClickListener(autoCompleteItemClickListener);

        // disable uppercasing of the button text
        mScanBarcodeButton.setTransformationMethod(null);
        mScanNfcTagButton.setTransformationMethod(null);

        // get the aanwezig values from a resource array with aanwezig values
        mAanwezigKeys = getResources().getStringArray(R.array.array_aanwezig_key);

        // populate the aanwezig spinner from a resource array with aanwezig titles
        ArrayAdapter<CharSequence> aanwezigAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_aanwezig_title,
                android.R.layout.simple_spinner_dropdown_item);
        aanwezigAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        mAanwezigSpinner.setAdapter(aanwezigAdapter);

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

        // initialize the producten values
        String[] productParams = getResources().getStringArray(R.array.array_product_param);
        for (String product : productParams) {
            mProducten.put(product, -1);
        }

        Utility.log(getContext(), LOG_TAG, "onActivityCreated called");

        // call the activity
        ((Callback) getActivity()).onKoopmanFragmentReady();
    }

    /**
     * Trigger the autocomplete onclick on the erkennings- en sollicitatenummer search buttons
     * @param view the clicked button
     */
    @OnClick({ R.id.search_erkenningsnummer_button, R.id.search_sollicitatienummer_button })
    public void onClick(ImageButton view) {
        if (view.getId() == R.id.search_erkenningsnummer_button) {
            showDropdown(mErkenningsnummerEditText);
        } else if (view.getId() == R.id.search_sollicitatienummer_button) {
            showDropdown(mSollicitatienummerEditText);
        }
    }

    /**
     * Trigger the autocomplete on enter on the erkennings- en sollicitatenummer search textviews
     * @param view the autocomplete textview
     * @param actionId the type of action
     * @param event the type of keyevent
     */
    @OnEditorAction({ R.id.search_erkenningsnummer, R.id.search_sollicitatienummer })
    public boolean onAutoCompleteEnter(AutoCompleteTextView view, int actionId, KeyEvent event) {
        if ((   (event != null) &&
                (event.getAction() == KeyEvent.ACTION_DOWN) &&
                (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                (actionId == EditorInfo.IME_ACTION_DONE)) {
            showDropdown(view);
        }
        return true;
    }

    /**
     * On selecting an item from the spinner update the member var with the value
     * @param position the selected position
     */
    @OnItemSelected(R.id.aanwezig_spinner)
    public void onAanwezigItemSelected(int position) {
        mAanwezigSelectedValue = mAanwezigKeys[position];
    }

    /**
     * Show the autocomplete dropdown or a notice when the entered text is smaller then the threshold
     * @param view autocomplete textview
     */
    public void showDropdown(AutoCompleteTextView view) {
        if (view.getText() != null && !view.getText().toString().trim().equals("") && view.getText().toString().length() >= view.getThreshold()) {
            view.showDropDown();
        } else {
            Utility.showToast(getContext(), mToast, getString(R.string.notice_autocomplete_minimum));
        }
    }

    /**
     * Set the koopman id and init the loader
     * @param koopmanId id of the koopman
     */
    public void setKoopman(int koopmanId, int dagvergunningId) {
        mKoopmanId = koopmanId;
        mDagvergunningId = dagvergunningId;
        getLoaderManager().restartLoader(KOOPMAN_LOADER, null, this);
    }

    /**
     * Select an item by value in the aanwezig spinner
     * @param value the aanwezig value
     */
    public void setAanwezig(CharSequence value) {
        for(int i=0 ; i< mAanwezigKeys.length; i++) {
            if (mAanwezigKeys[i].equals(value)) {
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
        if (mKoopmanId != -1) {
            CursorLoader loader = new CursorLoader(getActivity());
            loader.setUri(MakkelijkeMarktProvider.mUriKoopmanJoined);
            loader.setSelection(
                    MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID + " = ? "
            );
            loader.setSelectionArgs(new String[] {
                    String.valueOf(mKoopmanId)
            });

            return loader;
        }

        return null;
    }

    /**
     * Populate the koopman fragment item details item when the loader has finished
     * @param loader the cursor loader
     * @param data data object containing one or more koopman rows with joined sollicitatie data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            boolean validSollicitatie = false;

            // get the markt id from the sharedprefs
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

            // make the koopman details visible
            mKoopmanDetail.setVisibility(View.VISIBLE);

            // check koopman status
            String koopmanStatus = data.getString(data.getColumnIndex("koopman_status"));
            mMeldingVerwijderd = koopmanStatus.equals(getString(R.string.koopman_status_verwijderd));

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
            mErkenningsnummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER));
            mErkenningsnummerText.setText(mErkenningsnummer);

            // koopman sollicitaties
            View view = getView();
            if (view != null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout placeholderLayout = (LinearLayout) view.findViewById(R.id.sollicitaties_placeholder);
                placeholderLayout.removeAllViews();

                // get vaste producten for selectd markt, and add multiple markt sollicitatie views to the koopman items
                while (!data.isAfterLast()) {

                    // get vaste producten for selected markt
                    if (marktId > 0 && marktId == data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID))) {
                        String[] productParams = getResources().getStringArray(R.array.array_product_param);
                        for (String product : productParams) {
                            mProducten.put(product, data.getInt(data.getColumnIndex(product)));
                        }
                    }

                    // inflate sollicitatie layout and populate its view items
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
                    sollicitatieStatusText.setText(sollicitatieStatus);
                    if (sollicitatieStatus != null && !sollicitatieStatus.equals("?") && !sollicitatieStatus.equals("")) {
                        sollicitatieStatusText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                        sollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(
                                getContext(),
                                Utility.getSollicitatieStatusColor(getContext(), sollicitatieStatus)));

                        // check if koopman has at least one valid sollicitatie on selected markt
                        if (marktId == data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID))) {
                            validSollicitatie = true;
                        }
                    }

                    // add view and move cursor to next
                    placeholderLayout.addView(childLayout, data.getPosition());
                    data.moveToNext();
                }
            }

            // check valid sollicitatie
            mMeldingNoValidSollicitatie = !validSollicitatie;

            // get the date of today for the dag param
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
            String dag = sdf.format(new Date());

            // check multiple dagvergunningen
            Cursor dagvergunningen = getContext().getContentResolver().query(
                    MakkelijkeMarktProvider.mUriDagvergunningJoined,
                    null,
                    "dagvergunning_doorgehaald != '1' AND " +
                            MakkelijkeMarktProvider.mTableDagvergunning + "." + MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID + " = ? AND " +
                            MakkelijkeMarktProvider.Dagvergunning.COL_DAG + " = ? AND " +
                            MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE + " = ? ",
                    new String[] {
                            String.valueOf(marktId),
                            dag,
                            mErkenningsnummer,
                    },
                    null);
            mMeldingMultipleDagvergunningen = (dagvergunningen != null && dagvergunningen.moveToFirst()) && (dagvergunningen.getCount() > 1 || mDagvergunningId == -1);
            if (dagvergunningen != null) {
                dagvergunningen.close();
            }

            // callback to dagvergunning activity to updaten the meldingen view
            ((Callback) getActivity()).onMeldingenUpdated();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}