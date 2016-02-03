/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api.model;

import android.content.ContentValues;

import com.amsterdam.marktbureau.makkelijkemarkt.Utility;
import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcolangebeeke
 */
public class ApiSollicitatie {

    private int id;
    private int sollicitatieNummer;
    private String status;
    private List<String> vastePlaatsen = new ArrayList<String>();
    private int aantal3MeterKramen;
    private int aantal4MeterKramen;
    private int aantalExtraMeters;
    private int aantalElektra;
    private boolean krachtstroom;
    private boolean doorgehaald;
    private String doorgehaaldReden;
    private ApiKoopman koopman;
    private ApiMarkt markt;

    /**
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    public int getSollicitatieNummer() {
        return sollicitatieNummer;
    }

    /**
     * @param sollicitatieNummer
     */
    public void setSollicitatieNummer(int sollicitatieNummer) {
        this.sollicitatieNummer = sollicitatieNummer;
    }

    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return
     */
    public List<String> getVastePlaatsen() {
        return vastePlaatsen;
    }

    /**
     * @param vastePlaatsen
     */
    public void setVastePlaatsen(List<String> vastePlaatsen) {
        this.vastePlaatsen = vastePlaatsen;
    }

    /**
     * @return the vaste plaatsen list as a comma-separated String
     */
    public String getVastePlaatsenAsCsv() {
        return Utility.listToCsv(vastePlaatsen, ",");
    }

    /**
     * @return
     */
    public int getAantal3MeterKramen() {
        return aantal3MeterKramen;
    }

    /**
     * @param aantal3MeterKramen
     */
    public void setAantal3MeterKramen(int aantal3MeterKramen) {
        this.aantal3MeterKramen = aantal3MeterKramen;
    }

    /**
     * @return
     */
    public int getAantal4MeterKramen() {
        return aantal4MeterKramen;
    }

    /**
     * @param aantal4MeterKramen
     */
    public void setAantal4MeterKramen(int aantal4MeterKramen) {
        this.aantal4MeterKramen = aantal4MeterKramen;
    }

    /**
     * @return
     */
    public int getAantalExtraMeters() {
        return aantalExtraMeters;
    }

    /**
     * @param aantalExtraMeters
     */
    public void setAantalExtraMeters(int aantalExtraMeters) {
        this.aantalExtraMeters = aantalExtraMeters;
    }

    /**
     * @return
     */
    public int getAantalElektra() {
        return aantalElektra;
    }

    /**
     * @param aantalElektra
     */
    public void setAantalElektra(int aantalElektra) {
        this.aantalElektra = aantalElektra;
    }

    /**
     * @return
     */
    public boolean isKrachtstroom() {
        return krachtstroom;
    }

    /**
     * @param krachtstroom
     */
    public void setKrachtstroom(boolean krachtstroom) {
        this.krachtstroom = krachtstroom;
    }

    /**
     * @return
     */
    public boolean isDoorgehaald() {
        return doorgehaald;
    }

    /**
     * @param doorgehaald
     */
    public void setDoorgehaald(boolean doorgehaald) {
        this.doorgehaald = doorgehaald;
    }

    /**
     * @return
     */
    public String getDoorgehaaldReden() {
        return doorgehaaldReden;
    }

    /**
     * @param doorgehaaldReden
     */
    public void setDoorgehaaldReden(String doorgehaaldReden) {
        this.doorgehaaldReden = doorgehaaldReden;
    }

    /**
     * @return
     */
    public ApiKoopman getKoopman() {
        return koopman;
    }

    /**
     * @param koopman
     */
    public void setKoopman(ApiKoopman koopman) {
        this.koopman = koopman;
    }

    /**
     * @return
     */
    public ApiMarkt getMarkt() {
        return markt;
    }

    /**
     * @param markt
     */
    public void setMarkt(ApiMarkt markt) {
        this.markt = markt;
    }

    /**
     * Convert sollicitatie object to type contentvalues object
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues sollicitatieValues = new ContentValues();

        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_ID, getId());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_SOLLICITATIE_NUMMER, getSollicitatieNummer());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_STATUS, getStatus());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_DOORGEHAALD, isDoorgehaald());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_DOORGEHAALD_REDEN, getDoorgehaaldReden());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_VASTE_PLAATSEN, getVastePlaatsenAsCsv());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_3METER_KRAMEN, getAantal3MeterKramen());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_4METER_KRAMEN, getAantal4MeterKramen());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_EXTRA_METERS, getAantalExtraMeters());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_ELEKTRA, getAantalElektra());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_KRACHTSTROOM, isKrachtstroom());

        if (getMarkt() != null) {
            sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID, getMarkt().getId());
        }

        if (getKoopman() != null) {
            sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_KOOPMAN_ID, getKoopman().getId());
        }

        return sollicitatieValues;
    }
}