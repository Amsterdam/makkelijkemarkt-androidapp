/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import com.amsterdam.marktbureau.makkelijkemarkt.model.ApiAccount;
import com.amsterdam.marktbureau.makkelijkemarkt.model.ApiMarkt;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 *
 * @author marcolangebeeke
 */
public interface MakkelijkeMarktApi {

    @GET("/api/account")
    Call<List<ApiAccount>> loadAccounts();

    @GET("/api/markt")
    Call<List<ApiMarkt>> loadMarkten();





}