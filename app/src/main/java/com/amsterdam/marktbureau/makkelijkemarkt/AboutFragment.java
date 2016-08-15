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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class AboutFragment extends Fragment {

    // bind layout elements
    @Bind(R.id.about_text) WebView mAboutWebView;

    /**
     * Constructor
     */
    public AboutFragment() {
    }

    /**
     * Inflate the about_fragment layout containing the about text from strings resource
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // get the about fragment
        View view =  inflater.inflate(R.layout.about_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // load the about text from the assets depending on the locale
        String about_html = "about-nl.html";
        Locale current = getResources().getConfiguration().locale;
        if (current.getISO3Language().equals("eng")) {
            about_html = "about-en.html";
        }
        mAboutWebView.loadUrl("file:///android_asset/" + about_html);

        return view;
    }
}