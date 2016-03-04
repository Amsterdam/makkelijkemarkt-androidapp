/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author marcolangebeeke
 */
public class DrawerFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DrawerFragment.class.getSimpleName();

    // constants linking the position of the drawer menu to an activity
    public final int DRAWER_POSITION_MARKTEN = 0;
    public final int DRAWER_POSITION_DAGVERGUNNINGEN = 1;
    public final int DRAWER_POSITION_NOTITIES = 2;

    // bind layout elements
    @Bind(R.id.drawer_menu) ListView mDrawerMenu;

    /**
     * Constructor
     */
    public DrawerFragment() {
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

        // get the drawer fragment
        View view = inflater.inflate(R.layout.drawer_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // populate the drawer menu options
        mDrawerMenu.setAdapter(new ArrayAdapter<>(getContext(),
                R.layout.drawer_fragment_list_item,
                android.R.id.text1,
                new String[] {
                        getString(R.string.markten),
                        getString(R.string.dagvergunningen),
                        getString(R.string.notities)}));

        return view;
    }

    /**
     * Set the active state of the drawer menu
     * @param position which position to activate
     */
    public void checkItem(int position) {
        mDrawerMenu.setItemChecked(position, true);
    }

    /**
     * Handle clicks on the drawer menuitems for selecting the corresponding activity
     * @param position the clicked menuitem position
     */
    @OnItemClick(R.id.drawer_menu)
    public void selectItem(int position) {
        Intent intent;

        switch (position) {
            case DRAWER_POSITION_MARKTEN:
                intent = new Intent(getActivity(), MarktenActivity.class);
                startActivity(intent);
                break;
            case DRAWER_POSITION_DAGVERGUNNINGEN:
                intent = new Intent(getActivity(), DagvergunningenActivity.class);
                startActivity(intent);
                break;
            case DRAWER_POSITION_NOTITIES:
                intent = new Intent(getActivity(), NotitiesActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}