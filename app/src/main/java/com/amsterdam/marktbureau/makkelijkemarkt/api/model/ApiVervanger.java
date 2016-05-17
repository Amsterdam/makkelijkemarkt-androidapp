/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api.model;

import android.content.ContentValues;

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

/**
 * Api model for Vervanger
 * @author marcolangebeeke
 */
public class ApiVervanger {

    private int id;
    private ApiKoopman koopman;
    private ApiKoopman vervanger;
    private String pasUid;

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
    public ApiKoopman getVervanger() {
        return vervanger;
    }

    /**
     * @param vervanger
     */
    public void setVervanger(ApiKoopman vervanger) {
        this.vervanger = vervanger;
    }

    /**
     * @return
     */
    public String getPasUid() {
        return pasUid;
    }

    /**
     * @param nfcUid
     */
    public void setPasUid(String nfcUid) {
        this.pasUid = nfcUid;
    }

    /**
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues vervangerValues = new ContentValues();

        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_ID, getId());

        if (getKoopman() != null) {
            vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_KOOPMAN_ID, getKoopman().getId());
        }

        if (getVervanger() != null) {
            vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_VERVANGER_ID, getVervanger().getId());
        }

        // uppercase the nfc uid if we have one
        if (getPasUid() != null) {
            vervangerValues.put(MakkelijkeMarktProvider.Koopman.COL_PAS_UID, getPasUid().toUpperCase());
        }

        return vervangerValues;
    }
}