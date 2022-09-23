package com.homework.horse_racing.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.homework.horse_racing.R
import com.homework.horse_racing.model.database.entity.HistoryEntity

class HistoryListAdapter(
    private val context: Context,
    var historyList: MutableList<HistoryEntity>,
    private val itemOnCLickCallback: ItemOnClickCallback?
) : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {
    interface ItemOnClickCallback {
        fun onClick(pos: Int, entity: HistoryEntity, holder: ViewHolder)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvId: TextView = view.findViewById(R.id.tv_id)
        var tvVisible: TextView = view.findViewById(R.id.tv_visible)
        var tvBetAmount: TextView = view.findViewById(R.id.tv_bet_amount)
        var tvFocusNumber: TextView = view.findViewById(R.id.tv_focus_Number)
        var tvWinNumber: TextView = view.findViewById(R.id.tv_win_Number)
        var tvThisRoundAward: TextView = view.findViewById(R.id.tv_this_round_award)
        var tvPlayerRemount: TextView = view.findViewById(R.id.tv_player_remount)
        var btnDelete: Button = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entity = historyList[position]

        holder.apply {
            tvId.text = "id: ${entity.id}"
            tvVisible.text = "visible: ${entity.visible}"
            tvBetAmount.text = "下注金額: ${entity.betAmount}"
            tvFocusNumber.text = "投注號碼: ${entity.betHorseNumber}"
            tvWinNumber.text = "當期冠軍: ${entity.winHorseNumber}"
            tvThisRoundAward.text = "本期獎金:\n ${entity.thisRoundAward}"
            tvPlayerRemount.text = "賭金餘額:\n ${entity.playerAmountRemain}"

            // onClick
            btnDelete.setOnClickListener {
                itemOnCLickCallback?.onClick(position, entity, holder)
            }
        }

    }

    override fun getItemCount(): Int {
        return historyList.count()
    }


}