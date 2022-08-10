package com.caressa.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.ui.DialogUpdateMedicineStatus
import com.caressa.medication_tracker.ui.MedicineDashboardFragment
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.medication.MedicineListByDayModel.Medication
import kotlinx.android.synthetic.main.item_med_dashboard.view.*
import kotlinx.android.synthetic.main.item_medicine_calender.view.view

class DashboardMedicinesAdapter(val viewModel : MedicineTrackerViewModel, val context: Context,
                                val fragment: MedicineDashboardFragment, private var selectedDate: String) :
    RecyclerView.Adapter<DashboardMedicinesAdapter.DashboardMedicinesViewHolder>() {

    private val medicationTrackerHelper = viewModel.medicationTrackerHelper
    private var medicinesList: MutableList<Medication> = mutableListOf()
    private var currentDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var medTimeListAdapter: MedTimeListAdapter? = null
    private var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green)
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medicinesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardMedicinesViewHolder =
        DashboardMedicinesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_med_dashboard, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardMedicinesViewHolder, position: Int) {
        try {
            val medicine = medicinesList[position]
            holder.view.setBackgroundColor(ContextCompat.getColor(context, getRandomColor(position)))
            if (!Utilities.isNullOrEmpty(medicine.drugTypeCode)) {
                holder.imgMedType.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(medicine.drugTypeCode))
                if (medicine.drugTypeCode.equals("MOUTHWASH",ignoreCase = true)) {
                    holder.txtMedType.text = context.resources.getString(R.string.MOUTH_WASH)
                } else {
                    holder.txtMedType.text = context.resources.getString(medicationTrackerHelper.getMedTypeByCode(medicine.drugTypeCode))
                }
            }

            if ( !Utilities.isNullOrEmpty( medicine.drug.strength ) ) {
                holder.txtMedicineName.text = medicine.drug.name + " - " +medicine.drug.strength
            } else {
                holder.txtMedicineName.text = medicine.drug.name
            }

            if (medicine.medicationScheduleList.isNotEmpty()) {
                holder.txtDose.text = medicine.medicationScheduleList[0].dosage.toString() + " " + context.resources.getString(R.string.DOSE)
            }
            holder.txtMedInstruction.text = medicationTrackerHelper.getMedInstructionByCode(medicine.comments)

            var medDateDuration: String = DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.prescribedDate)
            if (!Utilities.isNullOrEmpty(medicine.endDate.toString())) {
                medDateDuration = context.resources.getString(R.string.FROM) + " " + medDateDuration +
                        " " + context.resources.getString(R.string.TO) + " " + DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.endDate.toString())

            } else {
                if (DateHelper.getDateDifference(medicine.prescribedDate, currentDate) < 0) {
                    medDateDuration = context.resources.getString(R.string.STARTING_ON) + "  $medDateDuration"
                } else {
                    medDateDuration = context.resources.getString(R.string.STARTED) + "  $medDateDuration"
                }
            }

            val dateDiff: Long = DateHelper.getDateDifference(medicine.endDate.toString(), currentDate)
            if (!Utilities.isNullOrEmpty(medicine.endDate.toString()) && dateDiff > 0) {
                holder.imgAlert.visibility = View.GONE
                holder.imgCompleted.visibility = View.VISIBLE
            } else {
                holder.imgAlert.visibility = View.VISIBLE
                holder.imgCompleted.visibility = View.GONE
            }

            if (currentDate.equals(medicine.prescribedDate, ignoreCase = true)
                && currentDate.equals(medicine.endDate.toString(), ignoreCase = true)) {
                holder.txtMedDateDuration.text = context.resources.getString(R.string.FOR_TODAY_ONLY)
            } else if (medicine.prescribedDate.equals(medicine.endDate.toString(),ignoreCase = true)) {
                holder.txtMedDateDuration.text = context.resources.getString(R.string.FOR) + "  " + DateHelper.getDateTimeAs_ddMMMyyyyNew(
                    medicine.prescribedDate) + "  " + context.resources.getString(R.string.ONLY)
            } else {
                holder.txtMedDateDuration.text = medDateDuration
            }

            if (medicine.notification!!.setAlert!!) {
                holder.imgAlert.setImageResource(R.drawable.img_alert_on)
                holder.imgAlert.tag = R.drawable.img_alert_on
            } else {
                holder.imgAlert.setImageResource(R.drawable.img_alert_off)
                holder.imgAlert.tag = R.drawable.img_alert_off
            }

            medTimeListAdapter = MedTimeListAdapter(context, medicine.medicationScheduleList)
            holder.rvMedScheduleTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.rvMedScheduleTime.adapter = medTimeListAdapter

            holder.imgAlert.setOnClickListener {
                if( getDrawableId(holder.imgAlert) == R.drawable.img_alert_on ) {
                    medicine.notification!!.setAlert = false
                }
                if( getDrawableId(holder.imgAlert) == R.drawable.img_alert_off ) {
                    medicine.notification!!.setAlert = true
                }
                fragment.showUpdateMedicineAlertDialog(medicine)
            }

            holder.imgCompleted.setOnClickListener {
                fragment.showRescheduleDialog(medicine)
            }

            holder.imgEditMed.setOnClickListener {
                if ( !medicine.medicationScheduleList.isNullOrEmpty() ) {
                    val dialogData = DialogUpdateMedicineStatus.MedicineDetails()
                    dialogData.selectedDate = selectedDate
                    dialogData.medName = medicine.drug.name
                    dialogData.medDose = medicine.medicationScheduleList[0].dosage.toString()
                    dialogData.medInstruction = medicine.comments
                    if (medicine.notification!!.setAlert!!) {
                        dialogData.setAlert = "true"
                    } else {
                        dialogData.setAlert = "false"
                    }
                    dialogData.medTimeStatusList.clear()
                    dialogData.medTimeStatusList = medicine.medicationScheduleList.toMutableList()
                    fragment.showUpdateStatusDialog(dialogData)
                } else {
                    fragment.showUpdateScheduleListDialog(medicine)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateMedicines(list : MutableList<Medication>,selectedDate : String) {
        this.selectedDate = selectedDate
        medicinesList.clear()
        medicinesList.addAll(list)
        notifyDataSetChanged()
    }

    private fun getRandomColor(position: Int): Int {
        return color[position % 6]
    }

    private fun getDrawableId(iv: ImageView): Int {
        return iv.tag as Int
    }

    inner class DashboardMedicinesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view.view!!
        val imgMedType = view.img_med_type!!
        val imgEditMed = view.img_edit_med!!
        val imgAlert = view.img_alert!!
        val imgCompleted = view.img_completed!!
        val txtMedType = view.txt_med_type!!
        val txtMedicineName = view.txt_medicine_name!!
        val txtDose = view.txt_dose!!
        val txtMedInstruction = view.txt_med_time!!
        val txtMedDateDuration = view.txt_med_date_duration!!
        val rvMedScheduleTime = view.rv_med_schedule_time!!
    }
}