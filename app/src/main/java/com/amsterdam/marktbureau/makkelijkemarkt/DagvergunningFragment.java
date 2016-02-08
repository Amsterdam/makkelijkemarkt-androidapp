/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragment.class.getSimpleName();

    // key constants for keeping state
    private static final String DAGVERGUNNING_ID_KEY = "dagvergunning_id";
    private static final String CURRENT_TAB = "current_tab";

    // key constants for viewpagerfragment instance tags
    private static final String KOOPMAN_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentKoopman.class.getSimpleName() + "_TAG";
    private static final String PRODUCT_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentProduct.class.getSimpleName() + "_TAG";
    private static final String OVERZICHT_FRAGMENT_TAG = LOG_TAG + DagvergunningFragmentOverzicht.class.getSimpleName() + "_TAG";

    // bind layout elements
    @Bind(R.id.dagvergunning_tablayout) TabLayout mTabLayout;
    @Bind(R.id.dagvergunning_meldingen_container) LinearLayout mMeldingenContainer;
    @Bind(R.id.dagvergunning_meldingen) LinearLayout mMeldingenPlaceholder;
    @Bind(R.id.dagvergunning_pager) ViewPager mViewPager;

    // viewpager adapter
    private DagvergunningPagerAdapter mPagerAdapter;

    // keep dagvergunning id if we are editing an existing one
    private int mId = -1;

    // koopman data
    // ..

    // product data
    // ..

    // overzicht data
    // ..

    // viewpager fragment references
    DagvergunningFragmentKoopman mKoopmanFragment;
    DagvergunningFragmentProduct mProductFragment;
    DagvergunningFragmentOverzicht mOverzichtFragment;

    // viepager fragment ready state
    private boolean mKoopmanFragmentReady = false;
    private boolean mProductFragmentReady = false;
    private boolean mOverzichtFragmentReady = false;

    // keep the current tab
    private int mCurrentTab = 0;

    /**
     * Constructor
     */
    public DagvergunningFragment() {
    }

    /**
     * Initialize the layout and it's elements
     * @return the fragments view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the markten fragment
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
                mCurrentTab = tab.getPosition();
                mViewPager.setCurrentItem(mCurrentTab);

                Utility.log(getContext(), LOG_TAG, "Switched to tab: " + mCurrentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (savedInstanceState == null) {
            Utility.log(getContext(), LOG_TAG, "viewpager fragments created");
            mKoopmanFragment = new DagvergunningFragmentKoopman();
            mProductFragment = new DagvergunningFragmentProduct();
            mOverzichtFragment = new DagvergunningFragmentOverzicht();
        } else {
            Utility.log(getContext(), LOG_TAG, "viewpager fragments re-used");
            mKoopmanFragment = (DagvergunningFragmentKoopman) getChildFragmentManager().getFragment(savedInstanceState, KOOPMAN_FRAGMENT_TAG);
            mProductFragment = (DagvergunningFragmentProduct) getChildFragmentManager().getFragment(savedInstanceState, PRODUCT_FRAGMENT_TAG);
            mOverzichtFragment = (DagvergunningFragmentOverzicht) getChildFragmentManager().getFragment(savedInstanceState, OVERZICHT_FRAGMENT_TAG);
        }

        // create the fragment pager adapter
        mPagerAdapter = new DagvergunningPagerAdapter(
                getChildFragmentManager(),
                mTabLayout.getTabCount(),
                mKoopmanFragment,
                mProductFragment,
                mOverzichtFragment);

        // create the fragment pager adapter
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mTabLayout)
        );

        return view;
    }

    /**
     * Get date retrieved with the intent, or restore state
     * @param savedInstanceState the previously saved state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        } else {

            // restore fragment state
            mId = savedInstanceState.getInt(DAGVERGUNNING_ID_KEY);
            mCurrentTab = savedInstanceState.getInt(CURRENT_TAB);

            // select tab of viepager from saved fragment state
            if (mViewPager.getCurrentItem() != mCurrentTab) {
                mViewPager.setCurrentItem(mCurrentTab);
            }
        }
    }

    private void populateKoopmanFragment() {
        // TODO: load selected vergunning and populate step fragment elements
//        if (mId > 0) {
//            mKoopmanFragment.mErkenningsnummerText.setText("Vergunning id: " + mId);
//        }
    }

    private void populateProductFragment() {
        // TODO: load selected vergunning and populate step fragment elements
    }

    private void populateOverzichtFragment() {
        // TODO: load selected vergunning and populate step fragment elements
    }

    /**
     * Save the fragment state
     * @param outState state to save to
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save fragment state
        outState.putInt(DAGVERGUNNING_ID_KEY, mId);
        outState.putInt(CURRENT_TAB, mCurrentTab);

        // save viewpager fragments to state
        getChildFragmentManager().putFragment(outState, KOOPMAN_FRAGMENT_TAG, mKoopmanFragment);
        getChildFragmentManager().putFragment(outState, PRODUCT_FRAGMENT_TAG, mProductFragment);
        getChildFragmentManager().putFragment(outState, OVERZICHT_FRAGMENT_TAG, mOverzichtFragment);
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

    public void koopmanFragmentReady() {
        mKoopmanFragmentReady = true;
        populateKoopmanFragment();
    }

    public void productFragmentReady() {
        mProductFragmentReady = true;
        populateProductFragment();
    }

    public void overzichtFragmentReady() {
        mOverzichtFragmentReady = true;
        populateOverzichtFragment();
    }

}