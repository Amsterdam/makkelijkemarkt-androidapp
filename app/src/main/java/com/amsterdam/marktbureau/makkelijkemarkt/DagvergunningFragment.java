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

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import butterknife.ButterKnife;

/**
 * @author marcolangebeeke
 */
public class DagvergunningFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragment.class.getSimpleName();

    // keep dagvergunning id if we are editing an existing one
    private int mId;

    /**
     * Constructor
     */
    public DagvergunningFragment() {
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

        // get the markten fragment
        View mainView = inflater.inflate(R.layout.dagvergunning_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

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
        }

        return mainView;
    }
}