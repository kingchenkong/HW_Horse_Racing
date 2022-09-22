package com.homework.horse_racing.model.database.dao

import androidx.room.*
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.database.entity.HorseEntity

@Dao
interface HorseDao {
    @Insert
    suspend fun insert(horseEntity: HorseEntity)

    @Update
    suspend fun update(horseEntity: HorseEntity)

    @Delete
    suspend fun delete(horseEntity: HorseEntity)

    @Query("SELECT * FROM horse WHERE id = :id")
    suspend fun getHorseById(id: Int): HorseEntity

    @Query("SELECT * FROM horse WHERE horseName = :horseName")
    suspend fun getHorseByHorseName(horseName: String): HorseEntity

    @Query("SELECT * FROM horse")
    suspend fun getAll(): List<HorseEntity>
}