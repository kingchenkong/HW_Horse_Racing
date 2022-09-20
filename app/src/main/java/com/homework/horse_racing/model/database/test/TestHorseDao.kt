package com.homework.horse_racing.model.database.test

import android.util.Log
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.model.database.dao.HorseDao
import com.homework.horse_racing.model.database.entity.HorseEntity

object TestHorseDao {
    private val TAG: String = TestHorseDao::class.java.simpleName

    suspend fun testHorseDao() {
        val dao: HorseDao = MyApp.appDatabase.getHorseDao()

        Log.d(TAG, "testHorseDao: do insert - - - - - ")
        dao.insert(HorseEntity(0, 1f))
        dao.insert(HorseEntity(0, 2f))
        dao.insert(HorseEntity(0, 3f))
        dao.insert(HorseEntity(0, 4f))
        dao.insert(HorseEntity(0, 5f))

        Log.d(TAG, "testHorseDao: do query - - - - - ")
        val list = dao.getAll()
        val size = list.size
        Log.d(TAG, "testHorseDao: playerList size = $size")
        list.forEach {
            Log.d(TAG, "testHorseDao: $it")
        }
        Log.d(TAG, "testHorseDao: first player: ${list.first()}")
        Log.d(TAG, "testHorseDao: last player: ${list.last()}")

        Log.d(TAG, "testHorseDao: do update - - - - - ")
        list.first().odds += 0.1f
        dao.update(list.first())
        list.last().odds += 0.2f
        dao.update(list.last())
        val listAfterUpdate = dao.getAll()
        listAfterUpdate.forEach {
            Log.d(TAG, "testHorseDao: $it")
        }

        Log.d(TAG, "testHorseDao: do delete - - - - - ")
        dao.delete(list.last())

        val listAfterDelete = dao.getAll()
        listAfterDelete.forEach {
            Log.d(TAG, "testHorseDao: $it")
        }
    }

}