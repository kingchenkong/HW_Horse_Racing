package com.homework.horse_racing.model.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppClientManager {
    val TAG: String = AppClientManager::class.java.simpleName

    const val DoMain: String = "https://tw.rter.info"

    private val exchangeRateApi: ExchangeRateApiService

    init {
        // HttpLoggingInterceptor is use to record networking in logcat
        val loggingInterceptor: HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        // cause retrofit hasn't logcat networking function, so use okhttp implement it.
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(DoMain)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // add okHttpClient to implement networking record in logcat.
            .build()
        exchangeRateApi = retrofit.create(ExchangeRateApiService::class.java)
    }

    suspend fun queryExchangeRate(): Result<JsonObject> {
        return kotlin.runCatching {
            val response = exchangeRateApi.queryExchangeRate()

            if (response.isSuccessful) {
                return@runCatching response.body()!!
            } else {
                val gson = Gson()
                val errorString = response.errorBody()!!.string()
                return@runCatching withContext(Dispatchers.Default) {
                    gson.fromJson<JsonObject>(
                        errorString, object : TypeToken<JsonObject>() {}.type
                    )
                }
            }
        }
    }

}