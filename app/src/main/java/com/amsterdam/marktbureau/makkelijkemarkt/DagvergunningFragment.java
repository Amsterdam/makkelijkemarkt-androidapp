/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiDeleteDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiGetKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPostDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPostDagvergunningConcept;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPutDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragment.class.getSimpleName();

    // key constants for referencing viewpager state
    private static final String KOOPMAN_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentKoopman.class.getSimpleName() + "_TAG";
    private static final String PRODUCT_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentProduct.class.getSimpleName() + "_TAG";
    private static final String OVERZICHT_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentOverzicht.class.getSimpleName() + "_TAG";
    private static final String CURRENT_TAB = "current_tab";

    // unique id for the dagvergunning loader
    private static final int DAGVERGUNNING_LOADER = 4;

    // unique id to recognize the callback when receiving the result from the scan nfc activity
    private static final int NFC_SCAN_REQUEST_CODE = 0x00008888;

    // bind layout elements
    @Bind(R.id.dagvergunning_tablayout) TabLayout mTabLayout;
    @Bind(R.id.dagvergunning_meldingen_container) LinearLayout mMeldingenContainer;
    @Bind(R.id.dagvergunning_meldingen) LinearLayout mMeldingenPlaceholder;
    @Bind(R.id.dagvergunning_pager) ViewPager mViewPager;
    @Bind(R.id.progressbar_dagvergunning) ProgressBar mProgressbar;
    @Bind(R.id.wizard_previous) Button mWizardPreviousButton;
    @Bind(R.id.wizard_next) Button mWizardNextButton;

    // viewpager fragment references
    private DagvergunningFragmentKoopman mKoopmanFragment;
    private DagvergunningFragmentProduct mProductFragment;
    private DagvergunningFragmentOverzicht mOverzichtFragment;

    // viepager fragment ready state
    private boolean mKoopmanFragmentReady = false;
    private boolean mProductFragmentReady = false;
    private boolean mOverzichtFragmentReady = false;

    // keep the current tab
    private int mCurrentTab = 0;

    // dagvergunning data
    private int mId = -1;
    private int mMarktId = -1;
    private String mDag;
    private String mErkenningsnummer;
    private String mErkenningsnummerInvoerMethode;
    private int mTotaleLengte = -1;
    private String mSollicitatieStatus;
    private String mKoopmanAanwezig;
    private int mKoopmanId = -1;
    private String mKoopmanVoorletters;
    private String mKoopmanAchternaam;
    private String mKoopmanFotoMedium;
    private int mSollicitatieId = -1;
    private int mSollicitatieNummer = -1;
    private String mNotitie;
    private int mRegistratieAccountId = -1;
    private String mRegistratieAccountNaam;
    private String mRegistratieDatumtijd;
    private double mRegistratieGeolocatieLatitude = -1;
    private double mRegistratieGeolocatieLongitude = -1;

    // environment data
    private int mActiveAccountId = -1;
    private String mActiveAccountNaam;
    private String mDagToday;

    // dagvergunning product data
    private Map<String, Integer> mProducten = new HashMap<>();

    // sollicitatie default product data
    private Map<String, Integer> mProductenVast = new HashMap<>();

    // progress dialog for during saving
    private ProgressDialog mProgressDialog;

    // common toast object
    protected Toast mToast;

    /**
     * Constructor
     */
    public DagvergunningFragment() {
    }

    /**
     * Initialize the layout and it's view elements
     * @return the fragments view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the dagvergunning fragment
        View view = inflater.inflate(R.layout.dagvergunning_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // create the tabs in the tablayout
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.koopman)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.product)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.overzicht)));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * When selecting a tab change the related fragment in the viewpager
             * @param tab the selected tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // switch to the new tab
                switchTab(tab.getPosition());

                // prevent the keyboard from popping up on pager fragment load
                Utility.hideKeyboard(getActivity());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // create new viewpager fragments or restore them from saved state
        if (savedInstanceState == null) {
            mKoopmanFragment = new DagvergunningFragmentKoopman();
            mProductFragment = new DagvergunningFragmentProduct();
            mOverzichtFragment = new DagvergunningFragmentOverzicht();
        } else {
            mCurrentTab = savedInstanceState.getInt(CURRENT_TAB);
            mKoopmanFragment = (DagvergunningFragmentKoopman) getChildFragmentManager()
                    .getFragment(savedInstanceState, KOOPMAN_FRAGMENT_TAG);
            mProductFragment = (DagvergunningFragmentProduct) getChildFragmentManager()
                    .getFragment(savedInstanceState, PRODUCT_FRAGMENT_TAG);
            mOverzichtFragment = (DagvergunningFragmentOverzicht) getChildFragmentManager()
                    .getFragment(savedInstanceState, OVERZICHT_FRAGMENT_TAG);
        }

        // create the fragment pager adapter
        DagvergunningFragmentPagerAdapter pagerAdapter = new DagvergunningFragmentPagerAdapter(
                getChildFragmentManager(),
                mTabLayout.getTabCount(),
                mKoopmanFragment,
                mProductFragment,
                mOverzichtFragment);

        // set the fragment pager adapter and add a pagechange listener to update the tab selection
        // important: set the offscreenpagelimit to the amount of fragments we are using minus 1 (the
        // currently active fragment) this makes sure all fragments in the viewpager are attached to
        // the fragmentmanager and can be referenced
        mViewPager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        // disable upper-casing the wizard menu buttons
        mWizardPreviousButton.setTransformationMethod(null);
        mWizardNextButton.setTransformationMethod(null);

        // create the save progress dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progressbar_circle));
        mProgressDialog.setMessage(getString(R.string.notice_dagvergunning_saving) + "...");
        mProgressDialog.setCancelable(false);

        return view;
    }

    /**
     * Get data retrieved with the intent, or restore state
     * @param savedInstanceState the previously saved state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize the producten
        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, -1);
        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, -1);
        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, -1);
        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, -1);
        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, -1);

        // initialize the vaste producten
        mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, -1);
        mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, -1);
        mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, -1);
        mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, -1);
        mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, -1);

        // get settings from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMarktId = settings.getInt(getString(R.string.sharedpreferences_key_markt_id), 0);
        mActiveAccountId = settings.getInt(getString(R.string.sharedpreferences_key_account_id), 0);
        mActiveAccountNaam = settings.getString(getString(R.string.sharedpreferences_key_account_naam), null);

        // get the date of today for the dag param
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        mDagToday = sdf.format(new Date());

        // only on fragment creation, not on rotation/re-creation
        if (savedInstanceState == null) {

            // check if we are editing an existing dagvergunning or making a new one
            Intent intent = getActivity().getIntent();
            if ((intent != null) && (intent.hasExtra(MakkelijkeMarktProvider.mTableDagvergunning +
                    MakkelijkeMarktProvider.Dagvergunning.COL_ID))) {
                int dagvergunningId = intent.getIntExtra(MakkelijkeMarktProvider.mTableDagvergunning +
                        MakkelijkeMarktProvider.Dagvergunning.COL_ID, 0);

                if (dagvergunningId != 0) {
                    mId = dagvergunningId;
                }
            }

            // init loader if an existing dagvergunning was selected
            if (mId > 0) {

                // create an argument bundle with the dagvergunning id and initialize the loader
                Bundle args = new Bundle();
                args.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID, mId);
                getLoaderManager().initLoader(DAGVERGUNNING_LOADER, args, this);

                // show the progressbar (because we are fetching the koopman from the api later in the onloadfinished)
                mProgressbar.setVisibility(View.VISIBLE);
            }
        } else {

            // restore dagvergunning data from saved state
            mMarktId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID);
            mDag = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_DAG);
            mId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID);
            mErkenningsnummer = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE);
            mErkenningsnummerInvoerMethode = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_METHODE);
            mRegistratieDatumtijd = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD);
            mRegistratieGeolocatieLatitude = savedInstanceState.getDouble(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LAT);
            mRegistratieGeolocatieLongitude = savedInstanceState.getDouble(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LONG);
            mTotaleLengte = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE);
            mSollicitatieStatus = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE);
            mKoopmanAanwezig = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_AANWEZIG);
            mKoopmanId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_KOOPMAN_ID);
            mKoopmanVoorletters = savedInstanceState.getString(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS);
            mKoopmanAchternaam = savedInstanceState.getString(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM);
            mKoopmanFotoMedium = savedInstanceState.getString(MakkelijkeMarktProvider.Koopman.COL_FOTO_MEDIUM_URL);
            mRegistratieAccountId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_ACCOUNT_ID);
            mRegistratieAccountNaam = savedInstanceState.getString(MakkelijkeMarktProvider.Account.COL_NAAM);
            mSollicitatieId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_SOLLICITATIE_ID);
            mSollicitatieNummer = savedInstanceState.getInt(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER);
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST));
            mNotitie = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_NOTITIE);

            // select tab of viewpager from saved fragment state (if it's different)
            if (mViewPager.getCurrentItem() != mCurrentTab) {
                mViewPager.setCurrentItem(mCurrentTab);
            }

            Utility.log(getContext(), LOG_TAG, "State restored!");
        }

        // set the right wizard menu depending on the current tab position
        setWizardMenu(mCurrentTab);

        // prevent the keyboard from popping up on first pager fragment load
        Utility.hideKeyboard(getActivity());
    }

    /**
     * Save the fragment state
     * @param outState state to save to
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // get the possibly changed values from the currently active pager fragments before saving state
        getFragmentValuesByPosition(mCurrentTab);

        // save dagvergunning state
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID, mMarktId);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_DAG, mDag);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID, mId);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE, mErkenningsnummer);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_METHODE, mErkenningsnummerInvoerMethode);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD, mRegistratieDatumtijd);
        outState.putDouble(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LAT, mRegistratieGeolocatieLatitude);
        outState.putDouble(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LONG, mRegistratieGeolocatieLongitude);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE, mTotaleLengte);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE, mSollicitatieStatus);
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_AANWEZIG, mKoopmanAanwezig);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_KOOPMAN_ID, mKoopmanId);
        outState.putString(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS, mKoopmanVoorletters);
        outState.putString(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM, mKoopmanAchternaam);
        outState.putString(MakkelijkeMarktProvider.Koopman.COL_FOTO_MEDIUM_URL, mKoopmanFotoMedium);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_ACCOUNT_ID, mRegistratieAccountId);
        outState.putString(MakkelijkeMarktProvider.Account.COL_NAAM, mRegistratieAccountNaam);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_SOLLICITATIE_ID, mSollicitatieId);
        outState.putInt(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER, mSollicitatieNummer);
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, mProductenVast.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, mProductenVast.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, mProductenVast.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, mProductenVast.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST));
        outState.putInt(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, mProductenVast.get(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST));
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_NOTITIE, mNotitie);

        // save viewpager state
        outState.putInt(CURRENT_TAB, mCurrentTab);
        getChildFragmentManager().putFragment(outState, KOOPMAN_FRAGMENT_TAG, mKoopmanFragment);
        getChildFragmentManager().putFragment(outState, PRODUCT_FRAGMENT_TAG, mProductFragment);
        getChildFragmentManager().putFragment(outState, OVERZICHT_FRAGMENT_TAG, mOverzichtFragment);

        Utility.log(getContext(), LOG_TAG, "State saved!");
    }

    /**
     * Update the meldingen based on the koopmanfragment meldingen data
     */
    public void populateMeldingen() {
        if (mKoopmanFragmentReady) {

            Utility.collapseView(mMeldingenContainer, true);
            mMeldingenPlaceholder.removeAllViews();

            if (mKoopmanFragment.mMeldingVerwijderd || mKoopmanFragment.mMeldingMultipleDagvergunningen || mKoopmanFragment.mMeldingNoValidSollicitatie) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // koopman verwijderd
                if (mKoopmanFragment.mMeldingVerwijderd) {
                    View meldingLayout = layoutInflater.inflate(R.layout.dagvergunning_meldingen_item, null);
                    TextView meldingText = (TextView) meldingLayout.findViewById(R.id.melding);
                    meldingText.setText(getString(R.string.notice_koopman_verwijderd));
                    mMeldingenPlaceholder.addView(meldingLayout, mMeldingenPlaceholder.getChildCount());
                }

                // koopman heeft vandaag al een dagvergunning ontvangen
                if (mKoopmanFragment.mMeldingMultipleDagvergunningen) {
                    View meldingLayout = layoutInflater.inflate(R.layout.dagvergunning_meldingen_item, null);
                    TextView meldingText = (TextView) meldingLayout.findViewById(R.id.melding);
                    meldingText.setText(getString(R.string.notice_koopman_multiple_dagvergunningen));
                    mMeldingenPlaceholder.addView(meldingLayout, mMeldingenPlaceholder.getChildCount());
                }

                // koopman heeft geen geldige sollicitatie
                if (mKoopmanFragment.mMeldingNoValidSollicitatie) {
                    View meldingLayout = layoutInflater.inflate(R.layout.dagvergunning_meldingen_item, null);
                    TextView meldingText = (TextView) meldingLayout.findViewById(R.id.melding);
                    meldingText.setText(getString(R.string.notice_koopman_no_valid_sollicitatie));
                    mMeldingenPlaceholder.addView(meldingLayout, mMeldingenPlaceholder.getChildCount());
                }

                Utility.collapseView(mMeldingenContainer, false);
            }
        }

        Utility.log(getContext(), LOG_TAG, "Meldingen populated!");
    }

    /**
     * Populate koopman fragment from local member vars
     */
    private void populateKoopmanFragment() {
        if (mKoopmanFragmentReady) {

            // for an existing dagvergunning hide the koopman discovery options
            if (mId > 0) {
                Utility.collapseView(mKoopmanFragment.mErkenningsnummerLayout, true);
                Utility.collapseView(mKoopmanFragment.mSollicitatienummerLayout, true);
                Utility.collapseView(mKoopmanFragment.mScanbuttonsLayout, true);
            } else {
                Utility.collapseView(mKoopmanFragment.mErkenningsnummerLayout, false);
                Utility.collapseView(mKoopmanFragment.mSollicitatienummerLayout, false);
                Utility.collapseView(mKoopmanFragment.mScanbuttonsLayout, false);
            }

            // set the koopman details
            if (mKoopmanId > 0) {
                mKoopmanFragment.setKoopman(mKoopmanId, mId);
            }

            // dagvergunning registratie tijd
            if (mRegistratieDatumtijd != null) {
                try {
                    Date registratieDate = new SimpleDateFormat(
                            getString(R.string.date_format_datumtijd),
                            Locale.getDefault()).parse(mRegistratieDatumtijd);
                    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_tijd));
                    String registratieTijd = sdf.format(registratieDate);
                    mKoopmanFragment.mRegistratieDatumtijdText.setText(registratieTijd);
                } catch (java.text.ParseException e) {
                    Utility.log(getContext(), LOG_TAG, "Format registratie tijd failed: " + e.getMessage());
                }
            } else {
                mKoopmanFragment.mRegistratieDatumtijdText.setText("");
            }

            // dagvergunning notitie
            if (mNotitie != null && !mNotitie.equals("")) {
                mKoopmanFragment.mNotitieText.setText(getString(R.string.label_notitie) + ": " + mNotitie);
                Utility.collapseView(mKoopmanFragment.mNotitieText, false);
            } else {
                Utility.collapseView(mKoopmanFragment.mNotitieText, true);
            }

            // dagvergunning totale lengte
            if (mTotaleLengte != -1) {
                mKoopmanFragment.mTotaleLengte.setText(mTotaleLengte + " " + getString(R.string.length_meter));
            }

            // registratie account naam
            if (mRegistratieAccountNaam != null) {
                mKoopmanFragment.mAccountNaam.setText(mRegistratieAccountNaam);
            } else {
                mKoopmanFragment.mAccountNaam.setText("");
            }

            // koopman aanwezig status
            if (mKoopmanAanwezig != null) {
                mKoopmanFragment.setAanwezig(mKoopmanAanwezig);
            }

            Utility.log(getContext(), LOG_TAG, "Koopman populated!");
        }
    }

    /**
     * Get koopman fragment values and update local member vars
     */
    public void getKoopmanFragmentValues() {
        if (mKoopmanFragmentReady) {

            // get the vaste producten for selected koopman sollicitatie
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, mKoopmanFragment.mAantal3MeterKramenVast);
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, mKoopmanFragment.mAantal4MeterKramenVast);
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, mKoopmanFragment.mAantalExtraMetersVast);
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, mKoopmanFragment.mAantalElektraVast);
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, mKoopmanFragment.mAfvaleilandVast);

            // if the producten have not been manually set, set them from the vaste producten
            if (mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN) == -1) {
                mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, mKoopmanFragment.mAantal3MeterKramenVast);
            }
            if (mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN) == -1) {
                mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, mKoopmanFragment.mAantal4MeterKramenVast);
            }
            if (mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS) == -1) {
                mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, mKoopmanFragment.mAantalExtraMetersVast);
            }
            if (mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA) == -1) {
                mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, mKoopmanFragment.mAantalElektraVast);
            }
            if (mProducten.get(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND) == -1) {
                mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, mKoopmanFragment.mAfvaleilandVast);
            }

            // get koopman aanwezig selection
            mKoopmanAanwezig = mKoopmanFragment.mAanwezigSelectedValue;

            // get selected koopman
            if (mKoopmanFragment.mKoopmanId != -1) {

                // if we are changing an previously selected koopman, reset the dagvergunning data
                if (mKoopmanId != -1 && mKoopmanId != mKoopmanFragment.mKoopmanId) {

                    mRegistratieAccountId = mActiveAccountId;
                    mRegistratieAccountNaam = mActiveAccountNaam;
                    mRegistratieDatumtijd = null;
                    mTotaleLengte = -1;

                    // reset aanwezig status and spinner
                    mKoopmanAanwezig = null;
                    mKoopmanFragment.mAanwezigSpinner.setSelection(0);

                    // if we are not editing an existing dagvergunning, get vaste producten from koopman
                    if (mId == -1) {
                        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, mKoopmanFragment.mAantal3MeterKramenVast);
                        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, mKoopmanFragment.mAantal4MeterKramenVast);
                        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, mKoopmanFragment.mAantalExtraMetersVast);
                        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, mKoopmanFragment.mAantalElektraVast);
                        mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, mKoopmanFragment.mAfvaleilandVast);
                    }

                    Utility.log(getContext(), LOG_TAG, "Koopman id changed from: " + mKoopmanId + " to: " + mKoopmanFragment.mKoopmanId);
                }

                // get koopman erkenningsnummer from selected koopman
                mErkenningsnummer = mKoopmanFragment.mErkenningsnummer;

                // if koopman was selected get the selection method
                if (mKoopmanFragment.mKoopmanSelectionMethod != null) {
                    mErkenningsnummerInvoerMethode = mKoopmanFragment.mKoopmanSelectionMethod;
                }

                // get koopman id and update local member var
                mKoopmanId = mKoopmanFragment.mKoopmanId;

                Utility.log(getContext(), LOG_TAG, "Koopman id set: " + mKoopmanId);
            }
        }

        Utility.log(getContext(), LOG_TAG, "getKoopmanFragmentValues called!");
    }

    /**
     * Get the values from the selected koopman in the koopman fragment, and populate the view with
     * selected values. This is called using a activity callback when a(nother) koopman is selected
     * in the koopman fragment
     */
    public void getAndSetKoopmanFragmentValues() {
        getKoopmanFragmentValues();
        populateKoopmanFragment();
    }

    /**
     * Populate product fragment from local member vars
     */
    private void populateProductFragment() {
        if (mProductFragmentReady) {

            // get the product list for selected martk from the shared prefs
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            String producten = settings.getString(getContext().getString(R.string.sharedpreferences_key_markt_producten), null);
            if (producten != null) {

                // split comma-separated string into list with product strings
                List<String> productList = Arrays.asList(producten.split(","));
                if (productList.size() > 0) {

                    String[] productKeys = getResources().getStringArray(R.array.array_product_key);
                    String[] productTypes = getResources().getStringArray(R.array.array_product_type);
                    String[] productColumns = getResources().getStringArray(R.array.array_product_column);

                    // get the product fragment view, find the product count views, and set their text
                    View fragmentView = mProductFragment.getView();
                    for (int i = 0; i < productList.size(); i++) {
                        if (fragmentView != null) {
                            View productView = fragmentView.findViewById(Utility.getResId("product_" + productList.get(i), R.id.class));
                            if (productView != null) {

                                // map the productkey to the productcolumn and get the value from the producten hashmap
                                String productColumn = "";
                                String productType = "";
                                for (int j = 0; j < productKeys.length; j++) {
                                    if (productKeys[j].equals(productList.get(i))) {
                                        productColumn = productColumns[j];
                                        productType = productTypes[j];
                                    }
                                }

                                // get the productcount from the local membervar producten hashmap
                                Integer productCount = mProducten.get(productColumn);
                                if (productCount == -1) {
                                    productCount = 0;
                                }

                                // set value depening on product type
                                if (productType.equals("integer")) {
                                    TextView productCountView = (TextView) productView.findViewById(R.id.product_count);
                                    productCountView.setText(productCount.toString());
                                } else if (productType.equals("boolean") && productCount == 1) {
                                    Switch productCountView = (Switch) productView.findViewById(R.id.product_switch);
                                    productCountView.setChecked(true);
                                }
                            }
                        }
                    }
                }
            }

            // set dagvergunning notitie
            if (mNotitie != null) {
                mProductFragment.mNotitie.setText(mNotitie);
            }

            Utility.log(getContext(), LOG_TAG, "Product populated!");
        }
    }

    /**
     * Get product fragment values and update local member vars
     */
    private void getProductFragmentValues() {
        if (mProductFragmentReady) {

            // get the product list for selected martk from the shared prefs
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            String producten = settings.getString(getContext().getString(R.string.sharedpreferences_key_markt_producten), null);
            if (producten != null) {

                // split comma-separated string into list with product strings
                List<String> productList = Arrays.asList(producten.split(","));
                if (productList.size() > 0) {

                    String[] productKeys = getResources().getStringArray(R.array.array_product_key);
                    String[] productTypes = getResources().getStringArray(R.array.array_product_type);
                    String[] productColumns = getResources().getStringArray(R.array.array_product_column);

                    // get the product fragment view, find the product count views, and get their values
                    View fragmentView = mProductFragment.getView();
                    for (int i = 0; i < productList.size(); i++) {
                        if (fragmentView != null) {
                            View productView = fragmentView.findViewById(Utility.getResId("product_" + productList.get(i), R.id.class));
                            if (productView != null) {

                                // get the corresponding product type and column based on the productlist item value
                                String productType = "";
                                String productColumn = "";
                                for (int j = 0; j < productKeys.length; j++) {
                                    if (productKeys[j].equals(productList.get(i))) {
                                        productType = productTypes[j];
                                        productColumn = productColumns[j];
                                    }
                                }

                                // get value depending on product type
                                String productCount = "0";
                                if (productType.equals("integer")) {
                                    TextView productCountView = (TextView) productView.findViewById(R.id.product_count);
                                    productCount = productCountView.getText().toString();
                                } else if (productType.equals("boolean")) {
                                    Switch productCountView = (Switch) productView.findViewById(R.id.product_switch);
                                    if (productCountView.isChecked()) {
                                        productCount = "1";
                                    }
                                }

                                // update the local product member var
                                if (!productColumn.equals("") && !productCount.equals("")) {

                                    // only set the local member var if the view value not is 0, or if it is 0 and the existing member is not -1
                                    if (!productCount.equals("0") || (productCount.equals("0") && mProducten.get(productColumn) != -1)) {
                                        mProducten.put(productColumn, Integer.valueOf(productCount));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // get dagvergunning notitie value
            if (!mProductFragment.mNotitie.getText().toString().equals("")) {
                mNotitie = mProductFragment.mNotitie.getText().toString();
            } else if (mNotitie != null && !mNotitie.equals("")) {
                mNotitie = null;
            }
        }

        Utility.log(getContext(), LOG_TAG, "getProductFragmentValues called!");
    }

    /**
     * Populate overzicht fragment from local member vars
     */
    private void populateOverzichtFragment() {
        if (mOverzichtFragmentReady) {

            // TODO: show the changes in case we are editing an existing dagvergunning

            // koopman
            if (mKoopmanId > 0) {
                mOverzichtFragment.mKoopmanLinearLayout.setVisibility(View.VISIBLE);
                mOverzichtFragment.mKoopmanEmptyTextView.setVisibility(View.GONE);

                // koopman details
                mOverzichtFragment.setKoopman(mKoopmanId);

                // dagvergunning registratie tijd
                if (mRegistratieDatumtijd != null) {
                    try {
                        Date registratieDate = new SimpleDateFormat(
                                getString(R.string.date_format_datumtijd),
                                Locale.getDefault()).parse(mRegistratieDatumtijd);
                        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_tijd));
                        String registratieTijd = sdf.format(registratieDate);
                        mOverzichtFragment.mRegistratieDatumtijdText.setText(registratieTijd);
                    } catch (java.text.ParseException e) {
                        Utility.log(getContext(), LOG_TAG, "Format registratie tijd failed: " + e.getMessage());
                    }
                } else {
                    mOverzichtFragment.mRegistratieDatumtijdText.setText("");
                }

                // dagvergunning notitie
                if (mNotitie != null && !mNotitie.equals("")) {
                    mOverzichtFragment.mNotitieText.setText(getString(R.string.label_notitie) + ": " + mNotitie);
                    Utility.collapseView(mOverzichtFragment.mNotitieText, false);
                } else {
                    Utility.collapseView(mOverzichtFragment.mNotitieText, true);
                }

                // dagvergunning totale lengte
                if (mTotaleLengte != -1) {
                    mOverzichtFragment.mTotaleLengte.setText(mTotaleLengte + " " + getString(R.string.length_meter));
                }

                // registratie account naam
                if (mRegistratieAccountNaam != null) {
                    mOverzichtFragment.mAccountNaam.setText(mRegistratieAccountNaam);
                } else {
                    mOverzichtFragment.mAccountNaam.setText("");
                }

                // koopman aanwezig status
                if (mKoopmanAanwezig != null) {

                    // get the corresponding aanwezig title from the resource array based on the aanwezig key
                    String[] aanwezigKeys = getResources().getStringArray(R.array.array_aanwezig_key);
                    String[] aanwezigTitles = getResources().getStringArray(R.array.array_aanwezig_title);
                    String aanwezigTitle = "";
                    for (int i = 0; i < aanwezigKeys.length; i++) {
                        if (aanwezigKeys[i].equals(mKoopmanAanwezig)) {
                            aanwezigTitle = aanwezigTitles[i];
                        }
                    }
                    mOverzichtFragment.mAanwezigText.setText(aanwezigTitle);
                }
            } else {
                mOverzichtFragment.mKoopmanEmptyTextView.setVisibility(View.VISIBLE);
                mOverzichtFragment.mKoopmanLinearLayout.setVisibility(View.GONE);
            }

            // product
            if (mErkenningsnummer != null && isProductSelected()) {

                // show progress bar
                mProgressbar.setVisibility(View.VISIBLE);

                // post the dagvergunning details to the api and retrieve a concept 'factuur'
                ApiPostDagvergunningConcept postDagvergunningConcept = new ApiPostDagvergunningConcept(getContext());
                postDagvergunningConcept.setPayload(dagvergunningToJson());
                postDagvergunningConcept.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Response<JsonObject> response) {

                        // hide progress bar
                        mProgressbar.setVisibility(View.GONE);

                        if (response.isSuccess() && response.body() != null) {
                            mOverzichtFragment.mProductenLinearLayout.setVisibility(View.VISIBLE);
                            mOverzichtFragment.mProductenEmptyTextView.setVisibility(View.GONE);

                            // from the response, populate the product section of the overzicht fragment
                            View overzichtView = mOverzichtFragment.getView();
                            if (overzichtView != null) {

                                // find placeholder table layout view
                                TableLayout placeholderLayout = (TableLayout) overzichtView.findViewById(R.id.producten_placeholder);
                                if (placeholderLayout != null) {
                                    placeholderLayout.removeAllViews();
                                    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    // get the producten array
                                    JsonArray producten = response.body().getAsJsonArray(getString(R.string.makkelijkemarkt_api_dagvergunning_concept_producten));

                                    if (producten != null) {
                                        int rowCount = 0;

                                        // table header
                                        View headerLayout = layoutInflater.inflate(R.layout.dagvergunning_overzicht_product_item, null);
                                        TextView btwHeaderText = (TextView) headerLayout.findViewById(R.id.btw_totaal);
                                        btwHeaderText.setText("BTW");
                                        TextView exclusiefHeaderText = (TextView) headerLayout.findViewById(R.id.bedrag_totaal);
                                        exclusiefHeaderText.setText("Ex. BTW");
                                        placeholderLayout.addView(headerLayout, rowCount++);

                                        for (int i = 0; i < producten.size(); i++) {
                                            JsonObject product = producten.get(i).getAsJsonObject();

                                            // get the product item layout
                                            View childLayout = layoutInflater.inflate(R.layout.dagvergunning_overzicht_product_item, null);

                                            // aantal
                                            if (product.get("aantal") != null && !product.get("aantal").isJsonNull()) {
                                                TextView aantalText = (TextView) childLayout.findViewById(R.id.product_aantal);
                                                aantalText.setText(product.get("aantal").getAsInt() + " x ");
                                            }

                                            // naam
                                            if (product.get("naam") != null && !product.get("naam").isJsonNull()) {
                                                TextView naamText = (TextView) childLayout.findViewById(R.id.product_naam);
                                                naamText.setText(Utility.capitalize(product.get("naam").getAsString()));
                                            }

                                            // btw %
                                            if (product.get("btw_percentage") != null && !product.get("btw_percentage").isJsonNull()) {
                                                long btwPercentage = Math.round(Double.parseDouble(product.get("btw_percentage").getAsString()));
                                                if (btwPercentage != 0) {
                                                    TextView btwPercentageText = (TextView) childLayout.findViewById(R.id.btw_percentage);
                                                    btwPercentageText.setText(btwPercentage + "%");
                                                }
                                            }

                                            // btw totaal
                                            if (product.get("btw_totaal") != null && !product.get("btw_totaal").isJsonNull()) {
                                                double btwTotaalProduct = Double.parseDouble(product.get("btw_totaal").getAsString());
                                                TextView btwTotaalText = (TextView) childLayout.findViewById(R.id.btw_totaal);
                                                if (Math.round(btwTotaalProduct) != 0) {
                                                    btwTotaalText.setText(String.format("€ %.2f", btwTotaalProduct));
                                                } else {
                                                    btwTotaalText.setText("-");
                                                }
                                            }

                                            // bedrag totaal
                                            if (product.get("totaal") != null && !product.get("totaal").isJsonNull()) {
                                                double bedragTotaal = Double.parseDouble(product.get("totaal").getAsString());
                                                TextView bedragTotaalText = (TextView) childLayout.findViewById(R.id.bedrag_totaal);
                                                bedragTotaalText.setText(String.format("€ %.2f", bedragTotaal));
                                            }

                                            // add child view
                                            placeholderLayout.addView(childLayout, rowCount++);
                                        }

                                        // exclusief
                                        double exclusief = 0;
                                        if (response.body().get("exclusief") != null && !response.body().get("exclusief").isJsonNull()) {
                                            exclusief = Double.parseDouble(response.body().get("exclusief").getAsString());
                                        }

                                        // totaal
                                        double totaal = 0;
                                        if (response.body().get("totaal") != null && !response.body().get("totaal").isJsonNull()) {
                                            totaal = response.body().get("totaal").getAsDouble();
                                        }

                                        // totaal btw en ex. btw
                                        View totaalLayout = layoutInflater.inflate(R.layout.dagvergunning_overzicht_product_item, null);
                                        TextView naamText = (TextView) totaalLayout.findViewById(R.id.product_naam);
                                        naamText.setText("Totaal");
                                        TextView btwTotaalText = (TextView) totaalLayout.findViewById(R.id.btw_totaal);
                                        if (Math.round(totaal - exclusief) != 0) {
                                            btwTotaalText.setText(String.format("€ %.2f", (totaal - exclusief)));
                                        }
                                        TextView exclusiefText = (TextView) totaalLayout.findViewById(R.id.bedrag_totaal);
                                        exclusiefText.setText(String.format("€ %.2f", exclusief));
                                        placeholderLayout.addView(totaalLayout, rowCount++);

                                        // seperator
                                        View emptyLayout = layoutInflater.inflate(R.layout.dagvergunning_overzicht_product_item, null);
                                        placeholderLayout.addView(emptyLayout, rowCount++);

                                        // totaal inc. btw
                                        View totaalIncLayout = layoutInflater.inflate(R.layout.dagvergunning_overzicht_product_item, null);
                                        TextView totaalNaamText = (TextView) totaalIncLayout.findViewById(R.id.product_naam);
                                        totaalNaamText.setText("Totaal inc. BTW");
                                        TextView totaalIncText = (TextView) totaalIncLayout.findViewById(R.id.bedrag_totaal);
                                        totaalIncText.setText(String.format("€ %.2f", totaal));
                                        placeholderLayout.addView(totaalIncLayout, rowCount);
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {

                        // hide progress bar
                        mProgressbar.setVisibility(View.GONE);

                        Utility.log(getContext(), LOG_TAG, "Failed to load dagvergunning concept!");
                    }
                });
            } else {
                mOverzichtFragment.mProductenLinearLayout.setVisibility(View.GONE);

                if (mKoopmanId > 0) {
                    mOverzichtFragment.mProductenEmptyTextView.setVisibility(View.VISIBLE);
                }
            }

            Utility.log(getContext(), LOG_TAG, "Overzicht populated!");
        }
    }

    /**
     * Get overzicht fragment values and update local member vars
     */
    private void getOverzichtFragmentValues() {
        if (mOverzichtFragmentReady) {}
    }

    /**
     * Post a new, or Put an existing, dagvergunning to the api, and update the db
     */
    private void saveDagvergunning() {

        // check if all required data is available, and show a toast if not
        if (mErkenningsnummer == null) {

            // geen koopman geselecteerd: inform the user and select the koopman tab
            switchTab(0);
            Utility.showToast(getContext(), mToast, getString(R.string.notice_select_koopman));

        } else if (!isProductSelected()) {

            // geen product geselecteerd: inform the user and select the product tab
            switchTab(1);
            Utility.showToast(getContext(), mToast, getString(R.string.notice_select_product));

        } else {

            // show progress dialog
            mProgressDialog.show();

            if (mId == -1) {
                // save new dagvergunning:

                // create a post request and add the dagvergunning details as json
                ApiPostDagvergunning postDagvergunning = new ApiPostDagvergunning(getContext());
                postDagvergunning.setPayload(dagvergunningToJson());
                postDagvergunning.enqueue(new Callback<ApiDagvergunning>() {
                    @Override
                    public void onResponse(Response<ApiDagvergunning> response) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        if (response.isSuccess() && response.body() != null) {

                            // get resulting dagvergunning as ApiDagvergunning object from response and save it to the database
                            ApiDagvergunning dagvergunning = response.body();
                            ContentValues dagvergunningValues = dagvergunning.toContentValues();
                            Uri dagvergunningUri = getContext().getContentResolver().insert(
                                    MakkelijkeMarktProvider.mUriDagvergunning, dagvergunningValues);

                            // on success close current activity and go back to dagvergunningen activity
                            if (dagvergunningUri != null) {
                                getActivity().finish();
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_success));
                            } else {
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                            }
                        } else {
                            Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                    }
                });
            } else {
                // update existing dagvergunning:

                // create put request and add the dagvergunning id as path and details as payload
                ApiPutDagvergunning putDagvergunning = new ApiPutDagvergunning(getContext());
                putDagvergunning.setId(mId);
                putDagvergunning.setPayload(dagvergunningToJson());
                putDagvergunning.enqueue(new Callback<ApiDagvergunning>() {
                    @Override
                    public void onResponse(Response<ApiDagvergunning> response) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        if (response.isSuccess() && response.body() != null) {

                            // get resulting dagvergunning as ApiDagvergunning object from response and update it in the database
                            ApiDagvergunning dagvergunning = response.body();
                            ContentValues dagvergunningValues = dagvergunning.toContentValues();

                            // for some reason the api will create a dagvergunning with a new id, so
                            // we first delete the existing from the db and then insert the new
                            // created dagvergunning that was returned from the api
                            int deleted = getContext().getContentResolver().delete(
                                    MakkelijkeMarktProvider.mUriDagvergunning,
                                    MakkelijkeMarktProvider.Dagvergunning.COL_ID + " = ? ",
                                    new String[] { String.valueOf(mId) }
                            );
                            Uri dagvergunningUri = getContext().getContentResolver().insert(
                                    MakkelijkeMarktProvider.mUriDagvergunning, dagvergunningValues);

                            // on success close current activity and go back to dagvergunningen activity
                            if (deleted == 1 && dagvergunningUri != null) {
                                getActivity().finish();
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_success));
                            } else {
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                            }
                        } else {
                            Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_save_failed));
                    }
                });
            }
        }
    }

    /**
     * Check if at least one product has been selected
     * @return true if a product has been selected, false if not
     */
    public boolean isProductSelected() {
        String[] productColumns = getResources().getStringArray(R.array.array_product_column);
        for(String product: productColumns) {
            if (mProducten.get(product) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a json object from the dagvergunning values
     * @return json object
     */
    public JsonObject dagvergunningToJson() {

        JsonObject dagvergunningPayload = new JsonObject();
        dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_erkenningsnummer), mErkenningsnummer);
        dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_markt_id), mMarktId);
        dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_dag), mDagToday);
        dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_aanwezig), mKoopmanAanwezig);

        // get product values
        String[] productParams = getResources().getStringArray(R.array.array_product_param);
        String[] productColumns = getResources().getStringArray(R.array.array_product_column);
        for (int i = 0; i < productParams.length; i++) {
            if (mProducten.get(productColumns[i]) > 0) {
                dagvergunningPayload.addProperty(productParams[i], mProducten.get(productColumns[i]));
            }
        }

        if (mErkenningsnummerInvoerMethode != null) {
            dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_erkenningsnummer_invoer_methode), mErkenningsnummerInvoerMethode);
        }

        if (mNotitie != null) {
            dagvergunningPayload.addProperty(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_notitie), mNotitie);
        }

        DateFormat datumtijdFormat = new SimpleDateFormat(getString(R.string.date_format_datumtijd));
        dagvergunningPayload.addProperty(
                getString(R.string.makkelijkemarkt_api_dagvergunning_payload_registratie_datumtijd),
                String.valueOf(datumtijdFormat.format(new Date())));

        // add the location from gps
        if (mRegistratieGeolocatieLatitude != -1 && mRegistratieGeolocatieLongitude != -1) {
            JsonArray geolocation = new JsonArray();
            geolocation.add(mRegistratieGeolocatieLatitude);
            geolocation.add(mRegistratieGeolocatieLongitude);
            dagvergunningPayload.add(getString(R.string.makkelijkemarkt_api_dagvergunning_payload_registratie_geolocatie), geolocation);
        }

        return dagvergunningPayload;
    }

    /**
     * Delete an existing dagvergunning
     */
    private void deleteDagvergunning(boolean confirmed) {
        if (mId != -1) {
            if (!confirmed) {

                // show a dialog to ask for confirmation
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.notice_dagvergunning_delete_confirm))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDagvergunning(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.mm_orange)
                        .show();
            } else {

                // show progress dialog
                mProgressDialog.show();

                // create and send the delete request
                ApiDeleteDagvergunning deleteDagvergunning = new ApiDeleteDagvergunning(getContext());
                deleteDagvergunning.setId(mId);
                deleteDagvergunning.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        if (response.isSuccess()) {

                            // delete the dagvergunning from the local db
                            int deleted = getContext().getContentResolver().delete(
                                    MakkelijkeMarktProvider.mUriDagvergunning,
                                    MakkelijkeMarktProvider.Dagvergunning.COL_ID + " = ? ",
                                    new String[] { String.valueOf(mId) }
                            );

                            // on success close current activity and go back to dagvergunningen activity
                            if (deleted == 1) {
                                getActivity().finish();
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_delete_success));
                            } else {
                                Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_delete_failed));
                            }
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {

                        // hide progress dialog
                        mProgressDialog.dismiss();

                        Utility.showToast(getContext(), mToast, getString(R.string.notice_dagvergunning_delete_failed));
                    }
                });
            }
        }
    }

    /**
     * Helper switch to populate fragment elements by viewpager position
     * @param position the position of the fragment in the viewpager
     */
    private void setFragmentValuesByPosition(int position) {
        switch (position) {
            case 0:
                populateKoopmanFragment();
                break;
            case 1:
                populateProductFragment();
                break;
            case 2:
                populateOverzichtFragment();
                break;
            default:
                break;
        }
    }

    /**
     * Helper switch to get the values from the viewpager fragment by viewpager position
     * @param position the position of the fragment in the viewpager
     */
    private void getFragmentValuesByPosition(int position) {
        switch (position) {
            case 0:
                getKoopmanFragmentValues();
                break;
            case 1:
                getProductFragmentValues();
                break;
            case 2:
                getOverzichtFragmentValues();
                break;
            default:
                break;
        }
    }

    /**
     * Set the status of the koopman fragment to ready and set its' values
     */
    public void koopmanFragmentReady() {
        mKoopmanFragmentReady = true;
        if (mCurrentTab == 0) {
            populateKoopmanFragment();

            // attach an onclick listener to the barcode scan button in the koopman fragment here
            // because from the koopman fragment inside the viewpager it seems impossible to get
            // the scan result using the onActivityResult method
            final Fragment dagvergunningFragment = this;
            View view = mKoopmanFragment.getView();
            if (view != null) {

                // barcode scan button
                final Button scanBarcodeButton = (Button) view.findViewById(R.id.scan_barcode_button);
                if (!scanBarcodeButton.hasOnClickListeners()) {
                    scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scanBarcode(dagvergunningFragment);
                        }
                    });
                }

                // nfctag scan button
                final Button scanNfcTagButton = (Button) view.findViewById(R.id.scan_nfctag_button);
                if (!scanNfcTagButton.hasOnClickListeners()) {
                    scanNfcTagButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ScanNfcActivity.class);
                            startActivityForResult(intent, NFC_SCAN_REQUEST_CODE);
                        }
                    });
                }
            }
        }
    }

    /**
     * Launch the barcode scanner
     * @param fragment the fragment we are launching from, and which its' activity will receive
     *                 the callback on result
     */
    private void scanBarcode(Fragment fragment) {

        // create the intent integrator to scan in the current fragment
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(fragment);

        // use a custom scanactivity that can be rotated
        integrator.setCaptureActivity(ScanBarcodeActivity.class);
        integrator.setOrientationLocked(false);

        // limit scanning to only one-dimensional barcodes
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

        // set the prompt message
        integrator.setPrompt(getString(R.string.hint_scan_barcode));

        // launch the scanner
        integrator.initiateScan();
    }

    /**
     * Catch the result of a scanned barcode
     * @param requestCode a code to identity from whom we received the result
     * @param resultCode the type of result
     * @param data the data received with the result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check if from whom we received a callback
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            // from the zxing barcode scanner activity:

            // parse the result in a intentresult object
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                if (result.getContents() == null) {
                    mToast = Utility.showToast(getActivity(), mToast, getString(R.string.notice_scan_barcode_cancelled));
                } else {

                    // get the scanned code
                    String barcode = result.getContents();

                    // find the koopman by querying for scanned barcode (=erkenningsnummer)
                    Cursor koopman = getContext().getContentResolver().query(
                            MakkelijkeMarktProvider.mUriKoopman,
                            new String[] { MakkelijkeMarktProvider.Koopman.COL_ID },
                            MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER + " = ? ",
                            new String[] { barcode },
                            null);

                    // set the koopman
                    if (koopman != null && koopman.moveToFirst()) {
                        mKoopmanFragment.setKoopman(
                                koopman.getInt(koopman.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ID)),
                                mId);
                        mKoopmanFragment.mErkenningsnummerEditText.setText(barcode);
                        mKoopmanFragment.mErkenningsnummerEditText.dismissDropDown();
                        mKoopmanFragment.mKoopmanSelectionMethod =
                                DagvergunningFragmentKoopman.KOOPMAN_SELECTION_METHOD_SCAN_BARCODE;
                    } else {
                        mToast = Utility.showToast(getActivity(), mToast, getString(R.string.notice_koopman_not_found));
                    }

                    // close the cursor
                    if (koopman != null) {
                        koopman.close();
                    }
                }
            }
        } else if (requestCode == NFC_SCAN_REQUEST_CODE) {
            // from the nfc scan activity

            if (resultCode == Activity.RESULT_OK) {
                String uid = data.getStringExtra(getString(R.string.scan_nfc_result_uid));
                if (uid != null) {

                    // lowercase the scanned uid
                    uid = uid.toLowerCase();

                    // show a toast saying that we detected an nfc tag. this will come right after the toast
                    // saying 'NFC tag type not supported.' which can unfortunately not be suppressed
                    mToast = Utility.showToast(getContext(), mToast, "NFC tag detected with UID: " + uid);

                    // find the koopman by querying for scanned nfc tag uid
                    Cursor koopman = getContext().getContentResolver().query(
                            MakkelijkeMarktProvider.mUriKoopman,
                            new String[] {
                                    MakkelijkeMarktProvider.Koopman.COL_ID,
                                    MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER
                            },
                            MakkelijkeMarktProvider.Koopman.COL_NFC_UID + " = ? ",
                            new String[] { uid },
                            null);

                    // set the koopman
                    if (koopman != null && koopman.moveToFirst()) {
                        mKoopmanFragment.setKoopman(
                                koopman.getInt(koopman.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ID)),
                                mId);
                        mKoopmanFragment.mErkenningsnummerEditText.setText(
                                koopman.getString(koopman.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER)));
                        mKoopmanFragment.mErkenningsnummerEditText.dismissDropDown();
                        mKoopmanFragment.mKoopmanSelectionMethod =
                                DagvergunningFragmentKoopman.KOOPMAN_SELECTION_METHOD_SCAN_NFC;
                    } else {
                        mToast = Utility.showToast(getActivity(), mToast, getString(R.string.notice_koopman_not_found));
                    }

                    // close the cursor
                    if (koopman != null) {
                        koopman.close();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mToast = Utility.showToast(getContext(), mToast, getString(R.string.notice_scan_nfc_cancelled));
            }
        }
    }

    /**
     * Set the status of the product fragment to ready and set its' values
     */
    public void productFragmentReady() {
        mProductFragmentReady = true;
        if (mCurrentTab == 1) {
            populateProductFragment();
        }
    }

    /**
     * Set the status of the overzicht fragment to ready and set its' values
     */
    public void overzichtFragmentReady() {
        mOverzichtFragmentReady = true;
        if (mCurrentTab == 2) {
            populateOverzichtFragment();
        }
    }

    /**
     * Set the location values, called from the activity
     * @param location Location object containing the lat and long values
     */
    public void setRegistratieGeoLocatie(Location location) {
        if (location != null) {
            mRegistratieGeolocatieLatitude = location.getLatitude();
            mRegistratieGeolocatieLongitude = location.getLongitude();
        }
    }

    /**
     * When switching tabs get (current tab) and set (new tab) fragment values and update the
     * tabmenu and wizardmenu to the new state
     * @param newTabPosition the new tab position
     */
    public void switchTab(int newTabPosition) {
        if (newTabPosition != mCurrentTab) {

            // get the possibly changed values from the currently active pager fragment before switching pages
            getFragmentValuesByPosition(mCurrentTab);

            // get new tab position and switch to new fragment in viewpager and populate it
            mCurrentTab = newTabPosition;
            mViewPager.setCurrentItem(mCurrentTab);
            setFragmentValuesByPosition(mCurrentTab);

            // update wizard menu buttons
            setWizardMenu(newTabPosition);
        }
    }

    /**
     * Set the state of the buttons of the wizard menu depending on the selected tab
     * @param tabPosition position of the tab
     */
    public void setWizardMenu(int tabPosition) {

        // button background colors
        int accentColor = ContextCompat.getColor(getContext(), R.color.accent);
        int whiteColor = ContextCompat.getColor(getContext(), android.R.color.white);
        int primaryDarkColor = ContextCompat.getColor(getContext(), R.color.primary_dark);
        int redColor = ContextCompat.getColor(getContext(), R.color.dagvergunning_melding_background);

        // button icons
        Drawable leftDrawable = ContextCompat.getDrawable(getContext(), R.drawable.chevron_left_primary_dark);
        Drawable trashDrawable = ContextCompat.getDrawable(getContext(), R.drawable.delete_white);
        Drawable rightDrawable = ContextCompat.getDrawable(getContext(), R.drawable.chevron_right_primary_dark);
        Drawable checkDrawable = ContextCompat.getDrawable(getContext(), R.drawable.check_primary_dark);

        // get previous button left drawable bounds
        Drawable[] previousButtonDrawables = mWizardPreviousButton.getCompoundDrawables();
        Drawable previousButtonLeftDrawable = previousButtonDrawables[0];

        // get next button right drawable bounds
        Drawable[] nextButtonDrawables = mWizardNextButton.getCompoundDrawables();
        Drawable nextButtonRightDrawable = nextButtonDrawables[2];

        switch (tabPosition) {

            // koopman tab
            case 0:
                if (mId == -1) {
                    mWizardPreviousButton.setVisibility(View.INVISIBLE);
                    mWizardPreviousButton.setBackgroundColor(whiteColor);
                    mWizardPreviousButton.setText("");
                    mWizardPreviousButton.setTextColor(primaryDarkColor);
                    if (leftDrawable != null) {
                        leftDrawable.setBounds(previousButtonLeftDrawable.getBounds());
                        mWizardPreviousButton.setCompoundDrawables(leftDrawable, null, null, null);
                    }
                } else {
                    mWizardPreviousButton.setVisibility(View.VISIBLE);
                    mWizardPreviousButton.setBackgroundColor(redColor);
                    mWizardPreviousButton.setText(getString(R.string.delete));
                    mWizardPreviousButton.setTextColor(whiteColor);
                    if (trashDrawable != null) {
                        trashDrawable.setBounds(previousButtonLeftDrawable.getBounds());
                        mWizardPreviousButton.setCompoundDrawables(trashDrawable, null, null, null);
                    }
                }
                mWizardNextButton.setVisibility(View.VISIBLE);
                mWizardNextButton.setBackgroundColor(accentColor);
                mWizardNextButton.setText(getString(R.string.product));
                if (rightDrawable != null) {
                    rightDrawable.setBounds(nextButtonRightDrawable.getBounds());
                    mWizardNextButton.setCompoundDrawables(null, null, rightDrawable, null);
                }
                break;

            // product tab
            case 1:
                mWizardPreviousButton.setVisibility(View.VISIBLE);
                mWizardPreviousButton.setBackgroundColor(whiteColor);
                mWizardPreviousButton.setText(getString(R.string.koopman));
                mWizardPreviousButton.setTextColor(primaryDarkColor);
                if (leftDrawable != null) {
                    leftDrawable.setBounds(previousButtonLeftDrawable.getBounds());
                    mWizardPreviousButton.setCompoundDrawables(leftDrawable, null, null, null);
                }
                mWizardNextButton.setVisibility(View.VISIBLE);
                mWizardNextButton.setBackgroundColor(accentColor);
                mWizardNextButton.setText(getString(R.string.overzicht));
                if (rightDrawable != null) {
                    rightDrawable.setBounds(nextButtonRightDrawable.getBounds());
                    mWizardNextButton.setCompoundDrawables(null, null, rightDrawable, null);
                }
                break;

            // overzicht tab
            case 2:
                mWizardPreviousButton.setVisibility(View.VISIBLE);
                mWizardPreviousButton.setBackgroundColor(whiteColor);
                mWizardPreviousButton.setText(getString(R.string.product));
                mWizardPreviousButton.setTextColor(primaryDarkColor);
                if (leftDrawable != null) {
                    leftDrawable.setBounds(previousButtonLeftDrawable.getBounds());
                    mWizardPreviousButton.setCompoundDrawables(leftDrawable, null, null, null);
                }
                mWizardNextButton.setVisibility(View.VISIBLE);
                mWizardNextButton.setBackgroundColor(accentColor);
                mWizardNextButton.setText(getString(R.string.label_opslaan));
                if (checkDrawable != null) {
                    checkDrawable.setBounds(nextButtonRightDrawable.getBounds());
                    mWizardNextButton.setCompoundDrawables(null, null, checkDrawable, null);
                }
                break;

            default:
                break;
        }
    }

    /**
     * On click on the previous-button, switch to the previous step
     */
    @OnClick(R.id.wizard_previous)
    public void goToPrevious() {
        if (mCurrentTab > 0) {
            switchTab(mCurrentTab - 1);
        } else if (mCurrentTab == 0) {
            deleteDagvergunning(false);
        }
    }

    /**
     * On click on the next-button, either switch to the next step, or if it's the last step,
     * add/update a dagvergunning
     */
    @OnClick(R.id.wizard_next)
    public void goToNext() {
        if (mCurrentTab < 2) {
            switchTab(mCurrentTab + 1);
        } else if (mCurrentTab == 2) {
            saveDagvergunning();
        }
    }

    /**
     * Create the loader that will load the dagvergunning from the db when we are editing an existing one
     * @param id the unique loader id
     * @param args an arguments bundle that contains the dagvergunning id
     * @return a cursor with one record containing the joined dagvergunning details
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // load the dagvergunning with given id in the arguments bundle
        if (args != null && args.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID, 0) != 0) {
            CursorLoader loader = new CursorLoader(getActivity());
            loader.setUri(MakkelijkeMarktProvider.mUriDagvergunningJoined);
            loader.setSelection(
                    MakkelijkeMarktProvider.mTableDagvergunning + "." + MakkelijkeMarktProvider.Dagvergunning.COL_ID + " = ? "
            );
            loader.setSelectionArgs(new String[]{
                    String.valueOf(args.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID, 0))
            });

            return loader;
        }

        return null;
    }

    /**
     * On loading finished get the data that we need for populating the viewpager fragments and set
     * the local dagvergunning vars
     * @param loader the loader
     * @param data a cursor with one record containing the joined dagvergunning details
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            Utility.log(getContext(), LOG_TAG, "dagvergunning loaded!");

            // dagvergunning values
            mErkenningsnummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE));
            mErkenningsnummerInvoerMethode = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_METHODE));
            mRegistratieDatumtijd = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD));
            mRegistratieGeolocatieLatitude = data.getDouble(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LAT));
            mRegistratieGeolocatieLongitude = data.getDouble(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LONG));
            mTotaleLengte = data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE));
            mSollicitatieStatus = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE));
            mKoopmanAanwezig = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANWEZIG));
            mKoopmanId = data.getInt(data.getColumnIndex("koopman_koopman_id"));
            mKoopmanVoorletters = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
            mKoopmanAchternaam = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
            mKoopmanFotoMedium = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_MEDIUM_URL));
            mRegistratieAccountId = data.getInt(data.getColumnIndex("account_account_id"));
            mRegistratieAccountNaam = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Account.COL_NAAM));
            mSollicitatieId = data.getInt(data.getColumnIndex("sollicitatie_sollicitatie_id"));
            mSollicitatieNummer = data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN)));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN)));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS)));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, data.getInt(data.getColumnIndex("dagvergunning_aantal_elektra")));
            mProducten.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, data.getInt(data.getColumnIndex("dagvergunning_afvaleiland")));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST)));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST)));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST)));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST)));
            mProductenVast.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST)));
            mNotitie = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_NOTITIE));

            // update the view elements of the currently selected tab
            setFragmentValuesByPosition(mCurrentTab);

            // load the koopman details from the api (will get his sollicitaties at other markten)
            if (mErkenningsnummer != null && !mErkenningsnummer.equals("")) {
                ApiGetKoopman getKoopman = new ApiGetKoopman(getContext());
                getKoopman.setErkenningsnummer(mErkenningsnummer);
                getKoopman.enqueue();
            }

            // destroy the loader when we are done (this only to prevent it from being called when
            // exiting the activity by navigating back to the dagvergunningen activity)
            getLoaderManager().destroyLoader(DAGVERGUNNING_LOADER);
        }
    }

    /**
     * On loader reset, do nothing
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Handle response event from api get koopman request onresponse method to update our ui
     * @param event the received event
     */
    @Subscribe
    public void onGetKoopmanResponseEvent(ApiGetKoopman.OnResponseEvent event) {

        // hide progressbar or show an error
        mProgressbar.setVisibility(View.GONE);
        if (event.mKoopman == null) {
            mToast = Utility.showToast(getContext(), mToast, getString(R.string.error_koopman_fetch_failed) + ": " + event.mMessage);
        }
    }

    /**
     * Register eventbus handlers
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unregister eventbus handlers
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}