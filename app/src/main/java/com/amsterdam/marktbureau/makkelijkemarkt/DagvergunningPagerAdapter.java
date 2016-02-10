/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Fragment pager adapter for the add/edit dagvergunning wizard
 * - Important: Changed this from the initial FragmentStatePagerAdapter to the FragmentPagerAdapter
 * because the State version gives problems when you are keeping the state of the fragments yourself
 * - Important: The pager always (re)creates the fragment on the next position. So, when we are
 * on position 0 the fragments 0 and 1 are in a created state. When we move from fragment 0 to
 * fragment 1, it will create fragment 2 (and destroy fragment 0?)
 * @author marcolangebeeke
 */
public class DagvergunningPagerAdapter extends FragmentPagerAdapter {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragment.class.getSimpleName();

    FragmentManager mFragmentManager;

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

        mFragmentManager = fragmentManager;
        mNumOfFragments = numOfTabs;
        mKoopmanFragment = koopmanFragment;
        mProductFragment = productFragment;
        mOverzichtFragment = overzichtFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // custom logic to add all fragments to the fragmentmanager on the first call
        // TODO: only run it on the first call, and only run it once (using a member var boolean)
        FragmentTransaction mCurTransaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < getCount(); i++) {
            String tmpName = makeFragmentName(container.getId(), getItemId(i));
            Fragment tmpFragment = mFragmentManager.findFragmentByTag(tmpName);
            if (tmpFragment == null) {
                tmpFragment = getItem(i);
                Log.v(LOG_TAG, "Adding item #" + i + ": f=" + tmpFragment);
                mCurTransaction.add(container.getId(), tmpFragment, makeFragmentName(container.getId(), i));
            }
        }
//        mCurTransaction.commitAllowingStateLoss();
        mCurTransaction.commit();
        mFragmentManager.executePendingTransactions();


        // continue default logic from here
        mCurTransaction = mFragmentManager.beginTransaction();

        final long itemId = getItemId(position);

        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            Log.v(LOG_TAG, "Attaching item #" + itemId + ": f=" + fragment);
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            Log.v(LOG_TAG, "Adding item #" + itemId + ": f=" + fragment);
            mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // we don't destroy our fragments
    }

    /**
     * Helper to construct a fragment name of how it exists in the fragmentmanager
     * @param viewId
     * @param id
     * @return
     */
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
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