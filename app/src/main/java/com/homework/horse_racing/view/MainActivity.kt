package com.homework.horse_racing.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.homework.horse_racing.R
import com.homework.horse_racing.model.api.AppClientManager
import com.homework.horse_racing.model.api.response_body.exchange_rate.ExchangeRateRespBody
import com.homework.horse_racing.model.api.response_body.exchange_rate.USDTWD

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: launch OK.")
    }

    override fun onStart() {
        super.onStart()

        queryExchangeRate()
    }

    private fun queryExchangeRate() {
        lifecycleScope.launchWhenStarted {
            val response = AppClientManager.queryExchangeRate()
            Log.d(TAG, "queryExchangeRate: response: $response")
            val gson = Gson()
            when {
                response.isSuccess -> {
                    val json = response.getOrNull() ?: JsonObject()
                    val body = gson.fromJson(json, ExchangeRateRespBody::class.java)
                    val USDToTWD: USDTWD = body.USDTWD
                    Log.d(TAG, "queryExchangeRate: USD to TWD: $USDToTWD")
                }
                else -> {}
            }
        }
    }

}