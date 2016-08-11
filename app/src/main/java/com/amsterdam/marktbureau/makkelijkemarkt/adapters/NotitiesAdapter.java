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
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.amsterdam.marktbureau.makkelijkemarkt.R;
import com.amsterdam.marktbureau.makkelijkemarkt.api.ApiPutNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class NotitiesAdapter extends CursorAdapter {

    // use classname when logging
    private static final String LOG_TAG = NotitiesAdapter.class.getSimpleName();

    /**
     * Constructor
     * @param context
     */
    public NotitiesAdapter(Context context) {
        super(context, null, 0);
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

        // create a new view from the notities_list_item definition
        View view = LayoutInflater.from(context).inflate(R.layout.notities_list_item, parent, false);

        // set a viewholder with our view layout for the view we just created
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
    public void bindView(View view, final Context context, Cursor cursor) {

        // get the viewholder layout containing the view items
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // bericht
        final String bericht = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Notitie.COL_BERICHT));
        viewHolder.berichtText.setText(bericht);

        // aangemaakt
        String aangemaakt = cursor.getString(cursor.getColumnIndex(MakkelijkeMarktProvider.Notitie.COL_AANGEMAAKT_DATUMTIJD));
        try {
            Date aangemaaktDate = new SimpleDateFormat(context.getString(R.string.date_format_datumtijd), Locale.getDefault()).parse(aangemaakt);
            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.date_format_tijd));
            String aangemaaktTijd = sdf.format(aangemaaktDate);
            viewHolder.aangemaaktText.setText(aangemaaktTijd);
        } catch (java.text.ParseException e) {
            viewHolder.aangemaaktText.setText("");
        }

        // afgevinkt
        int afgevinkt = cursor.getInt(cursor.getColumnIndex(MakkelijkeMarktProvider.Notitie.COL_AFGEVINKT));
        if (afgevinkt > 0) {
            viewHolder.afgevinktCheck.setChecked(true);
            viewHolder.berichtText.setTextColor(ContextCompat.getColor(context, R.color.primary));
        } else {
            viewHolder.afgevinktCheck.setChecked(false);
            viewHolder.berichtText.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
        }

        // onclick afgevinkt
        final int id = cursor.getInt(cursor.getColumnIndex(MakkelijkeMarktProvider.Notitie.COL_ID));
        viewHolder.afgevinktCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean afgevinkt = ((CheckBox) v).isChecked();

                // event to inform subscribers that we are starting to update the notitie
                EventBus.getDefault().post(new OnUpdateEvent(id, null));

                // create json object from checked state, id, and bericht
                JsonObject notitiePayload = new JsonObject();
                notitiePayload.addProperty(context.getString(R.string.makkelijkemarkt_api_notitie_payload_bericht), bericht);
                notitiePayload.addProperty(context.getString(R.string.makkelijkemarkt_api_notitie_payload_afgevinkt), afgevinkt);

                // send ApiPutNotitie and let the call update the database with the resulting object from the api call
                ApiPutNotitie apiPutNotitie = new ApiPutNotitie(context);
                apiPutNotitie.setId(id);
                apiPutNotitie.setPayload(notitiePayload);
                apiPutNotitie.enqueue();
            }
        });
    }

    /**
     * Viewholder helper class for easy referencing the view elements
     */
    public static class ViewHolder {

        // bind the elements
        @Bind(R.id.afgevinkt) CheckBox afgevinktCheck;
        @Bind(R.id.bericht) TextView berichtText;
        @Bind(R.id.aangemaakt) TextView aangemaaktText;

        /**
         * Bind the view elements using butterknife
         * @param view the listitem view
         */
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Event to inform subscribers that we are starting to update the notitie
     */
    public class OnUpdateEvent {
        public final int mNotitieId;
        public final String mMessage;

        public OnUpdateEvent(int id, String message) {
            mNotitieId = id;
            mMessage = message;
        }
    }
}