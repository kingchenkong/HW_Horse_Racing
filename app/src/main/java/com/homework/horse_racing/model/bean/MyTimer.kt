package com.homework.horse_racing.model.bean

import androidx.lifecycle.MutableLiveData
import java.util.*

class MyTimer(
    val pastSecLiveData: MutableLiveData<Int>,
    val progressLiveData: MutableLiveData<RaceProgress>
) {
    private var timer: Timer = Timer()
    var pastSec: Int = 0
    val raceProgress: RaceProgress = RaceProgress(
        race1 = Race(0, HorseNumber.NUM_1),
        race2 = Race(0, HorseNumber.NUM_2),
        race3 = Race(0, HorseNumber.NUM_3),
        race4 = Race(0, HorseNumber.NUM_4),
    )

    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            RaceProgress.addNewProgress(raceProgress, RaceProgress.randomNew())
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