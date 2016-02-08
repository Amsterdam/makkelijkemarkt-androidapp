/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfFragments;

    DagvergunningFragmentKoopman mKoopmanFragment;
    DagvergunningFragmentProduct mProductFragment;
    DagvergunningFragmentOverzicht mOverzichtFragment;

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

    @Override
    public int getCount() {
        return mNumOfFragments;
    }
}