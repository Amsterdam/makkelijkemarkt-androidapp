/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Fragment pager adapter for the add/edit dagvergunning wizard
 * - Important: Changed this from the initial FragmentStatePagerAdapter to the FragmentPagerAdapter because
 * the State version gives problems when you are keeping the state of the fragments yourself
 * - Important: The pager always (re)creates the fragment on the next position. So, when we are
 * on position 0 the fragments 0 and 1 are in a created state. When we move from fragment 0 to
 * fragment 1, it will create fragment 2 (and destroy fragment 0?)
 * @author marcolangebeeke
 */
public class DagvergunningPagerAdapter extends FragmentPagerAdapter {

    // the amount of fragments
    int mNumOfFragments;

    // keep a local reference to the pager fragments
    DagvergunningFragmentKoopman mKoopmanFragment;
    DagvergunningFragmentProduct mProductFragment;
    DagvergunningFragmentOverzicht mOverzichtFragment;

    /**
     * Constructor
     * @param fragmentManager the used fragmentmanager
     * @param numOfTabs the amount of pages (fragments)
     * @param koopmanFragment the koopman fragment
     * @param productFragment the product fragment
     * @param overzichtFragment the overzicht fragment
     */
    public DagvergunningPagerAdapter(
            FragmentManager fragmentManager,
            int numOfTabs,
            DagvergunningFragmentKoopman koopmanFragment,
            DagvergunningFragmentProduct productFragment,
            DagvergunningFragmentOverzicht overzichtFragment) {
        super(fragmentManager);

        mNumOfFragments = numOfTabs;
        mKoopmanFragment = koopmanFragment;
        mProductFragment = productFragment;
        mOverzichtFragment = overzichtFragment;
    }

    /**
     * Return the correct fragment by position
     * @param position the position asked for
     * @return the selected fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mKoopmanFragment;
            case 1:
                return mProductFragment;
            case 2:
                return mOverzichtFragment;
            default:
                return null;
        }
    }

    /**
     * Return the amount of fragments in the adapter
     * @return the amount of fragments
     */
    @Override
    public int getCount() {
        return mNumOfFragments;
    }
}