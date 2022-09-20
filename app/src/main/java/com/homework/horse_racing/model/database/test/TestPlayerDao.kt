package com.homework.horse_racing.model.database.test

import android.util.Log
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.model.database.dao.PlayerDao
import com.homework.horse_racing.model.database.entity.PlayerEntity

object TestPlayerDao {
    private val TAG: String = TestPlayerDao::class.java.simpleName

    suspend fun testPlayDao() {
        val dao: PlayerDao = MyApp.appDatabase.getPlayerDao()

        Log.d(TAG, "testPlayDao: do insert - - - - - ")
        dao.insert(PlayerEntity(0, 1))
        dao.insert(PlayerEntity(0, 10))
        dao.insert(PlayerEntity(0, 100))
        dao.insert(PlayerEntity(0, 1000))
        dao.insert(PlayerEntity(0, 10000))

        Log.d(TAG, "testPlayDao: do query - - - - - ")
        val list = dao.getAll()
        val size = list.size
        Log.d(TAG, "testPlayDao: playerList size = $size")
        list.forEach {
            Log.d(TAG, "testPlayDao: $it")
        }
        Log.d(TAG, "testPlayDao: first player: ${list.first()}")
        Log.d(TAG, "testPlayDao: last player: ${list.last()}")

        Log.d(TAG, "testPlayDao: do update - - - - - ")
        list.first().amount += 100
        dao.update(list.first())
        list.last().amount += 200
        dao.update(list.last())
        val listAfterUpdate = dao.getAll()
        listAfterUpdate.forEach {
            Log.d(TAG, "testPlayDao: $it")
        }

        Log.d(TAG, "testPlayDao: do delete - - - - - ")
        dao.delete(list.last())

        val listAfterDelete = dao.getAll()
        listAfterDelete.forEach {
            Log.d(TAG, "testPlayDao: $it")
        }
    }
}