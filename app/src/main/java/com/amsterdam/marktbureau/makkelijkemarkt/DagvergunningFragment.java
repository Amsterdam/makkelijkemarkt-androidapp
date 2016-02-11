/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    // bind layout elements
    @Bind(R.id.dagvergunning_tablayout) TabLayout mTabLayout;
    @Bind(R.id.dagvergunning_meldingen_container) LinearLayout mMeldingenContainer;
    @Bind(R.id.dagvergunning_meldingen) LinearLayout mMeldingenPlaceholder;
    @Bind(R.id.dagvergunning_pager) ViewPager mViewPager;

    // viewpager adapter
    private DagvergunningPagerAdapter mPagerAdapter;

    // unique id for the markten loader
    private static final int DAGVERGUNNING_LOADER = 4;

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
    private int mMarktId = -1;
    private String mDag;
    private int mId = -1;
    private String mErkenningsnummer;
    private String mRegistratieDatumtijd;
    private int mTotaleLengte = -1;
    private String mSollicitatieStatus;
    private String mKoopmanAanwezig;
    private int mKoopmanId = -1;
    private String mKoopmanVoorletters;
    private String mKoopmanAchternaam;
    private String mKoopmanFotoMedium;
    private int mRegistratieAccountId = -1;
    private String mRegistratieAccountNaam;
    private int mSollicitatieId = -1;
    private int mSollicitatieNummer = -1;

    /**
     * Constructor
     */
    public DagvergunningFragment() {}

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

                // get the possibly changed values from the currently active pager fragment before switching pages
                getFragmentValuesByPosition(mCurrentTab);

                // get new tab position and switch to new fragment in viewpager
                mCurrentTab = tab.getPosition();
                mViewPager.setCurrentItem(mCurrentTab);

                setFragmentValuesByPosition(mCurrentTab);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // create new view components or restore them from saved state
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
        mPagerAdapter = new DagvergunningPagerAdapter(
                getChildFragmentManager(),
                mTabLayout.getTabCount(),
                mKoopmanFragment,
                mProductFragment,
                mOverzichtFragment);

        // set the fragment pager adapter and add a pagechange listener to update the tab selection
        // important: set the offscreenpagelimit to the amount of fragments we are using minus 1 (the
        // currently active fragment) this makes sure all fragments in the viewpager are attached to
        // the fragmentmanager and can be referenced
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount() - 1);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        return view;
    }

    /**
     * Get data retrieved with the intent, or restore state
     * @param savedInstanceState the previously saved state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get the id of selected market from the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMarktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

        // get the date of today for the dag param
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format_dag));
        mDag = sdf.format(new Date());

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
            }
        } else {

            // restore dagvergunning data from saved state
            mMarktId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID);
            mDag = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_DAG);
            mId = savedInstanceState.getInt(MakkelijkeMarktProvider.Dagvergunning.COL_ID);
            mErkenningsnummer = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE);
            mRegistratieDatumtijd = savedInstanceState.getString(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD);
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

            // select tab of viewpager from saved fragment state
            if (mViewPager.getCurrentItem() != mCurrentTab) {
                mViewPager.setCurrentItem(mCurrentTab);
            }

            Utility.log(getContext(), LOG_TAG, "State restored!");
        }
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
        outState.putString(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD, mRegistratieDatumtijd);
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

        // save viewpager state
        outState.putInt(CURRENT_TAB, mCurrentTab);
        getChildFragmentManager().putFragment(outState, KOOPMAN_FRAGMENT_TAG, mKoopmanFragment);
        getChildFragmentManager().putFragment(outState, PRODUCT_FRAGMENT_TAG, mProductFragment);
        getChildFragmentManager().putFragment(outState, OVERZICHT_FRAGMENT_TAG, mOverzichtFragment);

        Utility.log(getContext(), LOG_TAG, "State saved!");
    }

    /**
     * Populate koopman fragment from local member vars
     */
    private void setKoopmanFragmentValues() {
        if (mKoopmanFragmentReady) {

            // koopman foto
            if (mKoopmanFotoMedium != null) {

                // make the koopman details visible
                mKoopmanFragment.mKoopmanDetail.setVisibility(View.VISIBLE);

                Glide.with(getContext()).load(mKoopmanFotoMedium)
                        .error(R.drawable.no_koopman_image)
                        .into(mKoopmanFragment.mKoopmanFotoImage);
            }

            // koopman naam
            if (mKoopmanVoorletters != null && mKoopmanAchternaam != null) {
                mKoopmanFragment.mKoopmanVoorlettersAchternaamText.setText(
                        mKoopmanVoorletters + " " + mKoopmanAchternaam);
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
            }

            // koopman erkenningsnummer
            if (mErkenningsnummer != null) {
                mKoopmanFragment.mErkenningsnummerText.setText(
                        getString(R.string.label_erkenningsnummer) + ": " + mErkenningsnummer);
            }

            // koopman sollicitatienummer
            if (mSollicitatieNummer != -1) {
                mKoopmanFragment.mSollicitatienummerText.setVisibility(View.VISIBLE);
                mKoopmanFragment.mSollicitatienummerText.setText(
                        getString(R.string.label_sollicitatienummer) + ": " + mSollicitatieNummer);
            }

            // koopman sollicitatie status
            if (mSollicitatieStatus != null && !mSollicitatieStatus.equals("?") && !mSollicitatieStatus.equals("")) {
                mKoopmanFragment.mSollicitatieStatusText.setVisibility(View.VISIBLE);
                mKoopmanFragment.mSollicitatieStatusText.setText(mSollicitatieStatus);
                mKoopmanFragment.mSollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(
                        getContext(),
                        Utility.getSollicitatieStatusColor(getContext(), mSollicitatieStatus)));
            }

            // dagvergunning totale lengte
            if (mTotaleLengte != -1) {
                mKoopmanFragment.mTotaleLengte.setText(mTotaleLengte + " " + getString(R.string.length_meter));
            }

            // registratie account naam
            if (mRegistratieAccountNaam != null) {
                mKoopmanFragment.mAccountNaam.setText(mRegistratieAccountNaam);
            }

            // koopman aanwezig status
            if (mKoopmanAanwezig != null) {
                Utility.log(getContext(), LOG_TAG, "mKoopmanAanwezig=" + mKoopmanAanwezig);
                mKoopmanFragment.setAanwezig(mKoopmanAanwezig);
            }

            Utility.log(getContext(), LOG_TAG, "Koopman populated!");
        }
    }

    public void getKoopmanFragmentValues() {
        if (mKoopmanFragmentReady) {
            mKoopmanAanwezig = mKoopmanFragment.mAanwezigSelectedValue;
        }
    }

    /**
     * Populate product fragment from local member vars
     */
    private void setProductFragmentValues() {
        if (mProductFragmentReady) {

            mProductFragment.mProductTest.setText("Product");

            Utility.log(getContext(), LOG_TAG, "Product populated!");
        }
    }

    private void getProductFragmentValues() {
        if (mProductFragmentReady) {
        }
    }

    /**
     * Populate overzicht fragment from local member vars
     */
    private void setOverzichtFragmentValues() {
        if (mOverzichtFragmentReady) {

            mOverzichtFragment.mOverzichtTest.setText("Overzicht");

            Utility.log(getContext(), LOG_TAG, "Overzicht populated!");
        }
    }

    private void getOverzichtFragmentValues() {
        if (mOverzichtFragmentReady) {
        }
    }

    /**
     * Open/close meldingen view
     * @param view view that needs to collapse
     * @param collapse collapse or open
     */
    private void collapseView(View view, boolean collapse) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();

        if (collapse) {
            lp.height = 0;
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        view.setLayoutParams(lp);
    }

    private void setFragmentValuesByPosition(int position) {
        switch (position) {
            case 0:
                setKoopmanFragmentValues();
                break;
            case 1:
                setProductFragmentValues();
                break;
            case 2:
                setOverzichtFragmentValues();
                break;
            default:
                break;
        }
    }

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

    public void koopmanFragmentReady() {
        mKoopmanFragmentReady = true;
        setKoopmanFragmentValues();
    }

    public void productFragmentReady() {
        mProductFragmentReady = true;
        setProductFragmentValues();
    }

    public void overzichtFragmentReady() {
        mOverzichtFragmentReady = true;
        setOverzichtFragmentValues();
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

            mErkenningsnummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE));
            mRegistratieDatumtijd = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD));
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

            // update their view elements
            setFragmentValuesByPosition(mCurrentTab);
        }
    }

    /**
     * On loader reset, do nothing
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}