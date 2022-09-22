package com.homework.horse_racing.view

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.bean.RaceProgress
import com.homework.horse_racing.model.manager.BetHorseManager
import com.homework.horse_racing.view_model.ExchangeApiViewModel
import com.homework.horse_racing.view_model.ui.MainActivityViewModel
import java.util.*

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
        uiVm.initViewData()

        initRace()

        getExchangeRateByApi()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initUiObserver() {
        uiVm.betAmountText.observe(this) {
            Log.d(TAG, "betAmountText: $it")
            val betAmountUSD: Double = when {
                it.isBlank() -> 0.0
                else -> it.toDouble()
            }
            manager.usdBetAmountLiveData.postValue(betAmountUSD)
        }

//        uiVm.pastSecLiveData.observe(this) {
//            Log.d(TAG, "[Race] past sec: $it")
//        }

        uiVm.raceProgressLiveData.observe(this) {
//            Log.d(TAG, "[Race] race progress: $it")
            binding.pbHorse1.progress = it.race1
            binding.pbHorse2.progress = it.race2
            binding.pbHorse3.progress = it.race3
            binding.pbHorse4.progress = it.race4

            RaceProgress.checkWinner(it)
            if (it.goalHorseNumberList.size > 0) {
                uiVm.processAfterGoal(it)
            }
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
            Log.d(TAG, "manager.focusHorseNumber: ${it.id}")
            uiVm.updateFocusNumberText(it.horseName)

            manager.calculateAward()
        }
        manager.usdBetAmountLiveData.observe(this) {
            manager.calculateTWDBetAmount()
            manager.calculateAward()
        }

        manager.twdBetAmountLiveData.observe(this) {
            Log.d(TAG, "twdBetAmountLiveData: $it ")
            uiVm.updateBetAmountToTWD(it)
        }

        manager.usdAwardLiveData.observe(this) {
            uiVm.updateAwardUSDText(it)
        }
        manager.twdAwardLiveData.observe(this) {
            uiVm.updateAwardTWDText(it)
        }
        manager.processEndTimeStampLiveData.observe(this) {
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssS", Locale.TAIWAN)
            Log.d(TAG, "[Race] 賽事結束時間: ${sdf.format(it)})")
            BetHorseManager.oddsStatistics()
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
                apiVm?.queryExchangeRate()
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
                // init 賽道
                initRace()
                // 下注 馬開跑
                uiVm?.startRace()
            }
        }
    }

    private fun initRace() {
        binding.apply {
            pbHorse1.max = RaceProgress.GOAL
            pbHorse2.max = RaceProgress.GOAL
            pbHorse3.max = RaceProgress.GOAL
            pbHorse4.max = RaceProgress.GOAL
            pbHorse1.progress = 0
            pbHorse2.progress = 0
            pbHorse3.progress = 0
            pbHorse4.progress = 0
        }
    }

    private fun getExchangeRateByApi() {
        uiVm.updateExchangeRate(-1.0)
        apiVm.queryExchangeRate()
    }

}