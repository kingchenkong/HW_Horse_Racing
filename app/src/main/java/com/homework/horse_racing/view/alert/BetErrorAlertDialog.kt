package com.homework.horse_racing.view.alert

import android.content.Context
import androidx.appcompat.app.AlertDialog

object BetErrorAlertDialog {

    enum class ErrorType {
        REMAIN_AMOUNT_NOT_ENOUGH,
        BET_AMOUNT_NOT_ENOUGH,
        DIFF_OF_AMOUNT_INVALID,
        FOCUS_NUMBER_INVALID,
    }

    fun showError(context: Context, errorType: ErrorType) {
        val dialog = AlertDialog.Builder(context)
        val msg: String = when (errorType) {
            ErrorType.REMAIN_AMOUNT_NOT_ENOUGH -> "喔ㄟ! 你沒錢啦!!"
            ErrorType.BET_AMOUNT_NOT_ENOUGH -> "賭金啦! 先下注好嗎?"
            ErrorType.DIFF_OF_AMOUNT_INVALID -> "肖欸heo!? 謀吉咧拉!! 沒he咧咖噌賣岬he咧瀉藥啊拉 = - ="
            ErrorType.FOCUS_NUMBER_INVALID -> "馬啦! 你的馬咧?"
        }
        dialog.setMessage(msg)
        dialog.setTitle("下注失敗")
        dialog.setPositiveButton("我知道了", null)
        dialog.setCancelable(false) // disable touch outside to dismiss
        dialog.show()
    }
}