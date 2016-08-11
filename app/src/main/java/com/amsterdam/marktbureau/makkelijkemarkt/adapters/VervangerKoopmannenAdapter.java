/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
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
public class VervangerKoopmannenAdapter extends CursorAdapter {

    // use classname when logging
    private static final String LOG_TAG = VervangerKoopmannenAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     */
    public VervangerKoopmannenAdapter(Context context) {
        super(context, null, 0);
    }

    /**
     * Inflate the vervanger_koopman_list_item view and attach the viewholder for easy referencing
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // create a new view from the vervanger_koopman_list_item definition
        View view = LayoutInflater.from(context).inflate(R.layout.vervanger_koopmannen_list_item, parent, false);

        // set a viewholder with our view layout for the view we just created
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /**
     * Bind the cursor data to the view elements
     * @param view listitem view containing the elements
     * @param context the context
     * @param cursor a cursor containing the dagvergunning data
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // get the viewholder layout containing the view items
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // foto
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                .error(R.drawable.no_koopman_image)
                .into(viewHolder.koopmanFoto);

        // naam
        String koopmanVoorletters = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
        String koopmanAchternaam = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
        viewHolder.koopmanVoorlettersAchternaamText.setText(koopmanVoorletters + " " + koopmanAchternaam);

        // erkenningsnummer
        viewHolder.erkenningsnummerText.setText(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER))
        );

        // status
        viewHolder.koopmanStatusText.setText(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_STATUS))
        );
    }

    /**
     * Viewholder helper class for easy referencing the view elements
     */
    public static class ViewHolder {

        // bind the elements
        @Bind(R.id.foto) ImageView koopmanFoto;
        @Bind(R.id.naam) TextView koopmanVoorlettersAchternaamText;
        @Bind(R.id.status) TextView koopmanStatusText;
        @Bind(R.id.erkenningsnummer) TextView erkenningsnummerText;

        /**
         * Bind the view elements using butterknife
         * @param view the listitem view
         */
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}