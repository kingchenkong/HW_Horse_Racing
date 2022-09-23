package com.homework.horse_racing.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horse")
data class HorseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "odds")
    var odds: Double, // 賠率

    @ColumnInfo(name = "horseName")
    var name: String, // 幾號馬(名字)
) {
    override fun toString(): String {
        return "HorseEntity(id=$id, odds=$odds, name='$name')"
    }
}
