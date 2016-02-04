/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningenListAdapter extends CursorAdapter {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningenListAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     * @param cursor
     * @param flags
     */
    public DagvergunningenListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    /**
     * Inflate the dagvergunningen_list_item view and attach the viewholder for easy referencing
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // create a new view from the dagvergunningen_list_item definition
        View view = LayoutInflater.from(context).inflate(R.layout.dagvergunningen_list_item, parent, false);

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

        // koopman foto
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_MEDIUM_URL)))
                .error(R.drawable.no_koopman_image)
                .into(viewHolder.koopmanFoto);

        // koopman naam
        String koopmanVoorletters = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
        String koopmanAchternaam = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
        viewHolder.koopmanVoorlettersAchternaamText.setText(koopmanVoorletters + " " + koopmanAchternaam);

        // registratietijd
        String registratieDatumtijd = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD));
        viewHolder.dagvergunningRegistratieDatumtijdText.setText(registratieDatumtijd);
        try {
            Date registratieDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(registratieDatumtijd);
            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.date_format_tijd));
            String registratieTijd = sdf.format(registratieDate);
            viewHolder.dagvergunningRegistratieDatumtijdText.setText(registratieTijd);
        } catch (java.text.ParseException e) {}

        // erkennings nummer
        String erkenningsnummer = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE));
        viewHolder.erkenningsnummerText.setText(context.getString(R.string.label_erkenningsnummer) + ": " + erkenningsnummer);

        // sollicitatie nummer
        String sollicitatieNummer = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
        if (sollicitatieNummer != null && !sollicitatieNummer.equals("")) {
            viewHolder.sollicitatieSollicitatieNummerText.setVisibility(View.VISIBLE);
            viewHolder.sollicitatieSollicitatieNummerText.setText(context.getString(R.string.label_sollicitatienummer) + ": " + sollicitatieNummer);
        }

        // sollicitatie status
        String sollicitatieStatus = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE));
        if (sollicitatieStatus != null && !sollicitatieStatus.equals("")) {
            viewHolder.sollicitatieStatusText.setVisibility(View.VISIBLE);
            viewHolder.sollicitatieStatusText.setText(sollicitatieStatus);

            // map the color for the sollicitatie status
            int sollicitatieStatusColor = R.color.sollicitatie_status_undefined;
            if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_lot))) {
                sollicitatieStatusColor = R.color.sollicitatie_status_lot;
            } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_soll))) {
                sollicitatieStatusColor = R.color.sollicitatie_status_soll;
            } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_vkk))) {
                sollicitatieStatusColor = R.color.sollicitatie_status_vkk;
            } else if (sollicitatieStatus.equals(context.getString(R.string.sollicitatie_status_vpl))) {
                sollicitatieStatusColor = R.color.sollicitatie_status_vpl;
            }
            viewHolder.sollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(context, sollicitatieStatusColor));
        }

        // totale lengte
        String totaleLengte = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE));
        viewHolder.dagvergunningTotaleLengteText.setText(totaleLengte + " " + context.getString(R.string.length_meter));

        // account naam
        String accountNaam = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Account.COL_NAAM));
        viewHolder.accountNaamText.setText(accountNaam);
    }

    /**
     * Viewholder helper class for easy referencing the view elements
     */
    public static class ViewHolder {

        // bind the elements
        @Bind(R.id.koopman_foto) ImageView koopmanFoto;

        @Bind(R.id.koopman_naam_tijd) RelativeLayout koopmanNaamTijd;
        @Bind(R.id.koopman_voorletters_achternaam) TextView koopmanVoorlettersAchternaamText;
        @Bind(R.id.dagvergunning_registratie_datumtijd) TextView dagvergunningRegistratieDatumtijdText;

        @Bind(R.id.erkenningsnummer) TextView erkenningsnummerText;

        @Bind(R.id.sollicitatie_nummer_status) RelativeLayout sollicitatieNummerStatus;
        @Bind(R.id.sollicitatie_sollicitatie_nummer) TextView sollicitatieSollicitatieNummerText;
        @Bind(R.id.sollicitatie_status) TextView sollicitatieStatusText;

        @Bind(R.id.totalelengte_accountnaam) RelativeLayout totaleLengteAccountNaam;
        @Bind(R.id.dagvergunning_totale_lente) TextView dagvergunningTotaleLengteText;
        @Bind(R.id.account_naam) TextView accountNaamText;

        /**
         * Bind the view elements using butterknife
         * @param view the listitem view
         */
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}