package com.homework.horse_racing.model.manager

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.bean.RaceProgress
import kotlin.math.roundToInt

object BetHorseManager {
    private val TAG: String = BetHorseManager::class.java.simpleName

    const val INITIAL_AMOUNT: Int = 10000
    const val INIT_ODDS: Double = 2.0
    const val MAX_ODDS: Double = 5.0
    const val MIN_ODDS: Double = 2.0
    const val ODDS_WIN_DIFF: Double = 0.1
    const val ODDS_LOSS_DIFF: Double = -0.1

    // 匯率時價
    var nowExchangeRateLiveData: MutableLiveData<Double> = MutableLiveData<Double>()// 0.0

    // 賠率
    var horseOdds1LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS
    var horseOdds2LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 2
    var horseOdds3LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 3
    var horseOdds4LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 2.5

    var nowOddsLiveData: MutableLiveData<Double> = MutableLiveData<Double>() // 0.0

    // 下注哪隻馬 (號碼)
    val focusHorseNumberLiveData: MutableLiveData<HorseNumber> = MutableLiveData<HorseNumber>()

    // 下注金額
    val usdBetAmountLiveData: MutableLiveData<Double> = MutableLiveData<Double>()
    val twdBetAmountLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    // 獎金
    val usdAwardLiveData: MutableLiveData<Double> = MutableLiveData<Double>()
    val twdAwardLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    // 處理結束 timestamp
    val processEndTimeStampLiveData:MutableLiveData<Long> = MutableLiveData<Long>()

    fun initialize() {
        nowExchangeRateLiveData.postValue(0.0)
        focusHorseNumberLiveData.postValue(HorseNumber.NUM_CLEAR)
        horseOdds1LiveData.postValue(INIT_ODDS)
        horseOdds2LiveData.postValue(INIT_ODDS)
        horseOdds3LiveData.postValue(INIT_ODDS)
        horseOdds4LiveData.postValue(INIT_ODDS)

        usdBetAmountLiveData.postValue(0.0)
        usdAwardLiveData.postValue(0.0)
        twdAwardLiveData.postValue(0)
    }

    fun getNowOdds(horseNumber: HorseNumber): Double {
        return when (horseNumber) {
            HorseNumber.NUM_CLEAR -> 0.0
            HorseNumber.NUM_1 -> horseOdds1LiveData.value ?: INIT_ODDS
            HorseNumber.NUM_2 -> horseOdds2LiveData.value ?: INIT_ODDS
            HorseNumber.NUM_3 -> horseOdds3LiveData.value ?: INIT_ODDS
            HorseNumber.NUM_4 -> horseOdds4LiveData.value ?: INIT_ODDS
        }
    }

    fun calculateAward() {
        val odds: Double = getNowOdds(focusHorseNumberLiveData.value ?: HorseNumber.NUM_CLEAR)
        val betAmountUSD: Double = usdBetAmountLiveData.value ?: 0.0
        val awardUSD: Double = betAmountUSD * odds
        val nowExchangeRate: Double = nowExchangeRateLiveData.value ?: 0.0
        val awardTWD: Int = (awardUSD * nowExchangeRate).toInt()
        Log.d(TAG, "calculateAward: odds: $odds, award: USD: $awardUSD, TWD: $awardTWD")
        usdAwardLiveData.postValue(awardUSD)
        twdAwardLiveData.postValue(awardTWD)
    }

    fun calculateTWDBetAmount() {
        val exRate: Double = nowExchangeRateLiveData.value ?: 0.0
        val usdBetAmount: Double = usdBetAmountLiveData.value ?: 0.0
        val exchangeToTWD: Double = exRate * usdBetAmount
        val twdBetAmount: Int = exchangeToTWD.toInt()
        Log.d(TAG, "calculateTWDBetAmount: exRate: $exRate, usd: $usdBetAmount, twd: $twdBetAmount")
        twdBetAmountLiveData.postValue(twdBetAmount)
    }

    fun processAfterGoal(raceProgress: RaceProgress) {
        winnerOddsProcess(raceProgress)
        loserOddsProcess(raceProgress)

        // clear winner, loser list
        RaceProgress.clearList(raceProgress)

        // post 處理完畢 timestamp
        processEndTimeStampLiveData.postValue(System.currentTimeMillis())
    }

    // 賽後統計
    fun oddsStatistics() {
        Log.d(
            TAG, "[Race] Odds, " +
                    "h1: ${horseOdds1LiveData.value}, " +
                    "h2: ${horseOdds2LiveData.value}, " +
                    "h3: ${horseOdds3LiveData.value}, " +
                    "h4: ${horseOdds4LiveData.value}"
        )
    }

    private fun winnerOddsProcess(raceProgress: RaceProgress) {
        raceProgress.goalHorseNumberList.forEach {
            when (it) {
                HorseNumber.NUM_1 -> {
                    val last = horseOdds1LiveData.value!!
                    val new = checkLimit(last + ODDS_WIN_DIFF)
                    horseOdds1LiveData.postValue(new)
                }
                HorseNumber.NUM_2 -> {
                    val last = horseOdds2LiveData.value!!
                    val new = checkLimit(last + ODDS_WIN_DIFF)
                    horseOdds2LiveData.postValue(new)
                }
                HorseNumber.NUM_3 -> {
                    val last = horseOdds3LiveData.value!!
                    val new = checkLimit(last + ODDS_WIN_DIFF)
                    horseOdds3LiveData.postValue(new)
                }
                HorseNumber.NUM_4 -> {
                    val last = horseOdds4LiveData.value!!
                    val new = checkLimit(last + ODDS_WIN_DIFF)
                    horseOdds4LiveData.postValue(new)
                }
                else -> {}
            }
        }
    }

    private fun loserOddsProcess(raceProgress: RaceProgress) {
        raceProgress.notGoalHorseNumberList.forEach {
            when (it) {
                HorseNumber.NUM_1 -> {
                    val last = horseOdds1LiveData.value!!
                    val new = checkLimit(last + ODDS_LOSS_DIFF)
                    horseOdds1LiveData.postValue(new)
                }
                HorseNumber.NUM_2 -> {
                    val last = horseOdds2LiveData.value!!
                    val new = checkLimit(last + ODDS_LOSS_DIFF)
                    horseOdds2LiveData.postValue(new)
                }
                HorseNumber.NUM_3 -> {
                    val last = horseOdds3LiveData.value!!
                    val new = checkLimit(last + ODDS_LOSS_DIFF)
                    horseOdds3LiveData.postValue(new)
                }
                HorseNumber.NUM_4 -> {
                    val last = horseOdds4LiveData.value!!
                    val new = checkLimit(last + ODDS_LOSS_DIFF)
                    horseOdds4LiveData.postValue(new)
                }
                else -> {}
            }
        }
    }

    private fun checkLimit(odds: Double): Double {
        // 四捨五入 (Double會有 極小數 精度差)
        val roundOdds: Double = (odds * 10.0).roundToInt() / 10.0
        return when {
            roundOdds > MAX_ODDS -> MAX_ODDS
            roundOdds < MIN_ODDS -> MIN_ODDS
            else -> roundOdds
        }
    }

}