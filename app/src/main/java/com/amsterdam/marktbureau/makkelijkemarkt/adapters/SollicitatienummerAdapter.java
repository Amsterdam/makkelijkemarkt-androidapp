/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */
package com.amsterdam.marktbureau.makkelijkemarkt.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class SollicitatienummerAdapter extends CursorAdapter implements Filterable {

    // use classname when logging
    private static final String LOG_TAG = SollicitatienummerAdapter.class.getSimpleName();

    // keep a reference to the context for access to the contentresolver
    private Context mContext;

    // id of the selected markt
    private int mMarktId;

    /**
     *
     * @param context
     */
    public SollicitatienummerAdapter(Context context) {
        super(context, null, 0);

        mContext = context;

        // get the markt id from the sharedprefs
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMarktId = settings.getInt(mContext.getString(R.string.sharedpreferences_key_markt_id), 0);
    }

    /**
     *
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sollicitatienummer_autocomplete_list_item, parent, false);

        // map the views of the layout using a viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /**
     *
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // get the viewholder layout containing the view items
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // koopman foto
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                .error(R.drawable.no_koopman_image)
                .into(viewHolder.foto);

        // sollicitatienummer
        String sollicitatienummer = cursor.getString(
                cursor.getColumnIndex(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
        viewHolder.sollicitatienummer.setText(sollicitatienummer);

        // koopman status
        String koopmanStatus = cursor.getString(cursor.getColumnIndex("koopman_status"));
        viewHolder.koopmanStatus.setText(koopmanStatus);

        // voorletters + achternaam
        String voorletters = cursor.getString(
                cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
        String achternaam = cursor.getString(
                cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
        viewHolder.naam.setText(voorletters + " " + achternaam);
    }

    /**
     * Return what we want to show in the textview after selection from the list
     * @param cursor the cursor on the selected position
     * @return a string containing the sollicitatienummer
     */
    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(
                MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
    }

    /**
     *
     * @param sollicitatienummer
     * @return
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence sollicitatienummer) {

        // limit the result so we never send thousands of records to the adapter
        String limit = "10";

        // select only koopmannen with actieve sollicitaties voor geselecteerde markt
        return mContext.getContentResolver().query(
                MakkelijkeMarktProvider.mUriKoopmanJoinedGroupBySollicitatienummer,
                new String[] {
                        MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ID,
                        MakkelijkeMarktProvider.mTableSollicitatie + "." + MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER,
                        MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS,
                        MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM,
                        MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_FOTO_URL,
                        MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_STATUS
                },
                MakkelijkeMarktProvider.mTableSollicitatie + "." + MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID + " = ? AND " +
                MakkelijkeMarktProvider.mTableSollicitatie + "." + MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER + " LIKE ? ",
                new String[] {
                        String.valueOf(mMarktId),
                        "%" + sollicitatienummer + "%"
                },
                MakkelijkeMarktProvider.mTableKoopman + "." + MakkelijkeMarktProvider.Koopman.COL_STATUS + " ASC, " +
                MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER + " ASC " +
                        " LIMIT " + limit
        );
    }

    /**
     * Viewholder helper class for easy referencing the view elements
     */
    public static class ViewHolder {

        // bind the elements
        @Bind(R.id.koopman_foto) ImageView foto;
        @Bind(R.id.sollicitatienummer) TextView sollicitatienummer;
        @Bind(R.id.koopman_status) TextView koopmanStatus;
        @Bind(R.id.naam) TextView naam;

        /**
         * Bind the view elements using butterknife
         * @param view the listitem view
         */
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}