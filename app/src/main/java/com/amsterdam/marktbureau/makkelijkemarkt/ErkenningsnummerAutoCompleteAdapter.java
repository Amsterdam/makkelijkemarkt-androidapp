/**
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author marcolangebeeke
 */
public class ErkenningsnummerAutoCompleteAdapter extends CursorAdapter implements Filterable {

    // use classname when logging
    private static final String LOG_TAG = ErkenningsnummerAutoCompleteAdapter.class.getSimpleName();

    private Context mContext;

    public ErkenningsnummerAutoCompleteAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.erkenningsnummer_autocomplete_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // get the viewholder layout containing the view items
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // photo
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                .error(R.drawable.no_koopman_image)
                .into(viewHolder.foto);

        // erkenningsnummer
        String erkenningsnummer = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER));
        viewHolder.erkenningsnummer.setText(erkenningsnummer);

        // voorletters + achternaam
        String voorletters = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
        String achternaam = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
        viewHolder.naam.setText(voorletters + " " + achternaam);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER));
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence erkenningsnummer) {
        return mContext.getContentResolver().query(
                MakkelijkeMarktProvider.mUriKoopman,
                new String[] {
                        MakkelijkeMarktProvider.Koopman.COL_ID,
                        MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER,
                        MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS,
                        MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM,
                        MakkelijkeMarktProvider.Koopman.COL_FOTO_URL
                },
                MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER + " LIKE ?",
                new String[] {
                        "%" + erkenningsnummer + "%"
                },
                MakkelijkeMarktProvider.Koopman.COL_ERKENNINGSNUMMER + " ASC"
        );
    }

    /**
     * Viewholder helper class for easy referencing the view elements
     */
    public static class ViewHolder {

        // bind the elements
        @Bind(R.id.koopman_foto) ImageView foto;
        @Bind(R.id.erkenningsnummer) TextView erkenningsnummer;
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