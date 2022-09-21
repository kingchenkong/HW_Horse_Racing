package com.homework.horse_racing.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.homework.horse_racing.model.api.AppClientManager
import com.homework.horse_racing.model.api.response_body.exchange_rate.ExchangeRateRespBody
import com.homework.horse_racing.model.api.response_body.exchange_rate.USDTWD
import kotlinx.coroutines.launch


class ExchangeApiViewModel : ViewModel() {
    companion object {
        private val TAG: String = ExchangeApiViewModel::class.java.simpleName
    }

    private val _exchangeApiLiveData: MutableLiveData<USDTWD> = MutableLiveData<USDTWD>()
    val exchangeApiLiveData: LiveData<USDTWD> = _exchangeApiLiveData

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared:")
    }

    fun queryExchangeRate() {
        viewModelScope.launch {
            val response = AppClientManager.queryExchangeRate()
            val gson = Gson()
            when {
                response.isSuccess -> {
                    val json = response.getOrNull() ?: JsonObject()
                    val body = gson.fromJson(json, ExchangeRateRespBody::class.java)
                    val USDToTWD: USDTWD = body.USDTWD
                    Log.d(TAG, "queryExchangeRate: USD to TWD: $USDToTWD")
                    _exchangeApiLiveData.postValue(USDToTWD)
                }
                else -> {}
            }
        }
    }

}