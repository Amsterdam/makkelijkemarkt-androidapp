/**
 * Copyright (C) 2016 X Gemeente
 *                    X Amsterdam
 *                    X Onderzoek, Informatie en Statistiek
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
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
    private List<String> vastePlaatsen = new ArrayList<>();
    private boolean doorgehaald;
    private String doorgehaaldReden;
    private int koopmanId;

    private int aantal3MeterKramen;
    private int aantal4MeterKramen;
    private int aantalExtraMeters;
    private int aantalElektra;
    private int afvaleiland;
    private boolean krachtstroom;
    private boolean reiniging;
    private boolean eenmaligElektra;

    private ApiMarkt markt;
    private ApiKoopman koopman;

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
    public int getAfvaleiland() {
        return afvaleiland;
    }

    /**
     * @param afvaleiland
     */
    public void setAfvaleiland(int afvaleiland) {
        this.afvaleiland = afvaleiland;
    }

    /**
     * @return
     */
    public boolean getKrachtstroom() {
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
    public boolean getEenmaligElektra() {
        return eenmaligElektra;
    }

    /**
     * @param eenmaligElektra
     */
    public void setEenmaligElektra(boolean eenmaligElektra) {
        this.eenmaligElektra = eenmaligElektra;
    }

    /**
     * @return
     */
    public boolean getReiniging() {
        return reiniging;
    }

    /**
     * @param reiniging
     */
    public void setReiniging(boolean reiniging) {
        this.reiniging = reiniging;
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
    public int getKoopmanId() {
        return koopmanId;
    }

    /**
     * @param koopmanId
     */
    public void setKoopmanId(int koopmanId) {
        this.koopmanId = koopmanId;
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
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_KOOPMAN_ID, getKoopmanId());

        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_3METER_KRAMEN, getAantal3MeterKramen());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_4METER_KRAMEN, getAantal4MeterKramen());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_EXTRA_METERS, getAantalExtraMeters());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AANTAL_ELEKTRA, getAantalElektra());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_AFVALEILAND, getAfvaleiland());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_KRACHTSTROOM, getKrachtstroom());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_REINIGING, getReiniging());
        sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_EENMALIG_ELEKTRA, getEenmaligElektra());

        if (getMarkt() != null) {
            sollicitatieValues.put(MakkelijkeMarktProvider.Sollicitatie.COL_MARKT_ID, getMarkt().getId());
        }

        // koopman object is not added to the contentvalues object

        return sollicitatieValues;
    }
}