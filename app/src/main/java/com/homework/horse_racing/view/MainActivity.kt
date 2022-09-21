package com.homework.horse_racing.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.view_model.ExchangeApiViewModel
import com.homework.horse_racing.view_model.ui.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var uiVm: MainActivityViewModel
    private lateinit var apiVm: ExchangeApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")

        uiVm = ViewModelProvider(this)[MainActivityViewModel::class.java]
        apiVm = ViewModelProvider(this)[ExchangeApiViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.uiVm = uiVm
        binding.apiVm = apiVm

        initUiObserver()
        initApiObserver()

        uiAction()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")

        initView()

        uiVm.updateExchangeRate(-1.0)
        apiVm.queryExchangeRate()
    }

    private fun initView() {
        uiVm.updateExchangeRate(0.0)

    }

    private fun initUiObserver() {

    }

    private fun initApiObserver() {
        apiVm.exchangeApiLiveData.observe(this) {
            Log.d(TAG, "initObserver: exchangeRateLiveData: $it")
            uiVm.updateExchangeRate(it.Exrate)
            uiVm.updateTime(it.UTC)
        }

    }

    private fun uiAction() {
        binding.apply {
            btnApi.setOnClickListener {
                Log.d(TAG, "uiAction: btnApi: onClick: ")
                apiVm!!.queryExchangeRate()
            }
        }
    }


}