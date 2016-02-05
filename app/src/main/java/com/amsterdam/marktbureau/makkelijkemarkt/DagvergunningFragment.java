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

    // bind layout elements
    @Bind(R.id.dagvergunning_tablayout) TabLayout mTabLayout;
    @Bind(R.id.dagvergunning_meldingen_container) LinearLayout mMeldingenContainer;
    @Bind(R.id.dagvergunning_meldingen) LinearLayout mMeldingenPlaceholder;
    @Bind(R.id.dagvergunning_pager) ViewPager mViewPager;

    private DagvergunningPagerAdapter mPagerAdapter;

    // keep dagvergunning id if we are editing an existing one
    private int mId = -1;

    // key constants for keeping state
    private static final String DAGVERGUNNING_ID_KEY = "dagvergunning_id";

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
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // create the fragment pager adapter
        mPagerAdapter = new DagvergunningPagerAdapter(
                getActivity().getSupportFragmentManager(), mTabLayout.getTabCount());

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

        }

        if (mId > 0) {

            Utility.log(getContext(), LOG_TAG, "Dagvergunning id: "+ mId);

            // TODO: load selected vergunning and populate step fragment elements
        }

        // TODO: if the user entered/selected certain elements re-populate them (based on the fragment member vars)
        // TODO: see how to populate fragment elements that are in the step fragments of the viewpager
        // TODO: and the other way round, how to save information entered in the step fragment elements back to the savestate of the main dagvergunning fragment
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

    }

    private void collapseView(View view, boolean collapse) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();

        if (collapse) {
            lp.height = 0;
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        view.setLayoutParams(lp);
    }
}