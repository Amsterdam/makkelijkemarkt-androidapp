/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiAccount;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiMarkt;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Makkelijke Markt Api interface defining the retrofit api calls
 * @author marcolangebeeke
 */
public interface MakkelijkeMarktApi {

    /**
     * Get a list of accounts from the Api
     * @return a list of ApiAccount objects
     */
    @GET("account")
    Call<List<ApiAccount>> getAccounts();

    /**
     * Get a list of markten from the Api
     * @return a list of ApiMarkt objects
     */
    @GET("markt")
    Call<List<ApiMarkt>> getMarkten();

    /**
     * Post an Account authentication request to the Api
     * @param auth a gson object containing the account credentials
     * @return a gson object containing the result with an Api key on success
     */
    @POST("login/basicId/")
    Call<JsonObject> postLoginBasicId(@Body JsonObject auth);






}