package com.homework.horse_racing.view_model.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homework.horse_racing.model.bean.MyTimer
import com.homework.horse_racing.model.bean.RaceProgress
import com.homework.horse_racing.model.manager.BetHorseManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel : ViewModel() {
    companion object {
        private val TAG: String = MainActivityViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

    fun initViewData() {
        viewModelScope.launch {
            _exchangeRateText.postValue(exchangeRateToText(0.0))
            _timeText.postValue("- - -")
            _remainAmountText.postValue(BetHorseManager.INITIAL_AMOUNT.toString())
            oddsHorse1Text.postValue(BetHorseManager.INIT_ODDS.toString())
            oddsHorse2Text.postValue(BetHorseManager.INIT_ODDS.toString())
            oddsHorse3Text.postValue(BetHorseManager.INIT_ODDS.toString())
            oddsHorse4Text.postValue(BetHorseManager.INIT_ODDS.toString())
            _focusNumberText.postValue("請選擇號碼")
            _betAmountExchangeToTWDText.postValue("0")
            _awardUSDText.postValue("0")
            _awardTWDText.postValue("0")
        }
    }

//    fun fetchProgress() {
//        viewModelScope.launch {
//            RaceEmitter.raceFlow()
//                .onEach {
//                    Log.d(TAG, "fetchProgress: $it")
//                }
//                .catch {
//                    Log.d(TAG, "fetchProgress: $this")
//                }
//                .collect {
//                    Log.d(TAG, "fetchProgress: $it")
//                }
//        }
//    }

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

    //region LiveData: output
    // 匯率時價: USD -> TWD
    private val _exchangeRateText: MutableLiveData<String> = MutableLiveData<String>()
    var exchangeRateText: LiveData<String> = _exchangeRateText

    fun updateExchangeRate(rate: Double) {
        if (rate < 0) {
            _exchangeRateText.postValue("loading...")
        } else {
            _exchangeRateText.postValue(exchangeRateToText(rate))
        }
    }

    // 匯率取得時間
    private val _timeText: MutableLiveData<String> = MutableLiveData<String>()
    var timeText: LiveData<String> = _timeText

    fun updateTime(timeString: String) {
        _timeText.postValue(parseUTCToTW(timeString))
    }

    // 剩餘賭金
    private val _remainAmountText: MutableLiveData<String> = MutableLiveData<String>()
    val remainAmountText: LiveData<String> = _remainAmountText

    // 賠率: 1 號馬
//    private val _oddsHorse1Text: MutableLiveData<String> = MutableLiveData<String>()
//    val oddsHorse1Text: LiveData<String> = _oddsHorse1Text
    val oddsHorse1Text: MutableLiveData<String> = MutableLiveData<String>()

    // 賠率: 2 號馬
//    private val _oddsHorse2Text: MutableLiveData<String> = MutableLiveData<String>()
//    val oddsHorse2Text: LiveData<String> = _oddsHorse2Text
    val oddsHorse2Text: MutableLiveData<String> = MutableLiveData<String>()

    // 賠率: 3 號馬
//    private val _oddsHorse3Text: MutableLiveData<String> = MutableLiveData<String>()
//    val oddsHorse3Text: LiveData<String> = _oddsHorse3Text
    val oddsHorse3Text: MutableLiveData<String> = MutableLiveData<String>()

    // 賠率: 4 號馬
//    private val _oddsHorse4Text: MutableLiveData<String> = MutableLiveData<String>()
//    val oddsHorse4Text: LiveData<String> = _oddsHorse4Text
    val oddsHorse4Text: MutableLiveData<String> = MutableLiveData<String>()

    // 下注 幾號碼
    private val _focusNumberText: MutableLiveData<String> = MutableLiveData<String>()
    val focusNumberText: LiveData<String> = _focusNumberText

    fun updateFocusNumberText(horseName: String) {
        when {
            horseName.isBlank() -> {
                _focusNumberText.postValue("請選擇號碼")
            }
            else -> {
                _focusNumberText.postValue("已選擇: $horseName")
            }
        }
    }

    // 下注金額轉換為台幣, 目前為: 無條件捨去法
    private val _betAmountExchangeToTWDText: MutableLiveData<String> = MutableLiveData<String>()
    val betAmountExchangeToTWDText: LiveData<String> = _betAmountExchangeToTWDText

    fun updateBetAmountToTWD(betAmount: Int) {
        _betAmountExchangeToTWDText.postValue(betAmount.toString())
    }

    // 本期獎金: USD
    private val _awardUSDText: MutableLiveData<String> = MutableLiveData<String>()
    val awardUSDText: LiveData<String> = _awardUSDText

    fun updateAwardUSDText(award: Double) {
        _awardUSDText.postValue(award.toString())
    }

    // 本期獎金: TWD
    private val _awardTWDText: MutableLiveData<String> = MutableLiveData<String>()
    val awardTWDText: LiveData<String> = _awardTWDText

    fun updateAwardTWDText(award: Int) {
        _awardTWDText.postValue(award.toString())
    }

    //endregion

    //region LiveData: Input

    // 輸入: 下注金額
    val betAmountText: MutableLiveData<String> = MutableLiveData<String>()

    // Race
    val pastSecLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    val raceProgressLiveData: MutableLiveData<RaceProgress> = MutableLiveData<RaceProgress>()

    //endregion

    lateinit var raceTimer: MyTimer
    fun startRace() {
        raceTimer = MyTimer(pastSecLiveData, raceProgressLiveData)
        raceTimer.startTimer()
    }

    fun stopRace() {
        raceTimer.stopTimer()
    }

}