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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentOverzicht extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentOverzicht.class.getSimpleName();

    // unique id for the koopman loader
    private static final int KOOPMAN_LOADER = 6;

    // bind layout elements
    @Bind(R.id.koopman) LinearLayout mKoopmanLinearLayout;
    @Bind(R.id.koopman_empty) TextView mKoopmanEmptyTextView;
    @Bind(R.id.producten) LinearLayout mProductenLinearLayout;
    @Bind(R.id.producten_empty) TextView mProductenEmptyTextView;
    @Bind(R.id.vervanger_detail) LinearLayout mVervangerDetail;

    @Bind(R.id.koopman_foto) ImageView mKoopmanFotoImage;
    @Bind(R.id.koopman_voorletters_achternaam) TextView mKoopmanVoorlettersAchternaamText;
    @Bind(R.id.dagvergunning_registratie_datumtijd) TextView mRegistratieDatumtijdText;
    @Bind(R.id.erkenningsnummer) TextView mErkenningsnummerText;
    @Bind(R.id.notitie) TextView mNotitieText;
    @Bind(R.id.dagvergunning_totale_lente) TextView mTotaleLengte;
    @Bind(R.id.account_naam) TextView mAccountNaam;
    @Bind(R.id.aanwezig) TextView mAanwezigText;

    @Bind(R.id.vervanger_foto) ImageView mVervangerFotoImage;
    @Bind(R.id.vervanger_voorletters_achternaam) TextView mVervangerVoorlettersAchternaamText;
    @Bind(R.id.vervanger_erkenningsnummer) TextView mVervangerErkenningsnummerText;

    // koopman id
    private int mKoopmanId = -1;

    /**
     * Constructor
     */
    public DagvergunningFragmentOverzicht() {
    }

    /**
     * Callback interface so we can talk back to the activity
     */
    public interface Callback {
        void onOverzichtFragmentReady();
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
        View view = inflater.inflate(R.layout.dagvergunning_fragment_overzicht, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Inform the activity that the overzicht fragment is ready so it can be manipulated by the
     * dagvergunning fragment
     * @param savedInstanceState saved fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // call the activity
        ((Callback) getActivity()).onOverzichtFragmentReady();
    }

    /**
     * Initialize the loader with given koopman id
     * @param koopmanId id of the koopman
     */
    public void setKoopman(int koopmanId) {
        mKoopmanId = koopmanId;
        getLoaderManager().restartLoader(KOOPMAN_LOADER, null, this);
    }

    /**
     * Load a vervanger and populate the details
     * @param vervangerId koopman id of the vervanger
     */
    public void setVervanger(int vervangerId) {
        if (vervangerId > 0) {

            // load the vervanger from the database
            Cursor vervanger = getContext().getContentResolver().query(
                    MakkelijkeMarktProvider.mUriKoopman,
                    new String[] {
                            MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER,
                            MakkelijkeMarktProvider.Koopman.COL_FOTO_URL,
                            MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS,
                            MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM
                    },
                    MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID + " = ? ",
                    new String[] {
                            String.valueOf(vervangerId),
                    },
                    null);

            // populate the vervanger layout item
            if (vervanger != null) {
                if (vervanger.moveToFirst()) {

                    // show the details layout
                    mVervangerDetail.setVisibility(View.VISIBLE);

                    // vervanger photo
                    Glide.with(getContext())
                            .load(vervanger.getString(vervanger.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                            .error(R.drawable.no_koopman_image)
                            .into(mVervangerFotoImage);

                    // vervanger naam
                    String naam = vervanger.getString(vervanger.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS)) + " " +
                            vervanger.getString(vervanger.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
                    mVervangerVoorlettersAchternaamText.setText(naam);

                    // vervanger erkenningsnummer
                    mVervangerErkenningsnummerText.setText(
                            vervanger.getString(vervanger.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER)));
                }

                vervanger.close();
            }
        }
    }

    /**
     * Create the cursor loader that will load the koopman from the database
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // load the koopman with given id
        if (mKoopmanId != -1) {
            CursorLoader loader = new CursorLoader(getActivity());
            loader.setUri(MakkelijkeMarktProvider.mUriKoopmanJoined);
            loader.setSelection(
                    MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID + " = ? "
            );
            loader.setSelectionArgs(new String[] {
                    String.valueOf(mKoopmanId),
            });

            return loader;
        }

        return null;
    }

    /**
     * Populate the koopman fragment item details item when the loader has finished
     * @param loader the cursor loader
     * @param data data object containing one or more koopman rows with joined sollicitatie data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // get the markt id from the sharedprefs
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            int marktId = settings.getInt(getContext().getString(R.string.sharedpreferences_key_markt_id), 0);

            // koopman photo
            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                    .error(R.drawable.no_koopman_image)
                    .into(mKoopmanFotoImage);

            // koopman naam
            String naam =
                    data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS)) + " " +
                    data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
            mKoopmanVoorlettersAchternaamText.setText(naam);

            // koopman erkenningsnummer
            String erkenningsnummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER));
            mErkenningsnummerText.setText(erkenningsnummer);

            // koopman sollicitaties
            View view = getView();
            if (view != null) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout placeholderLayout = (LinearLayout) view.findViewById(R.id.sollicitaties_placeholder);
                placeholderLayout.removeAllViews();

                // add multiple markt sollicitatie views to the koopman items
                while (!data.isAfterLast()) {

                    // inflate sollicitatie layout and populate its view items
                    View childLayout = layoutInflater.inflate(R.layout.dagvergunning_koopman_item_sollicitatie, null);

                    // highlight the sollicitatie for the current markt
                    if (data.getCount() > 1 && marktId > 0 && marktId == data.getInt(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID))) {
                        childLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
                    }

                    // markt afkorting
                    String marktAfkorting = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Markt.COL_AFKORTING));
                    TextView marktAfkortingText = (TextView) childLayout.findViewById(R.id.sollicitatie_markt_afkorting);
                    marktAfkortingText.setText(marktAfkorting);

                    // koopman sollicitatienummer
                    String sollicitatienummer = data.getString(data.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
                    TextView sollicitatienummerText = (TextView) childLayout.findViewById(R.id.sollicitatie_sollicitatie_nummer);
                    sollicitatienummerText.setText(sollicitatienummer);

                    // koopman sollicitatie status
                    String sollicitatieStatus = data.getString(data.getColumnIndex("sollicitatie_status"));
                    TextView sollicitatieStatusText = (TextView) childLayout.findViewById(R.id.sollicitatie_status);
                    sollicitatieStatusText.setText(sollicitatieStatus);
                    if (sollicitatieStatus != null && !sollicitatieStatus.equals("?") && !sollicitatieStatus.equals("")) {
                        sollicitatieStatusText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                        sollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(
                                getContext(),
                                Utility.getSollicitatieStatusColor(getContext(), sollicitatieStatus)));
                    }

                    // add view and move cursor to next
                    placeholderLayout.addView(childLayout, data.getPosition());
                    data.moveToNext();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}