package com.homework.horse_racing.model.bean

import android.util.Log

class RaceProgress(
    var race1: Race, var race2: Race, var race3: Race, var race4: Race
) {
    val goalHorseNumberList: MutableList<HorseNumber> = mutableListOf()
    val notGoalHorseNumberList: MutableList<HorseNumber> = mutableListOf()

    val loserList: MutableList<Race> = mutableListOf()
    var firstRace: Race = Race(0, HorseNumber.NUM_CLEAR)

    companion object {
        val TAG: String = RaceProgress::class.java.simpleName

        const val GOAL: Int = 100
        private const val STEP_MIN: Int = 1
        private const val STEP_MAX: Int = 10

        fun addNewProgress(old: RaceProgress, new: RaceProgress) {
            old.race1.progress += new.race1.progress
            old.race2.progress += new.race2.progress
            old.race3.progress += new.race3.progress
            old.race4.progress += new.race4.progress
        }

        fun randomNew(): RaceProgress {
            return RaceProgress(
                Race((STEP_MIN..STEP_MAX).random(), HorseNumber.NUM_1),
                Race((STEP_MIN..STEP_MAX).random(), HorseNumber.NUM_2),
                Race((STEP_MIN..STEP_MAX).random(), HorseNumber.NUM_3),
                Race((STEP_MIN..STEP_MAX).random(), HorseNumber.NUM_4),
            )
        }

        fun checkGoalList(raceProgress: RaceProgress) {
            if (checkGoal(raceProgress.race1.progress)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_1)
            }
            if (checkGoal(raceProgress.race2.progress)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_2)
            }
            if (checkGoal(raceProgress.race3.progress)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_3)
            }
            if (checkGoal(raceProgress.race4.progress)) {
                raceProgress.goalHorseNumberList.add(HorseNumber.NUM_4)
            }
        }

        fun checkNotGoalList(raceProgress: RaceProgress) {
            if (checkNotGoal(raceProgress.race1.progress)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_1)
            }
            if (checkNotGoal(raceProgress.race2.progress)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_2)
            }
            if (checkNotGoal(raceProgress.race3.progress)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_3)
            }
            if (checkNotGoal(raceProgress.race4.progress)) {
                raceProgress.notGoalHorseNumberList.add(HorseNumber.NUM_4)
            }
        }

        fun checkWinner(raceProgress: RaceProgress) {
            val raceList = listOf(
                raceProgress.race1,
                raceProgress.race2,
                raceProgress.race3,
                raceProgress.race4
            )
            val sortedRaceList = raceList.sortedBy { it.progress }
            sortedRaceList.forEach {
                Log.d(
                    TAG,
                    "checkWinner: horse: ${it.horseNumber.name}, progress: ${it.progress}"
                )
            }

            val firstRace = sortedRaceList.last()
            raceProgress.firstRace = firstRace
            Log.d(
                TAG,
                "checkWinner: first: horse: ${firstRace.horseNumber}, progress: ${firstRace.progress}"
            )
        }

        fun checkLoser(raceProgress: RaceProgress) {
            val firstHorseNumber = raceProgress.firstRace.horseNumber
            if (raceProgress.race1.horseNumber != firstHorseNumber) {
                raceProgress.loserList.add(raceProgress.race1)
            }
            if (raceProgress.race2.horseNumber != firstHorseNumber) {
                raceProgress.loserList.add(raceProgress.race2)
            }
            if (raceProgress.race3.horseNumber != firstHorseNumber) {
                raceProgress.loserList.add(raceProgress.race3)
            }
            if (raceProgress.race4.horseNumber != firstHorseNumber) {
                raceProgress.loserList.add(raceProgress.race4)
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