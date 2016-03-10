/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api.model;

import android.content.ContentValues;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marcolangebeeke
 */
public class ApiNotitie {

    private int id;
    private ApiMarkt markt;
    private String dag;
    private String bericht;
    private boolean afgevinktStatus;
    private boolean verwijderdStatus;
    private String aangemaaktDatumtijd;
    private String afgevinktDatumtijd;
    private String verwijderdDatumtijd;
    private List<Float> aangemaaktGeolocatie = new ArrayList<Float>();

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The markt
     */
    public ApiMarkt getMarkt() {
        return markt;
    }

    /**
     *
     * @param markt
     * The markt
     */
    public void setMarkt(ApiMarkt markt) {
        this.markt = markt;
    }

    /**
     *
     * @return
     * The dag
     */
    public String getDag() {
        return dag;
    }

    /**
     *
     * @param dag
     * The dag
     */
    public void setDag(String dag) {
        this.dag = dag;
    }

    /**
     *
     * @return
     * The bericht
     */
    public String getBericht() {
        return bericht;
    }

    /**
     *
     * @param bericht
     * The bericht
     */
    public void setBericht(String bericht) {
        this.bericht = bericht;
    }

    /**
     *
     * @return
     * The afgevinktStatus
     */
    public boolean isAfgevinkt() {
        return afgevinktStatus;
    }

    /**
     *
     * @param afgevinktStatus
     * The afgevinktStatus
     */
    public void setAfgevinkt(boolean afgevinktStatus) {
        this.afgevinktStatus = afgevinktStatus;
    }

    /**
     *
     * @return
     * The verwijderdStatus
     */
    public boolean isVerwijderd() {
        return verwijderdStatus;
    }

    /**
     *
     * @param verwijderdStatus
     * The verwijderdStatus
     */
    public void setVerwijderd(boolean verwijderdStatus) {
        this.verwijderdStatus = verwijderdStatus;
    }

    /**
     *
     * @return
     * The aangemaaktDatumtijd
     */
    public String getAangemaaktDatumtijd() {
        return aangemaaktDatumtijd;
    }

    /**
     *
     * @param aangemaaktDatumtijd
     * The aangemaaktDatumtijd
     */
    public void setAangemaaktDatumtijd(String aangemaaktDatumtijd) {
        this.aangemaaktDatumtijd = aangemaaktDatumtijd;
    }

    /**
     *
     * @return
     * The afgevinktDatumtijd
     */
    public String getAfgevinktDatumtijd() {
        return afgevinktDatumtijd;
    }

    /**
     *
     * @param afgevinktDatumtijd
     * The afgevinktDatumtijd
     */
    public void setAfgevinktDatumtijd(String afgevinktDatumtijd) {
        this.afgevinktDatumtijd = afgevinktDatumtijd;
    }

    /**
     *
     * @return
     * The verwijderdDatumtijd
     */
    public String getVerwijderdDatumtijd() {
        return verwijderdDatumtijd;
    }

    /**
     *
     * @param verwijderdDatumtijd
     * The verwijderdDatumtijd
     */
    public void setVerwijderdDatumtijd(String verwijderdDatumtijd) {
        this.verwijderdDatumtijd = verwijderdDatumtijd;
    }

    /**
     *
     * @return
     * The aangemaaktGeolocatie
     */
    public List<Float> getAangemaaktGeolocatie() {
        return aangemaaktGeolocatie;
    }

    /**
     *
     * @param aangemaaktGeolocatie
     * The aangemaaktGeolocatie
     */
    public void setAangemaaktGeolocatie(List<Float> aangemaaktGeolocatie) {
        this.aangemaaktGeolocatie = aangemaaktGeolocatie;
    }


    /**
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues notitieValues = new ContentValues();

        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_ID, getId());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_DAG, getDag());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_BERICHT, getBericht());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_AFGEVINKT, isAfgevinkt());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_VERWIJDERD, isVerwijderd());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_AANGEMAAKT_DATUMTIJD, getAangemaaktDatumtijd());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_AFGEVINKT_DATUMTIJD, getAfgevinktDatumtijd());
        notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_VERWIJDERD_DATUMTIJD, getVerwijderdDatumtijd());

        if (getMarkt() != null) {
            notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_MARKT_ID, getMarkt().getId());
        }

        if (getAangemaaktGeolocatie() != null && getAangemaaktGeolocatie().size() > 1) {
            notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_AANGEMAAKT_GEOLOCATIE_LAT, getAangemaaktGeolocatie().get(0));
            notitieValues.put(MakkelijkeMarktProvider.Notitie.COL_AANGEMAAKT_GEOLOCATIE_LONG, getAangemaaktGeolocatie().get(1));
        }

        return notitieValues;
    }

}