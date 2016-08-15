/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Fragment pager adapter for the add/edit dagvergunning wizard
 *
 * - Important: Changed this from the initial FragmentStatePagerAdapter to the FragmentPagerAdapter
 * because the State version gives problems when you are keeping the state of the fragments yourself
 * - Important: The pager always (re)creates the fragment on the next position. So, when we are
 * on position 0 the fragments 0 and 1 are in a created state. When we move from fragment 0 to
 * fragment 1, it will create fragment 2 (and destroy fragment 0?)
 * - Important: Found that if you tell the viewpager to keep more then 1 offscreen fragments all
 * this becomes irrelevant. So, instead I increased the OffscreenPageLimit to 2 so always all the
 * 3 fragments will be kept attached in the fragmentmanager
 * @author marcolangebeeke
 */
public class DagvergunningFragmentPagerAdapter extends FragmentPagerAdapter {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragment.class.getSimpleName();

    // the amount of fragments
    private int mNumOfFragments;

    // keep a local reference to the pager fragments
    private DagvergunningFragmentKoopman mKoopmanFragment;
    private DagvergunningFragmentProduct mProductFragment;
    private DagvergunningFragmentOverzicht mOverzichtFragment;

    /**
     * Constructor
     * @param fragmentManager the used fragmentmanager
     * @param numOfTabs the amount of pages (fragments)
     * @param koopmanFragment the koopman fragment
     * @param productFragment the product fragment
     * @param overzichtFragment the overzicht fragment
     */
    public DagvergunningFragmentPagerAdapter(
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