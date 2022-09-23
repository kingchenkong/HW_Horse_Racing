package com.homework.horse_racing.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.homework.horse_racing.application.MyApp
import com.homework.horse_racing.databinding.ActivityHistoryBinding
import com.homework.horse_racing.model.adapter.HistoryListAdapter
import com.homework.horse_racing.model.database.entity.HistoryEntity
import com.homework.horse_racing.view_model.ui.HistoryActivityViewModel
import kotlin.math.log

class HistoryActivity : AppCompatActivity() {
    companion object {
        val TAG: String = HistoryActivity::class.java.simpleName
    }

    private lateinit var uiVm: HistoryActivityViewModel
    private lateinit var binding: ActivityHistoryBinding

    private lateinit var adapter: HistoryListAdapter

    private val deleteCallback: HistoryListAdapter.ItemOnClickCallback =
        object : HistoryListAdapter.ItemOnClickCallback {
            override fun onClick(
                pos: Int,
                entity: HistoryEntity,
                holder: HistoryListAdapter.ViewHolder
            ) {
                Log.d(TAG, "[delete] onClick: pos: $pos, entity: $entity")
                lifecycleScope.launchWhenStarted {
                    val historyDao = MyApp.appDatabase.getHistoryDao()
                    entity.visible = false
                    historyDao.update(entity)
                    adapter.historyList.removeAt(pos)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiVm = ViewModelProvider(this)[HistoryActivityViewModel::class.java]

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.uiVm = uiVm

        initRecyclerView()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        lifecycleScope.launchWhenStarted {
            val historyDao = MyApp.appDatabase.getHistoryDao()
            val entityList: List<HistoryEntity> = historyDao.getAll()
            val adapterList: MutableList<HistoryEntity> = mutableListOf()
            entityList.forEach {
                if (it.visible) {
                    adapterList.add(it)
                }
            }

            adapter = HistoryListAdapter(this@HistoryActivity, adapterList, deleteCallback)
            val linearLayoutManager =
                LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
            binding.rvList.layoutManager = linearLayoutManager
            binding.rvList.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }


}