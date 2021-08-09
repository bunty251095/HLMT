package com.caressa.medication_tracker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.adapter.MedTimeStatusAdapter
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.medication.AddInTakeModel
import com.caressa.model.medication.MedicineListByDayModel
import kotlinx.android.synthetic.main.dialog_update_status.*
import timber.log.Timber

class DialogUpdateMedicineStatus(@NonNull context: Context?, medicineDetails: MedicineDetails,
                                 listener: OnUpdateClickListener,viewModel: MedicineTrackerViewModel
                                 ,fragment:MedicineDashboardFragment) : Dialog(context!!) {

    private var medicineDetails = MedicineDetails()
    private var onUpdateClickListener: OnUpdateClickListener
    private var viewModel: MedicineTrackerViewModel
    private var fragment:MedicineDashboardFragment
    private var medTimeStatusAdapter: MedTimeStatusAdapter? = null
    private var animation : LayoutAnimationController? = null

    init {
        this.medicineDetails = medicineDetails
        this.onUpdateClickListener = listener
        this.viewModel = viewModel
        this.fragment = fragment
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_update_status)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("SetTextI18n")
    override fun show() {
        super.show()
        try {
            animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)

            println("DialogSelectedDate=>" + medicineDetails.selectedDate)
            txt_medicine_name.text = medicineDetails.medName
            txt_dose.text = medicineDetails.medDose + " " + context.resources.getString(R.string.DOSE)
            txt_med_time.text = medicineDetails.medInstruction
            if (medicineDetails.setAlert.equals(Constants.TRUE, ignoreCase = true)) {
                img_alert.setImageResource(R.drawable.img_alert_on)
            } else {
                img_alert.setImageResource(R.drawable.img_alert_off)
            }

            medTimeStatusAdapter = MedTimeStatusAdapter(context,medicineDetails.medTimeStatusList,medicineDetails.selectedDate)
            rv_med_time_status.layoutManager = LinearLayoutManager(context)
            rv_med_time_status.layoutAnimation = animation
            rv_med_time_status.adapter = medTimeStatusAdapter

            img_close.setOnClickListener {
                onUpdateClickListener.onUpdateClick()
            }

            btn_update.setOnClickListener {
                val medScheduleList : MutableList<MedicineListByDayModel.MedicationSchedule> = mutableListOf()
                var futureTimeCount = 0
                for ( data in medTimeStatusAdapter!!.medTimeStatusList) {
                    if (!Utilities.isNullOrEmpty(data.status) &&
                        !data.status.equals("N",ignoreCase = true)) {
                        medScheduleList.add(data)
                    }
                    if (data.isFuture) {
                        futureTimeCount++
                    }
                }
                Timber.e("medScheduleList--->${medScheduleList.size}")
                when {
                    medScheduleList.size > 0 -> {
                        dismiss()
                        onUpdateClickListener.onUpdateClick()

                        val medicationInTakeList : MutableList<AddInTakeModel.MedicationInTake> = mutableListOf()
                        for ( item in medScheduleList ) {
                            val intake = AddInTakeModel.MedicationInTake()
                            intake.medicationID = item.medicationID
                            if ( !Utilities.isNullOrEmpty(item.medicationInTakeID.toString()) ) {
                                intake.medicationInTakeID = item.medicationInTakeID
                            } else {
                                intake.medicationInTakeID = 0
                            }
                            intake.scheduleID = item.scheduleId
                            intake.status = item.status
                            intake.medDate = medicineDetails.selectedDate
                            intake.medTime = item.scheduleTime
                            intake.dosage = item.dosage.toString()
                            intake.createdDate = medicineDetails.selectedDate
                            medicationInTakeList.add(intake)
                        }
                        viewModel.callAddMedicineIntakeApi(medicationInTakeList,fragment)
                    }
                    medTimeStatusAdapter!!.medTimeStatusList.size == futureTimeCount -> {
                        Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_STATUS_FOR_FUTURE_SCHEDULE_TIMES_CAN_NOT_BE_UPDATED))
                    }
                    else -> {
                        Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_SELECT_MEDICINE_STATUS))
                    }
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    class MedicineDetails {
        var selectedDate = ""
        var medName = ""
        var medDose = ""
        var medInstruction = ""
        var setAlert = "true"
        var medTimeStatusList: MutableList<MedicineListByDayModel.MedicationSchedule> = mutableListOf()
    }

    fun updateMedicineDetails(status:String,medicationInTakeID:Int) {
        println("UpdateIntake=>$status , $medicationInTakeID")
        medTimeStatusAdapter!!.updateTimeStatusList(status,medicationInTakeID)
    }

    interface OnUpdateClickListener {
        fun onUpdateClick()
    }

}