package com.caressa.fitness_tracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.fitness_tracker.R
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.fitness_tracker.databinding.ItemWeekdayBinding
import com.caressa.fitness_tracker.util.WeekDay

class WeekdayAdapter(mListener: OnWeekDayClickListener) : RecyclerView.Adapter<WeekdayAdapter.WeekdayViewHolder>() {

    private var selectedPos = 0
    private var listener: OnWeekDayClickListener = mListener
    private var weekDayList: MutableList<WeekDay> = mutableListOf()
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = weekDayList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekdayViewHolder =
        WeekdayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_weekday, parent, false))

    override fun onBindViewHolder(holder: WeekdayAdapter.WeekdayViewHolder, position: Int) {

        val weekday = weekDayList[position]
        holder.bindTo( weekday )

        if (selectedPos == position) {
            listener.onWeekDaySelection(position,weekday)
            holder.txtWeekDate.setTextColor(appColorHelper.primaryColor())
            holder.view.visibility = View.VISIBLE
        } else {
            holder.txtWeekDate.setTextColor(appColorHelper.textColor)
            holder.view.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            //listener.onWeekDaySelection(position,weekday)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }

    }

    fun updateList(list : MutableList<WeekDay>) {
        this.weekDayList.clear()
        this.weekDayList.addAll(list)
        this.selectedPos = list.size-1
        this.notifyDataSetChanged()
    }

    fun updateWeekDayPosition( pos :Int ) {
        this.selectedPos = pos
        this.notifyDataSetChanged()
    }

    interface OnWeekDayClickListener {
        fun onWeekDaySelection( position: Int , weekday : WeekDay )
    }

    inner class WeekdayViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemWeekdayBinding.bind(view)
        //val txtWeekDay = binding.txtWeekDay
        val txtWeekDate = binding.txtWeekDate
        val view = binding.view

        fun bindTo( weekday: WeekDay ) {
            binding.weekDay = weekday
        }

    }

}