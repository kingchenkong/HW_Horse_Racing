package com.homework.horse_racing.view.alert

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.homework.horse_racing.model.bean.ResultState
import com.homework.horse_racing.model.manager.BetHorseManager

object ResultAlertDialog {

    fun showResult(context: Context, resultState: ResultState) {
        val dialog = AlertDialog.Builder(context)
        val msg: String = when (resultState) {
            ResultState.WIN -> {
                "投注: ${BetHorseManager.focusHorseNumberLiveData.value?.horseName} \n" +
                        "贏了! TWD$ ${BetHorseManager.twdAwardLiveData.value}"
            }
            ResultState.LOSE -> {
                "投注: ${BetHorseManager.focusHorseNumberLiveData.value?.horseName}\n" +
                        "輸了..."
            }
        }
        dialog.setMessage(msg)
        dialog.setTitle("下注結果")
        dialog.setPositiveButton("我知道了", null)
        dialog.setCancelable(false) // disable touch outside to dismiss
        dialog.show()
    }
}