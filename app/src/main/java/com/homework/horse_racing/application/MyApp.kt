package com.homework.horse_racing.application

import androidx.multidex.MultiDexApplication
import com.homework.horse_racing.model.database.AppDatabase
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
            initDatabase()
        }
    }

    private fun initDatabase() {
        appDatabase = AppDatabase(this)
    }

}