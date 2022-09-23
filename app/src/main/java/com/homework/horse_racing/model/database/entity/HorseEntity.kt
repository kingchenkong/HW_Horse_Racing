package com.homework.horse_racing.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.homework.horse_racing.model.bean.HorseNumber

@Entity(tableName = "horse")
data class HorseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "odds")
    var odds: Double, // 賠率

    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "horseName")
    var horseName: String, // 幾號馬(名字)
) {
    override fun toString(): String {
        return "HorseEntity(id=$id, odds=$odds, number=$number, horseName='$horseName')"
    }

    fun getIntByEnum(horseNumber: HorseNumber): Int {
        return when (horseNumber) {
            HorseNumber.NUM_CLEAR -> -1
            HorseNumber.NUM_1 -> 1
            HorseNumber.NUM_2 -> 2
            HorseNumber.NUM_3 -> 3
            HorseNumber.NUM_4 -> 4
        }
    }
}
