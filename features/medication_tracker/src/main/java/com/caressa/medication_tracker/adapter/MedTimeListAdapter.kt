package com.caressa.medication_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.model.medication.MedicineListByDayModel.MedicationSchedule
import kotlinx.android.synthetic.main.item_med_schedule_time.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MedTimeListAdapter(val context: Context, private var medTimeList : List<MedicationSchedule>) :
    RecyclerView.Adapter<MedTimeListAdapter.MedTimeListViewHolder>() {

    override fun getItemCount(): Int = medTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeListViewHolder =
        MedTimeListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_med_schedule_time,parent,false))

    override fun onBindViewHolder(holder: MedTimeListViewHolder, position: Int) {
        val medTime = medTimeList[position]
        holder.txtMedTime.text = getFormattedValue(medTime.scheduleTime)
        if (!Utilities.isNullOrEmpty(medTime.status)) {
            when {
                medTime.status.equals(Constants.TAKEN,ignoreCase = true) -> {
                    holder.imgStatus.setImageResource(R.drawable.img_taken)
                    ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_nasty_green)))
                }
                medTime.status.equals(Constants.SKIPPED,ignoreCase = true) -> {
                    holder.imgStatus.setImageResource(R.drawable.img_skipped)
                    ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_watermelon)))
                }
                else -> {
                    holder.imgStatus.visibility = View.GONE
                }
            }
        } else {
            holder.imgStatus.visibility = View.GONE
        }
    }

    private fun getFormattedValue(time: String): String {
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        var sdf24HourDt: Date? = null
        try {
            sdf24HourDt = sdf24Hour.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return sdf12Hour.format(sdf24HourDt!!)
    }

    inner class MedTimeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMedTime = view.txt_med_time!!
        val imgStatus = view.img_status!!
    }

}