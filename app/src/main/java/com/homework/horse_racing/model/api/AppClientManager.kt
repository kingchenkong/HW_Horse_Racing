package com.homework.horse_racing.model.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppClientManager {
    val TAG: String = AppClientManager::class.java.simpleName

    const val DoMain: String = "https://tw.rter.info"

    private val apiService: ApiService

    init {
//        val loggingInterceptor: HttpLoggingInterceptor =
//            HttpLoggingInterceptor().apply {
//                setLevel(HttpLoggingInterceptor.Level.BODY)
//            }
//        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(DoMain)
            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }


    suspend fun queryExchangeRate(): Result<JsonObject> {
        return kotlin.runCatching {
            val response = apiService.queryExchangeRate()

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