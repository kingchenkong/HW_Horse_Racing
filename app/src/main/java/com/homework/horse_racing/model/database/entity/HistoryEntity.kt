package com.homework.horse_racing.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "bet_amount")
    var betAmount: Int, // 下注金額

    @ColumnInfo(name = "bet_horse_id")
    var betHorseId: Int, // 投注第幾號馬

    @ColumnInfo(name = "win_horse_id")
    var winHorseId: Int, // 當期冠軍(哪隻馬)

    @ColumnInfo(name = "this_round_award")
    var thisRoundAward: Int, // 本期獎金 (如果輸了就是 0)

    @ColumnInfo(name = "player_amount_remain")
    var playerAmountRemain: Int, // 本期賭金餘額(台幣)

    @ColumnInfo(name = "visible")
    var visible: Boolean, // visible(是否顯示, 以此代替刪除)
)