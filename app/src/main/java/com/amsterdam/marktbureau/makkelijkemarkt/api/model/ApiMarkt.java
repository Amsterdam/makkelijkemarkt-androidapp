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
 * Markt object for communicating with the Api using retrofit
 * @author marcolangebeeke
 */
public class ApiMarkt {

    private int id;
    private String afkorting;
    private String naam;
    private String geoArea;
    private String soort;
    private List<String> marktDagen = new ArrayList<>();
    private int standaardKraamAfmeting;
    private boolean extraMetersMogelijk;
    private List<String> aanwezigeOpties = new ArrayList<>();
    private int perfectViewNummer;

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
    public String getAfkorting() {
        return afkorting;
    }

    /**
     * @param afkorting
     */
    public void setAfkorting(String afkorting) {
        this.afkorting = afkorting;
    }

    /**
     * @return
     */
    public String getNaam() {
        return naam;
    }

    /**
     * @param naam
     */
    public void setNaam(String naam) {
        this.naam = naam;
    }

    /**
     * @return
     */
    public String getGeoArea() {
        return geoArea;
    }

    /**
     * @param geoArea
     */
    public void setGeoArea(String geoArea) {
        this.geoArea = geoArea;
    }

    /**
     * @return
     */
    public String getSoort() {
        return soort;
    }

    /**
     * @param soort
     */
    public void setSoort(String soort) {
        this.soort = soort;
    }

    /**
     * @return
     */
    public List<String> getMarktDagen() {
        return marktDagen;
    }

    /**
     * @param marktDagen
     */
    public void setMarktDagen(List<String> marktDagen) {
        this.marktDagen = marktDagen;
    }

    /**
     * @return
     */
    public int getStandaardKraamAfmeting() {
        return standaardKraamAfmeting;
    }

    /**
     * @param standaardKraamAfmeting
     */
    public void setStandaardKraamAfmeting(int standaardKraamAfmeting) {
        this.standaardKraamAfmeting = standaardKraamAfmeting;
    }

    /**
     * @return
     */
    public boolean isExtraMetersMogelijk() {
        return extraMetersMogelijk;
    }

    /**
     * @param extraMetersMogelijk
     */
    public void setExtraMetersMogelijk(boolean extraMetersMogelijk) {
        this.extraMetersMogelijk = extraMetersMogelijk;
    }

    /**
     * @return
     */
    public List<String> getAanwezigeOpties() {
        return aanwezigeOpties;
    }

    /**
     * @param aanwezigeOpties
     */
    public void setAanwezigeOpties(List<String> aanwezigeOpties) {
        this.aanwezigeOpties = aanwezigeOpties;
    }

    /**
     * @return
     */
    public int getPerfectViewNummer() {
        return perfectViewNummer;
    }

    /**
     * @param perfectViewNummer
     */
    public void setPerfectViewNummer(int perfectViewNummer) {
        this.perfectViewNummer = perfectViewNummer;
    }

    /**
     * @return the marktdagen list as a comma-separated String
     */
    public String getMarktDagenAsCsv() {
        return Utility.listToCsv(marktDagen, ",");
    }

    /**
     * @return the aanwezigeopties list as a comma-separated String
     */
    public String getAanwezigeOptiesAsCsv() {
        return Utility.listToCsv(aanwezigeOpties, ",");
    }

    /**
     * Convert markt object to type contentvalues object
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues marktValues = new ContentValues();

        marktValues.put(MakkelijkeMarktProvider.Markt.COL_ID, getId());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_NAAM, getNaam());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_GEO_AREA, getGeoArea());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_AFKORTING, getAfkorting());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_SOORT, getSoort());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_MARKT_DAGEN, getMarktDagenAsCsv());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_STANDAARD_KRAAM_AFMETING, getStandaardKraamAfmeting());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_EXTRA_METERS_MOGELIJK, isExtraMetersMogelijk());
        marktValues.put(MakkelijkeMarktProvider.Markt.COL_AANWEZIGE_OPTIES, getAanwezigeOptiesAsCsv());

        return marktValues;
    }
}