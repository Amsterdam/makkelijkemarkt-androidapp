/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt.api;

import com.amsterdam.marktbureau.makkelijkemarkt.model.Account;
import com.amsterdam.marktbureau.makkelijkemarkt.model.Markt;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 *
 * @author marcolangebeeke
 */
public interface MakkelijkeMarktApi {

    @GET("/api/account")
    Call<List<Account>> loadAccounts();

    @GET("/api/markt")
    Call<List<Markt>> loadMarkets();
}