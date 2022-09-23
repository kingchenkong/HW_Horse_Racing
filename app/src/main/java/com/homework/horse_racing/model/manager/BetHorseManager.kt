package com.homework.horse_racing.model.manager

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.bean.RaceProgress
import com.homework.horse_racing.model.bean.ResultState
import kotlin.math.roundToInt

object BetHorseManager {
    private val TAG: String = BetHorseManager::class.java.simpleName

    const val INIT_REMAIN: Int = 10000
    const val INIT_ODDS: Double = 2.0
    const val MAX_ODDS: Double = 5.0
    const val MIN_ODDS: Double = 2.0
    const val ODDS_WIN_DIFF: Double = -0.1
    const val ODDS_LOSS_DIFF: Double = 0.1

    const val BET_MAX_USD: Double = 10.0

    // 匯率時價
    val nowExchangeRateLiveData: MutableLiveData<Double> = MutableLiveData<Double>()// 0.0

    // 剩餘賭金
    val twdRemainAmountLiveData: MutableLiveData<Int> = MutableLiveData<Int>() // 10000

    // 賠率
    val horseOdds1LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS
    val horseOdds2LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 2
    val horseOdds3LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 3
    val horseOdds4LiveData: MutableLiveData<Double> = MutableLiveData<Double>() // INIT_ODDS + 2.5

    // 下注哪隻馬 (號碼)
    val focusHorseNumberLiveData: MutableLiveData<HorseNumber> = MutableLiveData<HorseNumber>()

    // 下注金額
    val usdBetAmountLiveData: MutableLiveData<Double> = MutableLiveData<Double>()
    val twdBetAmountLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    // 獎金
    val usdAwardLiveData: MutableLiveData<Double> = MutableLiveData<Double>()
    val twdAwardLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    // 處理結束 timestamp
    val processEndTimeStampLiveData: MutableLiveData<Long> = MutableLiveData<Long>()
    val resultStateLiveData: MutableLiveData<ResultState> = MutableLiveData<ResultState>()
    val resultRaceProgressLiveData: MutableLiveData<RaceProgress> = MutableLiveData<RaceProgress>()

    suspend fun initialize() {
        val playerList = MyApp.checkPlayerExist()
        val horseList = MyApp.checkHorseExist()

        Log.d(TAG, "[db] initialize: player: $playerList")
        Log.d(TAG, "[db] initialize: horse: $horseList")

        twdRemainAmountLiveData.postValue(playerList[0].amount)

        horseList.forEach {
            when (it.number) {
                HorseNumber.NUM_1.number -> horseOdds1LiveData.postValue(it.odds)
                HorseNumber.NUM_2.number -> horseOdds2LiveData.postValue(it.odds)
                HorseNumber.NUM_3.number -> horseOdds3LiveData.postValue(it.odds)
                HorseNumber.NUM_4.number -> horseOdds4LiveData.postValue(it.odds)
                HorseNumber.NUM_CLEAR.number -> {}

            }
        }
        nowExchangeRateLiveData.postValue(0.0)
        focusHorseNumberLiveData.postValue(HorseNumber.NUM_CLEAR)
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

        resultRaceProgressLiveData.postValue(raceProgress)

        // clear winner, loser list
        RaceProgress.clearList(raceProgress)

        // post 處理完畢 timestamp
        processEndTimeStampLiveData.postValue(System.currentTimeMillis())
    }

    // 贏了 拿錢
    fun takeAward(): Int {
        Log.d(TAG, "[Award] 取得獎金: ${twdAwardLiveData.value} ")
        val newRemainAmount: Int = twdRemainAmountLiveData.value!! + twdAwardLiveData.value!!
        twdRemainAmountLiveData.postValue(newRemainAmount)
        return newRemainAmount
    }

    fun checkIsWin(raceProgress: RaceProgress): Boolean {
//        var isWin: Boolean = false
//        raceProgress.goalHorseNumberList.forEach {
//            if (focusHorseNumberLiveData.value!! == it) {
//                resultState.postValue(ResultState.WIN)
//                return true
//            }
//        }
//        resultState.postValue(ResultState.LOSE)
//        return false
        val firstHorse = raceProgress.firstRace.horseNumber
        val focusHorse = focusHorseNumberLiveData.value!!
        Log.d(TAG, "[Race] checkIsWin: first: $firstHorse, focus: $focusHorse")
        val isWin = firstHorse == focusHorse
        val resultState: ResultState = when (isWin) {
            true -> ResultState.WIN
            false -> ResultState.LOSE
        }
        resultStateLiveData.postValue(resultState)
        return isWin
    }

    // 賽後統計
    fun printOdds() {
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
        raceProgress.loserList.forEach {
            when (it.horseNumber) {
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

    fun checkBetAmountIsInValid(usdBetAmount: Double): Boolean {
        return usdBetAmount > BET_MAX_USD
    }

}