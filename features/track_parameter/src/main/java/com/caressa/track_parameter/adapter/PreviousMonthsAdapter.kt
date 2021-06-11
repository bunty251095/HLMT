package com.caressa.track_parameter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.model.parameter.MonthYear
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemHistoryMonthBinding

class PreviousMonthsAdapter(private val yearMonthList : ArrayList<MonthYear>, val listener:OnMonthClickListener)
    : RecyclerView.Adapter<PreviousMonthsAdapter.PreviousMonthsViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = yearMonthList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousMonthsViewHolder =
        PreviousMonthsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_history_month, parent, false))

    override fun onBindViewHolder(holder : PreviousMonthsViewHolder, position: Int) {
        val yearMonth = yearMonthList[position]
        holder.bindTo( yearMonth )

        if (selectedPos == position) {
            holder.txtMonth.setTextColor(appColorHelper.primaryColor())
            holder.viewSelectedMonth.visibility = View.VISIBLE
        } else {
            holder.txtMonth.setTextColor(appColorHelper.textColor)
            holder.viewSelectedMonth.visibility = View.GONE
        }

        if ( position == yearMonthList.size-1 ) {
            holder.view.visibility = View.GONE
        } else {
            holder.view.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            listener.onMonthSelection(yearMonth, position)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }
    }

    interface OnMonthClickListener {
        fun onMonthSelection( yearMonth:MonthYear, newMonthPosition: Int)
    }

    inner class PreviousMonthsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemHistoryMonthBinding.bind(view)
        val view = binding.view
        val viewSelectedMonth = binding.viewSelectedMonth
        val txtMonth = binding.txtMonth

        fun bindTo(monthYear : MonthYear ) {
            binding.monthYear = monthYear
        }

    }
}