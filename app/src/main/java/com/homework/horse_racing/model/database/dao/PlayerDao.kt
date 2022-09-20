package com.homework.horse_racing.model.database.dao

import androidx.room.*
import com.homework.horse_racing.model.database.entity.PlayerEntity

@Dao
interface PlayerDao {
    @Insert
    suspend fun insert(playerEntity: PlayerEntity)

    @Update
    suspend fun update(playerEntity: PlayerEntity)

    @Delete
    suspend fun delete(playerEntity: PlayerEntity)

    @Query("SELECT * FROM player WHERE id = :id")
    suspend fun getPlayerById(id: Int): PlayerEntity

    @Query("SELECT * FROM player")
    suspend fun getAll():List<PlayerEntity>
}