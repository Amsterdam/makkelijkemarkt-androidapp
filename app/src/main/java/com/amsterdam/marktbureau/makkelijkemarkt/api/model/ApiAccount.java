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
 * Account object for communicating with the Api using retrofit
 * @author marcolangebeeke
 */
public class ApiAccount {

    private int id;
    private String email;
    private String naam;
    private String username;
    private List<String> roles = new ArrayList<>();

    /**
     * @return the id
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * @return the roles list as a comma-separated String
     */
    public String getRolesAsCsv() {
        return Utility.listToCsv(roles, ",");
    }

    /**
     * Convert object to type contentvalues
     * @return contentvalues object containing the objects name value pairs
     */
    public ContentValues toContentValues() {
        ContentValues accountValues = new ContentValues();

        accountValues.put(MakkelijkeMarktProvider.Account.COL_ID, getId());
        accountValues.put(MakkelijkeMarktProvider.Account.COL_NAAM, getNaam());
        accountValues.put(MakkelijkeMarktProvider.Account.COL_EMAIL, getEmail());
        accountValues.put(MakkelijkeMarktProvider.Account.COL_USERNAME, getUsername());
        accountValues.put(MakkelijkeMarktProvider.Account.COL_ROLE, getRolesAsCsv());

        return accountValues;
    }
}