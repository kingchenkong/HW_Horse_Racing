package com.homework.horse_racing.view_model.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homework.horse_racing.model.api.response_body.exchange_rate.USDTWD
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel : ViewModel() {
    companion object {
        private val TAG: String = MainActivityViewModel::class.java.simpleName
    }

    private val _exchangeRateText: MutableLiveData<String> = MutableLiveData<String>()
    var exchangeRateText: LiveData<String> = _exchangeRateText

    private val _timeText: MutableLiveData<String> = MutableLiveData<String>()
    var timeText: LiveData<String> = _timeText

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

    fun updateExchangeRate(rate: Double) {
        if (rate < 0) {
            _exchangeRateText.postValue("loading...")
        } else {
            _exchangeRateText.postValue(exchangeRateToText(rate))
        }
    }

    fun updateTime(timeString: String) {
        _timeText.postValue(parseUTCToTW(timeString))
    }

    private fun exchangeRateToText(exchangeRate: Double): String {
        return String.format("%.2f", exchangeRate)
    }

    private fun parseUTCToTW(utcTimeString: String): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val utcDF = SimpleDateFormat(pattern, Locale.UK)
        utcDF.timeZone = TimeZone.getTimeZone("UTC")
        val twDF = SimpleDateFormat(pattern, Locale.TAIWAN)
        twDF.timeZone = TimeZone.getDefault()
        val date: Date = utcDF.parse(utcTimeString) ?: Date()

        return twDF.format(date)
    }
}