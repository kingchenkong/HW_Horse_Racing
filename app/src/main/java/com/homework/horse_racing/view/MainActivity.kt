package com.homework.horse_racing.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.homework.horse_racing.R
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.model.api.AppClientManager
import com.homework.horse_racing.model.api.response_body.exchange_rate.ExchangeRateRespBody
import com.homework.horse_racing.model.api.response_body.exchange_rate.USDTWD
import com.homework.horse_racing.view_model.ExchangeApiViewModel
import com.homework.horse_racing.view_model.ui.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var exchangeApiViewModel: ExchangeApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        exchangeApiViewModel = ViewModelProvider(this)[ExchangeApiViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.uiVm = mainActivityViewModel
        binding.apiVm = exchangeApiViewModel

    }

    override fun onStart() {
        super.onStart()

        queryExchangeRate() // TODO: use liveData
    }

    private fun queryExchangeRate() {
        lifecycleScope.launchWhenStarted {
            val response = AppClientManager.queryExchangeRate()
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