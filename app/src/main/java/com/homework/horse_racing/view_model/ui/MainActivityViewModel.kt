package com.homework.horse_racing.view_model.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.bean.MyTimer
import com.homework.horse_racing.model.bean.RaceProgress
import com.homework.horse_racing.model.bean.ResultState
import com.homework.horse_racing.model.database.dao.HistoryDao
import com.homework.horse_racing.model.database.dao.HorseDao
import com.homework.horse_racing.model.database.dao.PlayerDao
import com.homework.horse_racing.model.database.entity.HistoryEntity
import com.homework.horse_racing.model.database.entity.HorseEntity
import com.homework.horse_racing.model.manager.BetHorseManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel : ViewModel() {
    companion object {
        private val TAG: String = MainActivityViewModel::class.java.simpleName
    }

    lateinit var raceTimer: MyTimer

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
    fun updateRemainAmountText(text: String) {
        _remainAmountText.postValue(text)
    }

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

    // 結果: 本期贏家
    private val _resultTextLiveData: MutableLiveData<String> = MutableLiveData<String>()
    val resultTextLiveData: LiveData<String> = _resultTextLiveData

    //endregion

    //region LiveData: Input

    // 輸入: 下注金額
    val betAmountText: MutableLiveData<String> = MutableLiveData<String>()

    // Race
    val pastSecLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    val raceProgressLiveData: MutableLiveData<RaceProgress> = MutableLiveData<RaceProgress>()

    //endregion

    fun initViewData() {
        viewModelScope.launch {
            _exchangeRateText.postValue(exchangeRateToText(0.0))
            _timeText.postValue("- - -")
            _remainAmountText.postValue("0")
            _focusNumberText.postValue("請選擇號碼")
            _betAmountExchangeToTWDText.postValue("0")
            _awardUSDText.postValue("0")
            _awardTWDText.postValue("0")
        }
    }
    //region Exchange rate

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

    //endregion

    //region timer

    fun startRace() {
        raceTimer = MyTimer(pastSecLiveData, raceProgressLiveData)
        raceTimer.startTimer()
    }

    fun stopRace() {
        raceTimer.stopTimer()
    }

    //endregion

    //region bet error

    fun remainAmountIsInvalid(): Boolean { // 賭金 TWD <= 0
        return BetHorseManager.twdRemainAmountLiveData.value!! <= 0
    }

    fun betAmountIsInvalid(): Boolean { // 下注金 TWD <= 0
        return BetHorseManager.twdBetAmountLiveData.value!! <= 0
    }

    fun diffAmountIsInvalid(): Boolean { // 賭金 < 下注金
        return BetHorseManager.twdRemainAmountLiveData.value!! < BetHorseManager.twdBetAmountLiveData.value!!
    }

    fun focusNumberNotFound(): Boolean { // 沒選馬
        return BetHorseManager.focusHorseNumberLiveData.value!! == HorseNumber.NUM_CLEAR
    }

    //endregion

    //region bet start

    // 扣除 賭金
    fun deductRemainAmount() {
        val newRemainAmount: Int =
            BetHorseManager.twdRemainAmountLiveData.value!! - BetHorseManager.twdBetAmountLiveData.value!!
        BetHorseManager.twdRemainAmountLiveData.postValue(newRemainAmount)
        _remainAmountText.postValue(newRemainAmount.toString())
    }

    //endregion

    //region result 結算

    fun processAfterGoal(raceProgress: RaceProgress) {
        stopRace()

        outputTvResult(raceProgress)
//        RaceProgress.checkNotGoalList(raceProgress)
        RaceProgress.checkLoser(raceProgress)

        // 有贏嗎?
        resultUIProcess(raceProgress)

        // 完賽後處理
        BetHorseManager.processAfterGoal(raceProgress)

    }

    private fun outputTvResult(raceProgress: RaceProgress) {
        _resultTextLiveData.postValue("${raceProgress.firstRace.horseNumber.horseName} 贏了!!")
    }

    private fun resultUIProcess(raceProgress: RaceProgress) {
        if (BetHorseManager.checkIsWin(raceProgress)) {
            val award: Int = BetHorseManager.takeAward()
            _remainAmountText.postValue(award.toString())
        }
    }

    //endregion

    //region Database operation

    fun afterResultDBProcess(raceProgress: RaceProgress) {
        viewModelScope.launch {
            // update player
            updateDBPlayerRemain(BetHorseManager.twdRemainAmountLiveData.value!!)

            // update horse odds in db
            updateDbHorseOdds()

            // insert history
            insertHistory(raceProgress)
        }
    }

    suspend fun updateDBPlayerRemain(remainTWD: Int) {
        val dao: PlayerDao = MyApp.appDatabase.getPlayerDao()
        val entity = dao.getAll()[0]
        entity.amount = remainTWD
        dao.update(entity)
        Log.d(TAG, "[Db] updatePlayerRemainAmount: $entity")
    }

    private suspend fun updateDbHorseOdds() {
        updateHorseOdds(HorseNumber.NUM_1, BetHorseManager.horseOdds1LiveData.value!!)
        updateHorseOdds(HorseNumber.NUM_2, BetHorseManager.horseOdds2LiveData.value!!)
        updateHorseOdds(HorseNumber.NUM_3, BetHorseManager.horseOdds3LiveData.value!!)
        updateHorseOdds(HorseNumber.NUM_4, BetHorseManager.horseOdds4LiveData.value!!)
    }

    private suspend fun updateHorseOdds(horseNumber: HorseNumber, odds: Double) {
        Log.d(TAG, "[Db] updateHorseOdds: ${horseNumber.horseName}, new odds: $odds")
        val dao: HorseDao = MyApp.appDatabase.getHorseDao()
        when (horseNumber) {
            HorseNumber.NUM_CLEAR -> {}
            HorseNumber.NUM_1 -> {
                val entity: HorseEntity? = dao.getHorseByHorseName(HorseNumber.NUM_1.horseName)
                Log.d(TAG, "[Db]: $entity")
                if (entity == null) {
                    dao.insert(HorseEntity(0, odds, 1, HorseNumber.NUM_1.horseName))
                } else {
                    dao.update(HorseEntity(entity.id, odds, 1, HorseNumber.NUM_1.horseName))
                }
            }
            HorseNumber.NUM_2 -> {
                val entity = dao.getHorseByHorseName(HorseNumber.NUM_2.horseName)
                Log.d(TAG, "[Db]: $entity")
                if (entity == null) {
                    dao.insert(HorseEntity(0, odds, 2, HorseNumber.NUM_2.horseName))
                } else {
                    dao.update(HorseEntity(entity.id, odds, 2, HorseNumber.NUM_2.horseName))
                }
            }
            HorseNumber.NUM_3 -> {
                val entity = dao.getHorseByHorseName(HorseNumber.NUM_3.horseName)
                Log.d(TAG, "[Db]: $entity")
                if (entity == null) {
                    dao.insert(HorseEntity(0, odds, 3, HorseNumber.NUM_3.horseName))
                } else {
                    dao.update(HorseEntity(entity.id, odds, 3, HorseNumber.NUM_3.horseName))
                }
            }
            HorseNumber.NUM_4 -> {
                val entity = dao.getHorseByHorseName(HorseNumber.NUM_4.horseName)
                Log.d(TAG, "[Db]: $entity")
                if (entity == null) {
                    dao.insert(HorseEntity(0, odds, 4, HorseNumber.NUM_4.horseName))
                } else {
                    dao.update(HorseEntity(entity.id, odds, 4, HorseNumber.NUM_4.horseName))
                }
            }
        }
    }

    suspend fun insertHistory(raceProgress: RaceProgress) {
        Log.d(TAG, "[DB] insertHistory: ")
        val dao: HistoryDao = MyApp.appDatabase.getHistoryDao()
        val award: Int = when (BetHorseManager.resultStateLiveData.value!!) {
            ResultState.WIN -> {
                BetHorseManager.twdAwardLiveData.value!!
            }
            else -> {
                0
            }
        }
        val horseDao: HorseDao = MyApp.appDatabase.getHorseDao()

        val betHorseEntity =
            horseDao.getHorseByHorseName(BetHorseManager.focusHorseNumberLiveData.value!!.horseName)!!
        val betHorseId: Int = betHorseEntity.id
        val betHorseNumber: Int = betHorseEntity.number

        val winHorseEntity =
            horseDao.getHorseByHorseName(raceProgress.firstRace.horseNumber.horseName)!!
        val winHorseId: Int = winHorseEntity.id
        val winHorseNumber: Int = winHorseEntity.number

        val entity = HistoryEntity(
            id = 0,
            betAmount = BetHorseManager.twdBetAmountLiveData.value!!,
            betHorseId = betHorseId,
            betHorseNumber = betHorseNumber,
            winHorseId = winHorseId,
            winHorseNumber = winHorseNumber,
            thisRoundAward = award,
            playerAmountRemain = BetHorseManager.twdRemainAmountLiveData.value!!,
            visible = true,
        )
        dao.insert(entity)
        Log.d(TAG, "[Db]: $entity")
    }

    //endregion

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

}