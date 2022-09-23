package com.homework.horse_racing.view.activity

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.bean.RaceProgress
import com.homework.horse_racing.model.bean.ResultState
import com.homework.horse_racing.model.manager.BetHorseManager
import com.homework.horse_racing.view.alert.BetErrorAlertDialog
import com.homework.horse_racing.view.alert.ResultAlertDialog
import com.homework.horse_racing.view_model.api.ExchangeApiViewModel
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

        manager.initialize()
        uiVm.initViewData()

        initRace()

        getExchangeRateByApi()
    }

    private fun initUiObserver() {
        uiVm.betAmountText.observe(this) {
            Log.d(TAG, "betAmountText: $it")
            val betAmountUSD: Double = when {
                it.isBlank() -> 0.0
                BetHorseManager.checkBetAmountIsInValid(it.toDouble()) -> {
                    Log.d(TAG, "[Bet] 我們沒有賭那麼大啦!! ")
                    uiVm.betAmountText.postValue("")
                    0.0
                }
                else -> it.toDouble()
            }
            manager.usdBetAmountLiveData.postValue(betAmountUSD)
        }

        uiVm.raceProgressLiveData.observe(this) {
            binding.pbHorse1.progress = it.race1.progress
            binding.pbHorse2.progress = it.race2.progress
            binding.pbHorse3.progress = it.race3.progress
            binding.pbHorse4.progress = it.race4.progress

            RaceProgress.checkGoalList(it)
            RaceProgress.checkWinner(it)
            if (it.goalHorseNumberList.size > 0) {
                uiVm.processAfterGoal(it)
            }
        }
    }

    private fun initManagerObserver() {
        manager.twdRemainAmountLiveData.observe(this) {
            lifecycleScope.launchWhenStarted {
                uiVm.updatePlayerRemainAmount(it)
            }
        }
        manager.horseOdds1LiveData.observe(this) {
            uiVm.oddsHorse1Text.postValue(it.toString())
            lifecycleScope.launchWhenStarted {
                uiVm.updateHorseOdds(HorseNumber.NUM_1, it)
            }
        }
        manager.horseOdds2LiveData.observe(this) {
            uiVm.oddsHorse2Text.postValue(it.toString())
            lifecycleScope.launchWhenStarted {
                uiVm.updateHorseOdds(HorseNumber.NUM_2, it)
            }
        }
        manager.horseOdds3LiveData.observe(this) {
            uiVm.oddsHorse3Text.postValue(it.toString())
            lifecycleScope.launchWhenStarted {
                uiVm.updateHorseOdds(HorseNumber.NUM_3, it)
            }
        }
        manager.horseOdds4LiveData.observe(this) {
            uiVm.oddsHorse4Text.postValue(it.toString())
            lifecycleScope.launchWhenStarted {
                uiVm.updateHorseOdds(HorseNumber.NUM_4, it)
            }
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
            lifecycleScope.launchWhenStarted {
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssS", Locale.TAIWAN)
                Log.d(TAG, "[Race] 賽事結束時間: ${sdf.format(it)})")
                BetHorseManager.oddsStatistics()
                // 報喜?嘲諷?
                showResultDialog()
                // 更新獎金
                BetHorseManager.calculateAward()
                // enable all btn and input
                enableAllBtnAndInput()
            }
        }
        manager.raceProgressLiveData.observe(this) {
            lifecycleScope.launchWhenStarted {
                Log.d(TAG, "[Db] raceProgressLiveData: ")
                // insert history
                uiVm.insertHistory(it)
            }
        }
    }

    private fun showResultDialog() {
        lifecycleScope.launchWhenStarted {
            ResultAlertDialog.showResult(
                this@MainActivity,
                BetHorseManager.resultState.value ?: ResultState.LOSE
            )
        }
    }

    private fun initApiObserver() {
        apiVm.exchangeApiLiveData.observe(this) {
            lifecycleScope.launchWhenStarted {
                Log.d(TAG, "initObserver: exchangeRateLiveData: $it")
                uiVm.updateExchangeRate(it.Exrate)
                uiVm.updateTime(it.UTC)
                manager.nowExchangeRateLiveData.postValue(it.Exrate)

                BetHorseManager.calculateTWDBetAmount()
                BetHorseManager.calculateAward()
            }
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
                Log.d(TAG, "[Bet] btnBet:")

                when {
                    uiVm!!.remainAmountIsInvalid() -> {
//                        Log.e(TAG, "[Bet] btnBet: 喔ㄟ! 你沒錢啦!!")
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.REMAIN_AMOUNT_NOT_ENOUGH)
                    }
                    uiVm!!.betAmountIsInvalid() -> {
//                        Log.e(TAG, "[Bet] btnBet: 賭金啦! 先下注好嗎?")
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.BET_AMOUNT_NOT_ENOUGH)
                    }
                    uiVm!!.diffAmountIsInvalid() -> {
//                        Log.e(TAG, "[Bet] btnBet: 肖欸heo!? 沒那個咖噌不要吃那個瀉藥 =.=凸")
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.DIFF_OF_AMOUNT_INVALID)
                    }
                    uiVm!!.focusNumberNotFound() -> {
//                        Log.e(TAG, "[Bet] btnBet: 馬啦! 你的馬咧?")
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.FOCUS_NUMBER_INVALID)
                    }
                    else -> {
                        Log.d(TAG, "[Bet] btnBet: GO!")

                        // disable all btn, and input
                        disableAllBtnAndInput()
                        // init 賽道
                        initRace()
                        // 扣錢
                        uiVm!!.deductRemainAmount()
                        // 下注 馬開跑
                        uiVm!!.startRace()
                    }
                }
            }
        }
    }

    private fun showBetErrorDialog(errorType: BetErrorAlertDialog.ErrorType) {
        lifecycleScope.launchWhenStarted {
            BetErrorAlertDialog.showError(this@MainActivity, errorType)
        }
    }

    private fun disableAllBtnAndInput() {
        binding.apply {
            edtBetAmount.inputType = InputType.TYPE_NULL
            btnApi.isEnabled = false
            btnBet.isEnabled = false
            btnHorse1.isEnabled = false
            btnHorse2.isEnabled = false
            btnHorse3.isEnabled = false
            btnHorse4.isEnabled = false
        }
    }

    private fun enableAllBtnAndInput() {
        binding.apply {
            edtBetAmount.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
            btnApi.isEnabled = true
            btnBet.isEnabled = true
            btnHorse1.isEnabled = true
            btnHorse2.isEnabled = true
            btnHorse3.isEnabled = true
            btnHorse4.isEnabled = true
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