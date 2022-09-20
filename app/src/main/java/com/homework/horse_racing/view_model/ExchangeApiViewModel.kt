package com.homework.horse_racing.view_model

import android.util.Log
import androidx.lifecycle.ViewModel

class ExchangeApiViewModel : ViewModel() {
    companion object {
        private val TAG: String = ExchangeApiViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared:")
    }

}