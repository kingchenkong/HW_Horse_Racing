package com.homework.horse_racing.application

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.homework.horse_racing.model.bean.HorseNumber
import com.homework.horse_racing.model.database.AppDatabase
import com.homework.horse_racing.model.database.entity.HorseEntity
import com.homework.horse_racing.model.database.entity.PlayerEntity
import com.homework.horse_racing.model.manager.BetHorseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApp : MultiDexApplication() {
    companion object {
        val TAG: String = MyApp::class.java.simpleName

        lateinit var appDatabase: AppDatabase

        suspend fun checkPlayerExist(): List<PlayerEntity> {
            Log.d(TAG, "[Db] checkPlayerExist: ")
            val playerDao = appDatabase.getPlayerDao()
            val playerList = playerDao.getAll()
            return playerList.ifEmpty {
                val initPlayer = PlayerEntity(0, BetHorseManager.INIT_REMAIN)
                playerDao.insert(initPlayer)
                listOf(initPlayer)
            }
        }

        suspend fun checkHorseExist(): List<HorseEntity> {
            Log.d(TAG, "[DB] checkHorseExist: ")
            val horseDao = appDatabase.getHorseDao()
            val horseList = horseDao.getAll()
            return horseList.ifEmpty {
                val initHorseList: List<HorseEntity> = listOf(
                    HorseEntity(0, 0.0, -1, HorseNumber.NUM_CLEAR.horseName),
                    HorseEntity(0, BetHorseManager.INIT_ODDS, 1, HorseNumber.NUM_1.horseName),
                    HorseEntity(0, BetHorseManager.INIT_ODDS, 2, HorseNumber.NUM_2.horseName),
                    HorseEntity(0, BetHorseManager.INIT_ODDS, 3, HorseNumber.NUM_3.horseName),
                    HorseEntity(0, BetHorseManager.INIT_ODDS, 4, HorseNumber.NUM_4.horseName),
                )
                initHorseList.forEach { horseDao.insert(it) }
                initHorseList
            }
        }
    }

    private val jobs = SupervisorJob()
    private val applicationScope: CoroutineScope = CoroutineScope(jobs + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        applicationScope.launch {
            appDatabase = AppDatabase(this@MyApp)
        }
    }

}