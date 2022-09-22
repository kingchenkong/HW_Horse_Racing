package com.homework.horse_racing.model.bean

import android.util.Log

class RaceProgress(
    var race1: Int, var race2: Int, var race3: Int, var race4: Int
) {
    val goalHorseNumberList: MutableList<HorseNumber> = mutableListOf()
    val notGoalHorseNumberList: MutableList<HorseNumber> = mutableListOf()

    companion object {
        val TAG: String = RaceProgress::class.java.simpleName

        const val GOAL: Int = 100
        private const val STEP_MIN: Int = 1
        private const val STEP_MAX: Int = 10

        fun addNewProgress(old: RaceProgress, new: RaceProgress) {
//            Log.d(TAG, "[Race] addNewProgress: new: $new")
            old.race1 += new.race1
            old.race2 += new.race2
            old.race3 += new.race3
            old.race4 += new.race4
        }

        fun randomNew(): RaceProgress {
            return RaceProgress(
                (STEP_MIN..STEP_MAX).random(),
                (STEP_MIN..STEP_MAX).random(),
                (STEP_MIN..STEP_MAX).random(),
                (STEP_MIN..STEP_MAX).random(),
            )
        }

        fun checkWinner(raceProgress: RaceProgress) {
            if (checkGoal(raceProgress.race1)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_1)
            }
            if (checkGoal(raceProgress.race2)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_2)
            }
            if (checkGoal(raceProgress.race3)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_3)
            }
            if (checkGoal(raceProgress.race4)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_4)
            }
        }

        fun checkLoser(raceProgress: RaceProgress) {
            if (checkNotGoal(raceProgress.race1)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_1)
            }
            if (checkNotGoal(raceProgress.race2)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_2)
            }
            if (checkNotGoal(raceProgress.race3)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_3)
            }
            if (checkNotGoal(raceProgress.race4)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_4)
            }
        }

        private fun checkGoal(progress: Int): Boolean {
            return progress >= GOAL
        }

        private fun checkNotGoal(progress: Int): Boolean {
            return progress < GOAL
        }

        fun clearList(raceProgress: RaceProgress) {
            raceProgress.goalHorseNumberList.clear()
            raceProgress.notGoalHorseNumberList.clear()
        }
    }

    override fun toString(): String {
        return "RaceProgress (race1=$race1, race2=$race2, race3=$race3, race4=$race4)"
    }

}