package com.homework.horse_racing.view.activity

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.inputmethod.InputMethodManager
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

        lifecycleScope.launchWhenStarted {
            initUiObserver()
            initManagerObserver()
            initApiObserver()
            uiAction()

            uiVm.initViewData()
            manager.initialize()

            initRace()

            getExchangeRateByApi()
        }
    }

    private fun initUiObserver() {
        uiVm.betAmountText.observe(this) {
            Log.d(TAG, "betAmountText: $it")
            val betAmountUSD: Double = when {
                it.isBlank() -> 0.0
                BetHorseManager.checkBetAmountIsInValid(it.toDouble()) -> {
                    Log.d(TAG, "[Bet] ???????????????????????????!! ")
                    uiVm.betAmountText.postValue("")
                    0.0
                }
                else -> it.toDouble()
            }
            manager.usdBetAmountLiveData.postValue(betAmountUSD)
        }

        uiVm.raceProgressLiveData.observe(this) {
            Log.d(TAG, "[Bet] initUiObserver: raceProgressLiveData")
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
            uiVm.updateRemainAmountText(it.toString())
        }
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
            Log.d(TAG, "manager.focusHorseNumber: ${it.number}, name: ${it.horseName}")
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
                Log.d(TAG, "[Race] ??????????????????: ${sdf.format(it)})")
                // ????????????
                BetHorseManager.calculateAward()

                // enable all btn and input
                enableAllBtnAndInput()
            }
        }
        manager.resultRaceProgressLiveData.observe(this) {
            Log.d(TAG, "[Race] result raceProgress: $it")

            lifecycleScope.launchWhenStarted {

                uiVm.afterResultDBProcess(it)

                afterResultUIProcess()
            }
        }
    }

    private fun afterResultUIProcess() {
        BetHorseManager.printOdds()
        // show dialog
        showResultDialog()
    }

    private fun showResultDialog() {
        lifecycleScope.launchWhenStarted {
            ResultAlertDialog.showResult(
                this@MainActivity,
                BetHorseManager.resultStateLiveData.value ?: ResultState.LOSE
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
                Log.d(TAG, "[Api] btnApi:")
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
                hideKeyBoard()
                when {
                    uiVm!!.remainAmountIsInvalid() -> {
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.REMAIN_AMOUNT_NOT_ENOUGH)
                    }
                    uiVm!!.betAmountIsInvalid() -> {
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.BET_AMOUNT_NOT_ENOUGH)
                    }
                    uiVm!!.diffAmountIsInvalid() -> {
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.DIFF_OF_AMOUNT_INVALID)
                    }
                    uiVm!!.focusNumberNotFound() -> {
                        showBetErrorDialog(BetErrorAlertDialog.ErrorType.FOCUS_NUMBER_INVALID)
                    }
                    else -> {
                        Log.d(TAG, "[Bet] btnBet: GO!")
                        // disable all btn, and input
                        disableAllBtnAndInput()
                        // init ??????
                        initRace()
                        // ??????
                        uiVm!!.deductRemainAmount()
                        // ?????? ?????????
                        uiVm!!.startRace()
                    }
                }
            }
            btnHistory.setOnClickListener {
                Log.d(TAG, "[History] btnHistory:")
                intentHistory()
            }
        }
    }

    private fun intentHistory() {
        lifecycleScope.launchWhenStarted {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }
    }

    private fun hideKeyBoard() {
        lifecycleScope.launchWhenStarted {
            val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
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