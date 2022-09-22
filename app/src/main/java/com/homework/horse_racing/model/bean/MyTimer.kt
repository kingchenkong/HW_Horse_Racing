package com.homework.horse_racing.model.bean

import androidx.lifecycle.MutableLiveData
import java.util.*

class MyTimer(
    val pastSecLiveData: MutableLiveData<Int>,
    val progressLiveData: MutableLiveData<RaceProgress>
) {
    companion object {
        private val TAG: String = MyTimer::class.java.simpleName
    }

    private var timer: Timer = Timer()
    var pastSec: Int = 0
    val raceProgress: RaceProgress = RaceProgress(
        race1 = 0, race2 = 0, race3 = 0, race4 = 0
    )

    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            RaceProgress.addNewProgress(raceProgress, RaceProgress.randomNew())
//            Log.d(TAG, "[Race] raceProgress: $raceProgress")
            pastSec += 1
            pastSecLiveData.postValue(pastSec)
            progressLiveData.postValue(raceProgress)
        }
    }

    fun startTimer() {
        timer.schedule(task, Date(), 100)
    }

    fun stopTimer() {
        timer.cancel()
    }
}