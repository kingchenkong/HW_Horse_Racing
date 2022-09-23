package com.homework.horse_racing.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "amount")
    var amount: Int, // 持有賭金
) {
    override fun toString(): String {
        return "PlayerEntity(id=$id, amount=$amount)"
    }
}
