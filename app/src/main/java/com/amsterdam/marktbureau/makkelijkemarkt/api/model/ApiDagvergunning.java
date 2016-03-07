/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api.model;

import android.content.ContentValues;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcolangebeeke
 */
public class ApiDagvergunning {

    private int id;
    private String dag;
    private int aantal3MeterKramen;
    private int aantal4MeterKramen;
    private int extraMeters;
    private int totaleLengte;
    private int aantalElektra;
    private int afvaleiland;
    private String erkenningsnummer;
    private String erkenningsnummerInvoerMethode;
    private String aanwezig;
    private String notitie;
    private int aantal3meterKramenVast;
    private int aantal4meterKramenVast;
    private int aantalExtraMetersVast;
    private int totaleLengteVast;
    private int aantalElektraVast;
    private int afvaleilandVast;
    private String status;
    private String registratieDatumtijd;
    private List<Float> registratieGeolocatie = new ArrayList<Float>();
    private String aanmaakDatumtijd;
    private boolean doorgehaald;
    private ApiAccount registratieAccount;
    private ApiMarkt markt;
    private ApiKoopman koopman;
    private ApiSollicitatie sollicitatie;

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
    public String getDag() {
        return dag;
    }

    /**
     * @param dag
     */
    public void setDag(String dag) {
        this.dag = dag;
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
    public int getExtraMeters() {
        return extraMeters;
    }

    /**
     * @param extraMeters
     */
    public void setExtraMeters(int extraMeters) {
        this.extraMeters = extraMeters;
    }

    /**
     * @return
     */
    public int getTotaleLengte() {
        return totaleLengte;
    }

    /**
     * @param totaleLengte
     */
    public void setTotaleLengte(int totaleLengte) {
        this.totaleLengte = totaleLengte;
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
    public String getErkenningsnummer() {
        return erkenningsnummer;
    }

    /**
     * @param erkenningsnummer
     */
    public void setErkenningsnummer(String erkenningsnummer) {
        this.erkenningsnummer = erkenningsnummer;
    }

    /**
     * @return
     */
    public String getErkenningsnummerInvoerMethode() {
        return erkenningsnummerInvoerMethode;
    }

    /**
     * @param erkenningsnummerInvoerMethode
     */
    public void setErkenningsnummerInvoerMethode(String erkenningsnummerInvoerMethode) {
        this.erkenningsnummerInvoerMethode = erkenningsnummerInvoerMethode;
    }

    /**
     * @return
     */
    public String getAanwezig() {
        return aanwezig;
    }

    /**
     * @param aanwezig
     */
    public void setAanwezig(String aanwezig) {
        this.aanwezig = aanwezig;
    }

    /**
     * @return
     */
    public String getNotitie() {
        return notitie;
    }

    /**
     * @param notitie
     */
    public void setNotitie(String notitie) {
        this.notitie = notitie;
    }

    /**
     * @return
     */
    public int getAantal3meterKramenVast() {
        return aantal3meterKramenVast;
    }

    /**
     * @param aantal3meterKramenVast
     */
    public void setAantal3meterKramenVast(int aantal3meterKramenVast) {
        this.aantal3meterKramenVast = aantal3meterKramenVast;
    }

    /**
     * @return
     */
    public int getAantal4meterKramenVast() {
        return aantal4meterKramenVast;
    }

    /**
     * @param aantal4meterKramenVast
     */
    public void setAantal4meterKramenVast(int aantal4meterKramenVast) {
        this.aantal4meterKramenVast = aantal4meterKramenVast;
    }

    /**
     * @return
     */
    public int getAantalExtraMetersVast() {
        return aantalExtraMetersVast;
    }

    /**
     * @param aantalExtraMetersVast
     */
    public void setAantalExtraMetersVast(int aantalExtraMetersVast) {
        this.aantalExtraMetersVast = aantalExtraMetersVast;
    }

    /**
     * @return
     */
    public int getTotaleLengteVast() {
        return totaleLengteVast;
    }

    /**
     * @param totaleLengteVast
     */
    public void setTotaleLengteVast(int totaleLengteVast) {
        this.totaleLengteVast = totaleLengteVast;
    }

    /**
     * @return
     */
    public int getAantalElektraVast() {
        return aantalElektraVast;
    }

    /**
     * @param aantalElektraVast
     */
    public void setAantalElektraVast(int aantalElektraVast) {
        this.aantalElektraVast = aantalElektraVast;
    }

    /**
     * @return
     */
    public int getAfvaleilandVast() {
        return afvaleilandVast;
    }

    /**
     * @param afvaleilandVast
     */
    public void setAfvaleilandVast(int afvaleilandVast) {
        this.afvaleilandVast = afvaleilandVast;
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
    public String getRegistratieDatumtijd() {
        return registratieDatumtijd;
    }

    /**
     * @param registratieDatumtijd
     */
    public void setRegistratieDatumtijd(String registratieDatumtijd) {
        this.registratieDatumtijd = registratieDatumtijd;
    }

    /**
     * @return
     */
    public List<Float> getRegistratieGeolocatie() {
        return registratieGeolocatie;
    }

    /**
     * @param registratieGeolocatie
     */
    public void setRegistratieGeolocatie(List<Float> registratieGeolocatie) {
        this.registratieGeolocatie = registratieGeolocatie;
    }

    /**
     * @return
     */
    public String getAanmaakDatumtijd() {
        return aanmaakDatumtijd;
    }

    /**
     * @param aanmaakDatumtijd
     */
    public void setAanmaakDatumtijd(String aanmaakDatumtijd) {
        this.aanmaakDatumtijd = aanmaakDatumtijd;
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
    public ApiAccount getRegistratieAccount() {
        return registratieAccount;
    }

    /**
     * @param registratieAccount
     */
    public void setRegistratieAccount(ApiAccount registratieAccount) {
        this.registratieAccount = registratieAccount;
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
    public ApiSollicitatie getSollicitatie() {
        return sollicitatie;
    }

    /**
     * @param sollicitatie
     */
    public void setSollicitatie(ApiSollicitatie sollicitatie) {
        this.sollicitatie = sollicitatie;
    }

    /**
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues dagvergunningValues = new ContentValues();

        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_ID, getId());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_DAG, getDag());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN, getAantal3MeterKramen());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN, getAantal4MeterKramen());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_EXTRA_METERS, getExtraMeters());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE, getTotaleLengte());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA, getAantalElektra());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND, getAfvaleiland());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_WAARDE, getErkenningsnummer());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_ERKENNINGSNUMMER_INVOER_METHODE, getErkenningsnummerInvoerMethode());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANWEZIG, getAanwezig());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_NOTITIE, getNotitie());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL3METER_KRAMEN_VAST, getAantal3meterKramenVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL4METER_KRAMEN_VAST, getAantal4meterKramenVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_EXTRA_METERS_VAST, getAantalExtraMetersVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_TOTALE_LENGTE_VAST, getTotaleLengteVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANTAL_ELEKTRA_VAST, getAantalElektraVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AFVALEILAND_VAST, getAfvaleilandVast());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_STATUS_SOLLICITATIE, getStatus());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_DATUMTIJD, getRegistratieDatumtijd());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_AANMAAK_DATUMTIJD, getAanmaakDatumtijd());
        dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_DOORGEHAALD, isDoorgehaald());

        if (getRegistratieGeolocatie() != null && getRegistratieGeolocatie().size() > 1) {
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LAT, getRegistratieGeolocatie().get(0));
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_GEOLOCATIE_LONG, getRegistratieGeolocatie().get(1));
        }

        if (getRegistratieAccount() != null) {
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_REGISTRATIE_ACCOUNT_ID, getRegistratieAccount().getId());
        }

        if (getMarkt() != null) {
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_MARKT_ID,getMarkt().getId());
        }

        if (getKoopman() != null) {
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_KOOPMAN_ID, getKoopman().getId());
        }

        if (getSollicitatie() != null) {
            dagvergunningValues.put(MakkelijkeMarktProvider.Dagvergunning.COL_SOLLICITATIE_ID, getSollicitatie().getId());
        }

        return dagvergunningValues;
    }
}