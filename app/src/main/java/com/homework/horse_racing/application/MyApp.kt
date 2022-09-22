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
    }

    private val jobs = SupervisorJob()
    private val applicationScope: CoroutineScope = CoroutineScope(jobs + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            Log.d(TAG, "onCreate: ")
            initDatabase()

        }
    }

    private suspend fun initDatabase() {
        appDatabase = AppDatabase(this)

        val playerDao = appDatabase.getPlayerDao()
        val playerList = playerDao.getAll()
        if (playerList.isEmpty()) {
            playerDao.insert(PlayerEntity(0, BetHorseManager.INIT_REMAIN))
        }
        val horseDao = appDatabase.getHorseDao()
        val horseList = horseDao.getAll()
        if (horseList.isEmpty()) {
            val initHorseList: List<HorseEntity> = listOf(
                HorseEntity(0, BetHorseManager.INIT_ODDS, HorseNumber.NUM_1.horseName),
                HorseEntity(0, BetHorseManager.INIT_ODDS, HorseNumber.NUM_2.horseName),
                HorseEntity(0, BetHorseManager.INIT_ODDS, HorseNumber.NUM_3.horseName),
                HorseEntity(0, BetHorseManager.INIT_ODDS, HorseNumber.NUM_4.horseName),
            )
            initHorseList.forEach { horseDao.insert(it) }
        }
    }

}