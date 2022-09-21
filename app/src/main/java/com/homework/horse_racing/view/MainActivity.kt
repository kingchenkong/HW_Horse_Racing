package com.homework.horse_racing.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.manager.BetHorseManager
import com.homework.horse_racing.view_model.ExchangeApiViewModel
import com.homework.horse_racing.view_model.ui.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var uiVm: MainActivityViewModel
    private lateinit var apiVm: ExchangeApiViewModel

    private var manager: BetHorseManager = BetHorseManager

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
        initManagerObserver()
        initApiObserver()

        uiAction()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")

        manager.initialize()
        uiVm.initView()

        getExchangeRateByApi()
    }

    private fun initUiObserver() {
        uiVm.betAmountText.observe(this) {
            Log.d(TAG, "initUiObserver: betAmountText: $it")
            val betAmountUSD: Double = when {
                it.isBlank() -> 0.0
                else -> it.toDouble()
            }
            manager.usdBetAmountLiveData.postValue(betAmountUSD)
        }

    }

    private fun initManagerObserver() {
        manager.horseOdds1LiveData.observe(this) {
            uiVm.oddsHorse1Text.postValue(it.toString())
        }
        manager.horseOdds2LiveData.observe(this) {
            uiVm.oddsHorse2Text.postValue(it.toString())
        }
        manager.horseOdds3LiveData.observe(this) {
            uiVm.oddsHorse3Text.postValue(it.toString())
        }
        manager.horseOdds4LiveData.observe(this) {
            uiVm.oddsHorse4Text.postValue(it.toString())
        }
        manager.focusHorseNumberLiveData.observe(this) {
            Log.d(TAG, "initUiObserver: manager.focusHorseNumber: ${it.id}")
            uiVm.updateFocusNumberText(it.horseName)

            manager.calculateAward()
        }
        manager.usdBetAmountLiveData.observe(this) {
            manager.calculateTWDBetAmount()
            manager.calculateAward()
        }

        manager.twdBetAmountLiveData.observe(this) {
            Log.d(TAG, "initManagerObserver: twdBetAmountLiveData: $it ")
            uiVm.updateBetAmountToTWD(it)
        }

        manager.usdAwardLiveData.observe(this) {
            uiVm.updateAwardUSDText(it)
        }
        manager.twdAwardLiveData.observe(this) {
            uiVm.updateAwardTWDText(it)
        }

    }

    private fun initApiObserver() {
        apiVm.exchangeApiLiveData.observe(this) {
            Log.d(TAG, "initObserver: exchangeRateLiveData: $it")
            uiVm.updateExchangeRate(it.Exrate)
            uiVm.updateTime(it.UTC)
            manager.nowExchangeRateLiveData.postValue(it.Exrate)
        }
    }

    private fun uiAction() {
        binding.apply {
            btnApi.setOnClickListener {
                Log.d(TAG, "uiAction: btnApi: onClick: ")
                apiVm!!.queryExchangeRate()
            }
            btnHorse1.setOnClickListener {
                manager.focusHorseNumberLiveData.postValue(HorseNumber.NUM_1)
            }
            btnHorse2.setOnClickListener {
                manager.focusHorseNumberLiveData.postValue(HorseNumber.NUM_2)
            }
            btnHorse3.setOnClickListener {
                manager.focusHorseNumberLiveData.postValue(HorseNumber.NUM_3)
            }
            btnHorse4.setOnClickListener {
                manager.focusHorseNumberLiveData.postValue(HorseNumber.NUM_4)
            }
            btnBet.setOnClickListener {
                Log.d(TAG, "uiAction: btnBet:")
                //TODO: 下注 馬開跑

            }
        }
    }

    private fun getExchangeRateByApi() {
        uiVm.updateExchangeRate(-1.0)
        apiVm.queryExchangeRate()
    }


}