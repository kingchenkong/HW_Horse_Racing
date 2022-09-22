package com.homework.horse_racing.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.homework.horse_racing.databinding.ActivityHistoryBinding
import com.homework.horse_racing.databinding.ActivityMainBinding
import com.homework.horse_racing.view_model.ui.HistoryActivityViewModel

class HistoryActivity : AppCompatActivity() {
    companion object {
        val TAG: String = HistoryActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityHistoryBinding

    private lateinit var uiVm: HistoryActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiVm = ViewModelProvider(this)[HistoryActivityViewModel::class.java]

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.uiVm = uiVm


    }




}