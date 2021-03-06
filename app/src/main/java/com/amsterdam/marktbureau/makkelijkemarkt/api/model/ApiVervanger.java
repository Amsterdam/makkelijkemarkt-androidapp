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

import com.amsterdam.marktbureau.makkelijkemarkt.data.MakkelijkeMarktProvider;

/**
 * Api model for Vervanger
 * @author marcolangebeeke
 */
public class ApiVervanger {

    private String id;
    private int koopman_id;
    private int vervanger_id;
    private String pas_uid;

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
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
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues vervangerValues = new ContentValues();

        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_ID, getId());
        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_KOOPMAN_ID, getKoopmanId());
        vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_VERVANGER_ID, getVervangerId());

        // uppercase the nfc uid if we have one
        if (getPasUid() != null) {
            vervangerValues.put(MakkelijkeMarktProvider.Vervanger.COL_PAS_UID, getPasUid().toUpperCase());
        }

        return vervangerValues;
    }
}