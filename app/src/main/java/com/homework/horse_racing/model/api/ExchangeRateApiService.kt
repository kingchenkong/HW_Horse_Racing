package com.homework.horse_racing.model.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeRateApiService {

    /*
     * query exchange rate
     */
    @GET("/capi.php")
   suspend fun queryExchangeRate(): Response<JsonObject>

}