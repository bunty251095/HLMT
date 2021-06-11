package com.caressa.medication_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.ItemMedTimeBinding
import com.caressa.medication_tracker.model.TimeModel
import com.caressa.medication_tracker.ui.ScheduleDetailsFragment

class MedScheduleTimeAdapter(val context: Context, var fragment : ScheduleDetailsFragment)
    : RecyclerView.Adapter<MedScheduleTimeAdapter.MedScheduleTimeViewHolder>() {

    var medScheduleTimeList: MutableList<TimeModel> = mutableListOf()

    override fun getItemCount(): Int = medScheduleTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedScheduleTimeViewHolder =
        MedScheduleTimeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_med_time, parent, false))

    override fun onBindViewHolder(holder: MedScheduleTimeViewHolder, position: Int) {
        val time = medScheduleTimeList[position]
        holder.bindTo( time )

        if (!Utilities.isNullOrEmpty(time.displayTime)) {
            holder.txtMedScheduleTime.text = time.displayTime
        } else {
            holder.txtMedScheduleTime.text = "--:--"
        }

        if (itemCount > 1) {
            holder.imgRemove.visibility = View.VISIBLE
        } else {
            holder.imgRemove.visibility = View.GONE
        }

        holder.layoutMedTime.setOnClickListener {
            fragment.showTimePicker(position, time.hour, time.minute)
        }

        holder.imgRemove.setOnClickListener {
            if (medScheduleTimeList.size > 1) {
                fragment.removeAndAddToRemovedScheduleList(position, time, true)
            }
        }
    }

    fun addMedTime(position: Int, timeModel: TimeModel?) {
        println("Added_position=>$position")
        medScheduleTimeList.add(position, timeModel!!)
        notifyItemInserted(position)
        if (position == 1) {
            this.notifyItemChanged(0)
        }
        //this.notifyDataSetChanged();
    }

    fun updateTime(position: Int, timeModel: TimeModel) {
        println("Update_position=>$position")
        medScheduleTimeList[position].displayTime = timeModel.displayTime
        medScheduleTimeList[position].time = timeModel.time
        medScheduleTimeList[position].hour = timeModel.hour
        medScheduleTimeList[position].minute = timeModel.minute
        this.notifyItemChanged(position)
    }

    fun removeMedTime(position: Int) {
        println("Removed_position=>$position")
        medScheduleTimeList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    class MedScheduleTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMedTimeBinding.bind(view)
        val layoutMedTime = binding.layoutMedTime
        val txtMedScheduleTime = binding.txtMedScheduleTime
        val imgRemove = binding.imgRemove

        fun bindTo(time : TimeModel ) {
            binding.time = time
        }
    }
}