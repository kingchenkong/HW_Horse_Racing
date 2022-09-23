package com.homework.horse_racing.model.bean

data class Race(
    var progress: Int,
    val horseNumber: HorseNumber,
) {
    override fun toString(): String {
        return "Race(progress=$progress, horseNumber=$horseNumber)"
    }
}
