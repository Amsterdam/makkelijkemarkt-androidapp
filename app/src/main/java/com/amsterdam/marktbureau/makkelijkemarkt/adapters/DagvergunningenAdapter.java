/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.adapters;

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

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
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
public class DagvergunningenAdapter extends CursorAdapter {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningenAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     */
    public DagvergunningenAdapter(Context context) {
        super(context, null, 0);
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
        boolean multipleDagvergunningen = false;

        // get erkenningsnummer for checking multiple dagvergunningen for the same koopman
        String erkenningsnummer = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE));

        // get current position, check for multiple dagvergunningen, and restore position again
        int position = cursor.getPosition();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(
                    MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE))
                    .equals(erkenningsnummer) && cursor.getPosition() != position) {
                multipleDagvergunningen = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor.moveToPosition(position);

        // get the viewholder layout containing the view items
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // koopman foto
        Glide.with(context.getApplicationContext()).load(
                cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_FOTO_URL)))
                .error(R.drawable.no_koopman_image)
                .into(viewHolder.koopmanFoto);

        // alert !
        String koopmanStatus = cursor.getString(cursor.getColumnIndex("koopman_status"));
        if (multipleDagvergunningen || koopmanStatus.equals(context.getString(R.string.koopman_status_verwijderd))) {
            viewHolder.koopmanStatusText.setVisibility(View.VISIBLE);
        } else {
            viewHolder.koopmanStatusText.setVisibility(View.GONE);
        }

        // koopman naam
        String koopmanVoorletters = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_VOORLETTERS));
        String koopmanAchternaam = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Koopman.COL_ACHTERNAAM));
        viewHolder.koopmanVoorlettersAchternaamText.setText(koopmanVoorletters + " " + koopmanAchternaam);

        // vervanger aanwezig
        String vervangerErkenningsnummer = cursor.getString(
                cursor.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_VERVANGER_ERKENNINGSNUMMER));
        if (vervangerErkenningsnummer != null) {
            viewHolder.vervangerAanwezigText.setVisibility(View.VISIBLE);
        } else {
            viewHolder.vervangerAanwezigText.setVisibility(View.GONE);
        }

        // registratietijd
        String registratieDatumtijd = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD));
        try {
            Date registratieDate = new SimpleDateFormat(context.getString(R.string.date_format_datumtijd), Locale.getDefault()).parse(registratieDatumtijd);
            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.date_format_tijd));
            String registratieTijd = sdf.format(registratieDate);
            viewHolder.dagvergunningRegistratieDatumtijdText.setText(registratieTijd);
        } catch (java.text.ParseException e) {
            viewHolder.dagvergunningRegistratieDatumtijdText.setText("");
        }

        // erkennings nummer
        viewHolder.erkenningsnummerText.setText(context.getString(R.string.label_erkenningsnummer) + ": " + erkenningsnummer);

        // sollicitatie nummer
        String sollicitatieNummer = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER));
        if (sollicitatieNummer != null && !sollicitatieNummer.equals("")) {
            viewHolder.sollicitatieSollicitatieNummerText.setVisibility(View.VISIBLE);
            viewHolder.sollicitatieSollicitatieNummerText.setText(context.getString(R.string.label_sollicitatienummer) + ": " + sollicitatieNummer);
        } else {
            // we need to clear the textview contents because listview items are recycled and may
            // therefor contain data from other dagvergunning
            viewHolder.sollicitatieSollicitatieNummerText.setVisibility(View.GONE);
            viewHolder.sollicitatieSollicitatieNummerText.setText("");
        }

        // sollicitatie status
        String sollicitatieStatus = cursor.getString(cursor.getColumnIndex(
                MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE));
        if (sollicitatieStatus != null && !sollicitatieStatus.equals("?") && !sollicitatieStatus.equals("")) {
            viewHolder.sollicitatieStatusText.setVisibility(View.VISIBLE);
            viewHolder.sollicitatieStatusText.setText(sollicitatieStatus);
            viewHolder.sollicitatieStatusText.setBackgroundColor(ContextCompat.getColor(context, Utility.getSollicitatieStatusColor(context, sollicitatieStatus)));
        } else {
            viewHolder.sollicitatieStatusText.setVisibility(View.GONE);
            viewHolder.sollicitatieStatusText.setText("");
        }

        // notitie
        String notitie = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Dagvergunning.COL_NOTITIE));
        if (notitie != null && !notitie.equals("")) {
            viewHolder.notitieText.setText(context.getString(R.string.label_notitie) + ": " + notitie);
            Utility.collapseView(viewHolder.notitieText, false);
        } else {
            Utility.collapseView(viewHolder.notitieText, true);
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
        @Bind(R.id.koopman_status) TextView koopmanStatusText;
        @Bind(R.id.koopman_naam_tijd) RelativeLayout koopmanNaamTijd;
        @Bind(R.id.koopman_voorletters_achternaam) TextView koopmanVoorlettersAchternaamText;
        @Bind(R.id.vervanger_aanwezig) TextView vervangerAanwezigText;
        @Bind(R.id.dagvergunning_registratie_datumtijd) TextView dagvergunningRegistratieDatumtijdText;
        @Bind(R.id.erkenningsnummer) TextView erkenningsnummerText;
        @Bind(R.id.sollicitatie_nummer_status) RelativeLayout sollicitatieNummerStatus;
        @Bind(R.id.sollicitatie_sollicitatie_nummer) TextView sollicitatieSollicitatieNummerText;
        @Bind(R.id.sollicitatie_status) TextView sollicitatieStatusText;
        @Bind(R.id.notitie) TextView notitieText;
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