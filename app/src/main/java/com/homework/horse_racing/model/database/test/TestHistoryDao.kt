package com.homework.horse_racing.model.database.test

import android.util.Log
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.model.database.dao.HistoryDao
import com.homework.horse_racing.model.database.entity.HistoryEntity

object TestHistoryDao {
    private val TAG: String = TestHistoryDao::class.java.simpleName

    suspend fun testHistoryDao() {
        val dao: HistoryDao = MyApp.appDatabase.getHistoryDao()

        Log.d(TAG, "testHistoryDao: do insert - - - - - ")
        dao.insert(HistoryEntity(0, 0, 0, 0, 0, 0, true))
        dao.insert(HistoryEntity(0, 1, 1, 1, 1, 1, true))
        dao.insert(HistoryEntity(0, 2, 2, 2, 2, 2, true))
        dao.insert(HistoryEntity(0, 3, 3, 3, 3, 3, true))
        dao.insert(HistoryEntity(0, 4, 4, 4, 4, 4, true))

        Log.d(TAG, "testHistoryDao: do query - - - - - ")
        val list = dao.getAll()
        val size = list.size
        Log.d(TAG, "testHistoryDao: playerList size = $size")
        list.forEach {
            Log.d(TAG, "testHistoryDao: $it")
        }
        Log.d(TAG, "testHistoryDao: first player: ${list.first()}")
        Log.d(TAG, "testHistoryDao: last player: ${list.last()}")

        Log.d(TAG, "testHistoryDao: do update - - - - - ")
        list.first().betAmount += 100
        list.first().visible = false
        dao.update(list.first())
        list.last().betAmount += 400
        list.last().visible = false
        dao.update(list.last())
        val listAfterUpdate = dao.getAll()
        listAfterUpdate.forEach {
            Log.d(TAG, "testHistoryDao: $it")
        }

        Log.d(TAG, "testHistoryDao: do delete - - - - - ")
        dao.delete(list.last())

        val listAfterDelete = dao.getAll()
        listAfterDelete.forEach {
            Log.d(TAG, "testHistoryDao: $it")
        }
    }

}