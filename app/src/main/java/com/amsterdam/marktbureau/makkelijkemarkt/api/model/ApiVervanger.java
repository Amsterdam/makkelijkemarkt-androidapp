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

    private String pas_uid;
    private int koopman_id;
    private int vervanger_id;

    /**
     * @return
     */
    public String getPasUid() {
        return pas_uid;
    }

    /**
     * @param pasUid
     */
    public void setPasUid(String pasUid) {
        this.pas_uid = pasUid;
    }

    /**
     * @return
     */
    public int getKoopmanId() {
        return koopman_id;
    }

    /**
     * @param koopmanId
     */
    public void setKoopmanId(int koopmanId) {
        this.koopman_id = koopmanId;
    }

    /**
     * @return
     */
    public int getVervangerId() {
        return vervanger_id;
    }

    /**
     * @param vervangerId
     */
    public void setVervangerId(int vervangerId) {
        this.vervanger_id = vervangerId;
    }

    /**
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues vervangerValues = new ContentValues();

        // uppercase the nfc uid if we have one
        if (getPasUid() != null) {
            vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_PAS_UID, getPasUid().toUpperCase());
        }

        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_KOOPMAN_ID, getKoopmanId());
        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_VERVANGER_ID, getVervangerId());

        return vervangerValues;
    }
}