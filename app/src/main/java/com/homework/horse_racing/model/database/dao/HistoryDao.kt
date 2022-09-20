package com.homework.horse_racing.model.database.dao

import androidx.room.*
import com.homework.horse_racing.model.database.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(historyEntity: HistoryEntity)

    @Update
    suspend fun update(historyEntity: HistoryEntity)

    @Delete
    suspend fun delete(historyEntity: HistoryEntity)

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Int): HistoryEntity

    @Query("SELECT * FROM history")
    suspend fun getAll(): List<HistoryEntity>
}
