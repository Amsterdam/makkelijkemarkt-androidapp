package com.amsterdam.marktbureau.makkelijkemarkt.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MakkelijkeMarktApi {

    @GET("/api/account")
    Call<List<Account>> loadAccounts();
}