package com.caressa.medication_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.model.MedStatusModel
import com.caressa.model.medication.MedicineListByDayModel.MedicationSchedule
import kotlinx.android.synthetic.main.item_time_status.view.*
import java.text.SimpleDateFormat
import java.util.*

class MedTimeStatusAdapter(val context: Context, var medTimeStatusList : MutableList<MedicationSchedule>, private var selectedDate: String) :
    RecyclerView.Adapter<MedTimeStatusAdapter.MedTimeStatusViewHolder>(),
    MedStatusAdapter.OnStatusChangeListener, MedStatusAdapter.OnFutureListener{

    private var medStatusAdapter: MedStatusAdapter? = null

    override fun getItemCount(): Int = medTimeStatusList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeStatusViewHolder =
        MedTimeStatusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_time_status, parent, false))

    override fun onBindViewHolder(holder: MedTimeStatusViewHolder, position: Int) {
        try {
            val medicine = medTimeStatusList[position]

            holder.txtMedTime.text = getFormattedValue(medicine.scheduleTime)
            var selectedPos = -1
            if (!Utilities.isNullOrEmpty(medicine.status)) {
                if (medicine.status.equals(Constants.TAKEN,ignoreCase = true)) {
                    selectedPos = 0
                } else if (medicine.status.equals(Constants.SKIPPED,ignoreCase = true)) {
                    selectedPos = 1
                }
                medStatusAdapter = MedStatusAdapter(context, position, getStatusList(), selectedPos, medicine.scheduleTime, selectedDate, this, this)
            } else {
                medStatusAdapter = MedStatusAdapter(context, position, getStatusList(), selectedPos, medicine.scheduleTime, selectedDate, this, this)
            }
            holder.rvMedTimeStatus.layoutManager = GridLayoutManager(context, 2)
            holder.rvMedTimeStatus.itemAnimator = null
            holder.rvMedTimeStatus.adapter = medStatusAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFormattedValue(date: String): String {
        var str12HrTime = ""
        try {
            val str24HourSDF = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val str12HourSDF = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val str24HourDt: Date?
            str24HourDt = str24HourSDF.parse(date)
            str12HrTime = str12HourSDF.format(str24HourDt!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return str12HrTime
    }

    private fun getStatusList(): ArrayList<MedStatusModel> {
        val statusList: ArrayList<MedStatusModel> = ArrayList()
        statusList.add(MedStatusModel(context.resources.getString(R.string.TAKEN),R.drawable.img_taken,R.color.vivant_nasty_green))
        statusList.add(MedStatusModel(context.resources.getString(R.string.SKIPPED),R.drawable.img_skipped,R.color.vivant_watermelon))
        return statusList
    }

    fun updateTimeStatusList( status:String,medicationInTakeID:Int ) {
        medTimeStatusList[0].status = status
        medTimeStatusList[0].medicationInTakeID = medicationInTakeID
        if (!Utilities.isNullOrEmpty(status)) {
            if (status.equals(Constants.TAKEN, ignoreCase = true)) {
                medStatusAdapter!!.updateSelectedPosStatus(0)
            } else if (status.equals(Constants.SKIPPED, ignoreCase = true)) {
                medStatusAdapter!!.updateSelectedPosStatus(1)
            }
        }
        this.notifyItemChanged(0)
    }

    override fun onStatusChange(position: Int, status: String) {
        medTimeStatusList[position].status = status
    }

    override fun onFutureChange(position: Int, isFuture: Boolean) {
        medTimeStatusList[position].isFuture = isFuture
    }

    inner class MedTimeStatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMedTime = view.txt_med_time!!
        val rvMedTimeStatus = view.rv_med_time_status!!
    }

}