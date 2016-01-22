package com.amsterdam.marktbureau.makkelijkemarkt.api;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private int id;
    private String email;
    private String naam;
    private String username;
    private List<String> roles = new ArrayList<String>();

    /**
     * No args constructor for use in serialization
     */
    public Account() {
    }

    /**
     * Constructor
     * @param id
     * @param username
     * @param email
     * @param roles
     * @param naam
     */
    public Account(int id, String email, String naam, String username, List<String> roles) {
        this.id = id;
        this.email = email;
        this.naam = naam;
        this.username = username;
        this.roles = roles;
    }

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
     *
     * @return
     * The naam
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
     * @return the roles as a comma-separated String
     */
    public String getRolesAsString() {
        List<String> rolesCopy = new ArrayList<String>(roles);
        StringBuilder builder = new StringBuilder();
        builder.append(rolesCopy.remove(0));
        for (String role : rolesCopy) {
            builder.append(",");
            builder.append(role);
        }
        return builder.toString();
    }

    /**
     * @param roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}