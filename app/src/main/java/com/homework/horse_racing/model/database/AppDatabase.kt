package com.homework.horse_racing.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.homework.horse_racing.model.database.dao.HistoryDao
import com.homework.horse_racing.model.database.dao.HorseDao
import com.homework.horse_racing.model.database.dao.PlayerDao
import com.homework.horse_racing.model.database.entity.HistoryEntity
import com.homework.horse_racing.model.database.entity.HorseEntity
import com.homework.horse_racing.model.database.entity.PlayerEntity

@Database(
    entities = [
        PlayerEntity::class, HorseEntity::class, HistoryEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME: String = "app_db"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        // 這裡用的是 Singleton 寫法，也是官方所推薦的，因為實體的產生很耗資源，而且也不需要多個資料庫實體，所以宣告為 Singleton 即可。
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
//                .fallbackToDestructiveMigration() // migration: fallback
                .build()
                .also { instance = it }
    }

    abstract fun getPlayerDao(): PlayerDao
    abstract fun getHorseDao(): HorseDao
    abstract fun getHistoryDao(): HistoryDao
}