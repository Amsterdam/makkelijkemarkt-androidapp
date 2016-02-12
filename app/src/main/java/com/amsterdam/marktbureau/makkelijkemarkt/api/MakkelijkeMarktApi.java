/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiAccount;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiMarkt;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Makkelijke Markt Api interface defining the retrofit api calls
 * @author marcolangebeeke
 */
public interface MakkelijkeMarktApi {

    /**
     * Get a list of accounts from the Api
     * @return a list of ApiAccount objects
     */
    @GET("account/")
    Call<List<ApiAccount>> getAccounts();

    /**
     * Get a list of markten from the Api
     * @return a list of ApiMarkt objects
     */
    @GET("markt/")
    Call<List<ApiMarkt>> getMarkten();

    /**
     * Post an Account authentication request to the Api
     * @param auth a gson object containing the account credentials
     * @return a gson object containing the result with an Api key on success
     */
    @POST("login/basicId/")
    Call<JsonObject> postLoginBasicId(@Body JsonObject auth);

    /**
     * Send Get request to logout of the api server session
     * @return a gson object with object lifeTime set to 0
     */
    @GET("logout/")
    Call<JsonObject> getLogout();

    /**
     * Get a list of dagvergunningen for a given markt and date
     * @param marktId the id of the markt
     * @param dag the date
     * @return a list of ApiDagvergunning objects
     */
    @GET("dagvergunning/")
    Call<List<ApiDagvergunning>> getDagvergunningen(@Query("marktId") String marktId, @Query("dag") String dag);

    /**
     * Get a koopman object, including sollicitaties, from the api
     * @param erkenningsnummer the erkenningsnummer as a path segment parameter
     * @return an ApiKoopman object
     */
    @GET("koopman/erkenningsnummer/{erkenningsnummer}")
    Call<ApiKoopman> getKoopman(@Path("erkenningsnummer") String erkenningsnummer);
}