/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiAccount;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiDagvergunning;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiKoopman;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiMarkt;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiNotitie;
import com.amsterdam.marktbureau.makkelijkemarkt.api.model.ApiSollicitatie;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
     * Post a new dagvergunning
     * @param dagvergunning a gson object containing the dagvergunning values
     * @return a apidagvergunning object containing the result
     */
    @POST("dagvergunning/")
    Call<ApiDagvergunning> postDagvergunning(@Body JsonObject dagvergunning);

    /**
     * Put an existing dagvergunning
     * @param id the id of the existing dagvergunning
     * @param dagvergunning a gson object containing the dagvergunning values
     * @return a apidagvergunning object containing the result
     */
    @PUT("dagvergunning/{id}")
    Call<ApiDagvergunning> putDagvergunning(@Path("id") String id, @Body JsonObject dagvergunning);

    /**
     * Delete a dagvergunning
     * @param id the id of the dagvergunning
     */
    @DELETE("dagvergunning/{id}")
    Call<String> deleteDagvergunning(@Path("id") String id);

    /**
     * Post a new dagvergunning concept to get a factuur with pricing
     * @param dagvergunning a gson object containing the dagvergunning values
     * @return a json object containing the factuur details
     */
    @POST("dagvergunning_concept/")
    Call<JsonObject> postDagvergunningConcept(@Body JsonObject dagvergunning);

    /**
     * Get a koopman object, including sollicitaties, from the api
     * @param erkenningsnummer the erkenningsnummer as a path segment parameter
     * @return an ApiKoopman object
     */
    @GET("koopman/erkenningsnummer/{erkenningsnummer}")
    Call<ApiKoopman> getKoopman(@Path("erkenningsnummer") String erkenningsnummer);

    /**
     * Get a list of sollicitaties for a given marktid from the Api
     * @param marktId the id of the marktet we want the sollicitaties for
     * @param listOffset the offset of from which position we want to query the total list
     * @param listLength the length of the response list
     * @return a list of ApiSollicitatie objects
     */
    @GET("sollicitaties/markt/{marktId}")
    Call<List<ApiSollicitatie>> getSollicitaties(@Path("marktId") String marktId, @Query("listOffset") String listOffset, @Query("listLength") String listLength);

    /**
     * Get a list of notities for a given markt and date
     * @param marktId the id of the markt
     * @param dag the date
     * @param verwijderdStatus the status of the notitie
     * @return a list of ApiNotitie objects
     */
    @GET("notitie/{marktId}/{dag}")
    Call<List<ApiNotitie>> getNotities(@Path("marktId") String marktId, @Path("dag") String dag, @Query("verwijderdStatus") String verwijderdStatus);

    /**
     * Put an existing notitie
     * @param id the id of the existing notitie
     * @param notitie a gson object containing the notitie values
     * @return a apinotitie object containing the result
     */
    @PUT("notitie/{id}")
    Call<ApiNotitie> putNotitie(@Path("id") String id, @Body JsonObject notitie);

    /**
     * Post a new notitie
     * @param notitie a gson object containing the notitie values
     * @return a apinotitie object containing the result
     */
    @POST("notitie/")
    Call<ApiNotitie> postNotitie(@Body JsonObject notitie);
}