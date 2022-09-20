package com.homework.horse_racing.view_model.ui

import android.util.Log
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    companion object {
        private val TAG: String = MainActivityViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

}