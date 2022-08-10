package com.caressa.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.ItemMyMedicationBinding
import com.caressa.medication_tracker.ui.MyMedicationsFragment
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.entity.MedicationEntity
import timber.log.Timber

class MyMedicationsAdapter(val viewModel : MedicineTrackerViewModel, val context: Context, val fragment: MyMedicationsFragment ) :
    RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {

    private val medicationTrackerHelper = viewModel.medicationTrackerHelper
    private var spinnerPosition = 0
    private var medicinesList: MutableList<MedicationEntity.Medication> = mutableListOf()
    private var currentDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green)
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medicinesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMedicationsViewHolder =
        MyMedicationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_medication, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyMedicationsAdapter.MyMedicationsViewHolder, position: Int) {
        try {
            val medicine = medicinesList[position]
            holder.view.setBackgroundColor(ContextCompat.getColor(context,getRandomColor(position)))
            if (!Utilities.isNullOrEmpty(medicine.DrugTypeCode)) {
                holder.imgMedType.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(medicine.DrugTypeCode!!))
                if (medicine.DrugTypeCode.equals("MOUTHWASH", ignoreCase = true)) {
                    holder.txtMedType.text = context.resources.getString(R.string.MOUTH_WASH)
                } else {
                    holder.txtMedType.text = context.resources.getString(medicationTrackerHelper.getMedTypeByCode(medicine.DrugTypeCode!!))
                }
            }
            val medName = if ( !Utilities.isNullOrEmpty(medicine.drug.strength) ) {
                medicine.drug.name + " - " +medicine.drug.strength
            } else {
                medicine.drug.name
            }

            holder.txtMedicineName.text = medName
            if (medicine.scheduleList.isNotEmpty()) {
                holder.txtDose.text = medicine.scheduleList[0].dosage + " " + context.resources.getString(R.string.DOSE)
            }
            holder.txtMedTime.text = medicationTrackerHelper.getMedInstructionByCode(medicine.comments!!)

            var medDateDuration: String = DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.PrescribedDate)
            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                medDateDuration = context.resources.getString(R.string.FROM) + " " + medDateDuration +
                        " " + context.resources.getString(R.string.TO) + " " + DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.EndDate)
            } else {
                if (DateHelper.getDateDifference(medicine.PrescribedDate!!, currentDate) < 0) {
                    medDateDuration = context.resources.getString(R.string.STARTING_ON) + "  $medDateDuration"
                } else {
                    medDateDuration = context.resources.getString(R.string.STARTED) + "  $medDateDuration"
                }
            }

            if ( !Utilities.isNullOrEmpty(medicine.EndDate) ) {
                val dateDiff = DateHelper.getDateDifference(medicine.EndDate!!, currentDate)
                if ( dateDiff > 0) {
                    holder.imgEditMed.visibility = View.GONE
                    holder.imgCompleted.visibility = View.VISIBLE
                } else {
                    holder.imgEditMed.visibility = View.VISIBLE
                    holder.imgCompleted.visibility = View.GONE
                }
            } else {
                holder.imgEditMed.visibility = View.VISIBLE
                holder.imgCompleted.visibility = View.GONE
            }

            if (currentDate.equals(medicine.PrescribedDate, ignoreCase = true)
                && currentDate.equals(medicine.EndDate, ignoreCase = true)) {
                //holder.lblStarted.setText("For ");
                holder.txtStartDate.text = context.resources.getString(R.string.FOR_TODAY_ONLY)
            } else if (medicine.PrescribedDate.equals(medicine.EndDate, ignoreCase = true)) {
                holder.txtStartDate.text = context.resources.getString(R.string.FOR) + "  " + DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.PrescribedDate) + "  " + context.resources.getString(R.string.ONLY)
            } else {
                holder.txtStartDate.text = medDateDuration
            }

            holder.imgEditMed.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(Constants.FROM,getFrom(spinnerPosition))
                bundle.putInt(Constants.MEDICATION_ID,medicine.medicationId)
                bundle.putInt(Constants.Drug_ID,medicine.drugID)
                bundle.putString(Constants.MEDICINE_NAME,medName)
                if ( !Utilities.isNullOrEmpty(medicine.DrugTypeCode) ) {
                    bundle.putString(Constants.DRUG_TYPE_CODE,medicine.DrugTypeCode)
                }
                it.findNavController().navigate(R.id.action_myMedicationsFragment_to_scheduleMedicineFragment,bundle)
            }

            holder.imgCompleted.setOnClickListener {
                fragment.showRescheduleDialog(medicine)
            }

            holder.imgDeleteMed.setOnClickListener {
                fragment.showDeleteMedicineDialog(medicine)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateMedicines(list : MutableList<MedicationEntity.Medication>, position : Int) {
        this.spinnerPosition = position
        Timber.i("Position=>$position")
        medicinesList.clear()
        medicinesList.addAll(list)
        notifyDataSetChanged()
    }

    private fun getRandomColor(position: Int): Int {
        return color[position % 6]
    }

    private fun getFrom(position: Int): String {
        val from: String = when (position) {
            0 -> {
                "History"
            }
            1 -> {
                "Reschedule"
            }
            else -> {
                "All"
            }
        }
        println("from=>$from")
        return from
    }

    inner class MyMedicationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMyMedicationBinding.bind(view)

        val view = binding.view
        val imgMedType = binding.imgMedType
        val txtMedType = binding.txtMedType
        val txtMedicineName = binding.txtMedicineName
        val txtDose = binding.txtDose
        val txtMedTime = binding.txtMedTime
        val txtStartDate = binding.txtStartDate
        val imgEditMed = binding.imgEditMed
        val imgCompleted = binding.imgCompleted
        val imgDeleteMed = binding.imgDeleteMed
    }

}