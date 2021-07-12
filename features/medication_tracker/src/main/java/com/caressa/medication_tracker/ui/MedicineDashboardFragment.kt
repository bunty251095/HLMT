package com.caressa.medication_tracker.ui

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.*
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.adapter.DashboardMedicinesAdapter
import com.caressa.medication_tracker.adapter.MedCalenderAdapter
import com.caressa.medication_tracker.common.MedicationSingleton
import com.caressa.medication_tracker.databinding.FragmentMedicineDashboardBinding
import com.caressa.medication_tracker.model.MedCalender
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.medication.MedicineListByDayModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MedicineDashboardFragment : BaseFragment(),MedCalenderAdapter.OnDateClickListener,
    DialogUpdateMedicineStatus.OnUpdateClickListener,DefaultNotificationDialog.OnDialogValueListener {

    val viewModel: MedicineTrackerViewModel by viewModel()
    private lateinit var binding: FragmentMedicineDashboardBinding

    //private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var dialogClickType = "Alert"
    private var selectedDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var cal = Calendar.getInstance()
    private var previousPosition = 7
    private var medCalenderAdapter : MedCalenderAdapter? = null
    private var dashboardMedicinesAdapter: DashboardMedicinesAdapter? = null
    var defaultNotificationDialog: DialogUpdateMedicineStatus? = null
    private var calenderDateList : MutableList<MedCalender> = mutableListOf()
    private var medicineDetailsList:MutableList<MedicineListByDayModel.Medication> = mutableListOf()
    private var medicine = MedicineListByDayModel.Medication()
    private var animation : LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            selectedDate = it.getString(Constants.DATE,"")!!
            Timber.e("from,selectedDate--->$from,$selectedDate")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMedicineDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        try {
            animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)
            setCalender(selectedDate)
            if (NetworkUtility.isOnline(context)) {
                startShimmer()
                if ( Utilities.isNullOrEmpty(selectedDate) ) {
                    selectedDate = DateHelper.currentDateAsStringyyyyMMdd
                }
                viewModel.callMedicineListByDayApi(selectedDate,this)
            } else {
                Utilities.toastMessageShort(context, resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
            }

            dashboardMedicinesAdapter = DashboardMedicinesAdapter(viewModel,requireContext(), this, selectedDate)
            binding.rvMedicineDetails.layoutManager = LinearLayoutManager(context)
            //rvMedDetails.setLayoutAnimation(animation);
            binding.rvMedicineDetails.adapter = dashboardMedicinesAdapter

            // When Opened from Clicking on Reminder Notification
            if( from.equals(Constants.NOTIFICATION_ACTION,ignoreCase = true) ) {
                val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                //val scheduleId = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
                viewModel.getMedicationInTakeByScheduleID(notificationData,this)
                val dialogData = DialogUpdateMedicineStatus.MedicineDetails()
                dialogData.selectedDate = notificationData.getStringExtra(Constants.DATE)!!
                dialogData.medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)!!
                dialogData.medDose = notificationData.getStringExtra(Constants.DOSAGE)!!
                dialogData.medInstruction = notificationData.getStringExtra(Constants.INSTRUCTION)!!
                dialogData.setAlert = Constants.TRUE

                val medicationScheduleList: MutableList<MedicineListByDayModel.MedicationSchedule> = mutableListOf()
                val medTime = MedicineListByDayModel.MedicationSchedule()
                medTime.medicationID = notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toInt()
                medTime.scheduleId = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toInt()
                medTime.medicationInTakeID = notificationData.getIntExtra(Constants.MEDICINE_IN_TAKE_ID,0)
                medTime.scheduleTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!
                medTime.dosage = notificationData.getStringExtra(Constants.DOSAGE)!!.toDouble()
                medTime.createdDate = notificationData.getStringExtra(Constants.DATE)!!

                medicationScheduleList.add(medTime)
                dialogData.medTimeStatusList = medicationScheduleList
                showUpdateStatusDialog(dialogData)
            }

            viewModel.medicineListByDay.observe( viewLifecycleOwner , {})
            viewModel.setAlert.observe( viewLifecycleOwner , {})
            viewModel.listMedicationInTake.observe(viewLifecycleOwner, {  })
            viewModel.addMedicineIntake.observe(viewLifecycleOwner, {})
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.btnDashUploadPre.setOnClickListener {
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.STORE_RECORDS)
            launchIntent.putExtra(Constants.FROM, Constants.MEDICATION)
            startActivity(launchIntent)
        }

        binding.layoutAddMedication.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,Constants.DASHBOARD)
            bundle.putString(Constants.DATE,selectedDate)
            it.findNavController().navigate(R.id.action_medicineDashboardFragment_to_addMedicineFragment,bundle)
        }

        binding.btnDashMyMedications.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,Constants.DASHBOARD)
            bundle.putString(Constants.DATE,selectedDate)
            it.findNavController().navigate(R.id.action_medicineDashboardFragment_to_myMedicationsFragment,bundle)
        }

    }

    private fun setCalender( medDate : String ) {

        for (i in 7 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val date = MedCalender()
            date.Year = getFormattedValue("yyyy", cal.time)
            date.Month = getFormattedValue("MMM", cal.time)
            date.MonthOfYear = getFormattedValue("MM", cal.time)
            date.DayOfWeek = getFormattedValue("EEE", cal.time)
            date.DayOfMonth = getFormattedValue("dd", cal.time)
            date.Date = getFormattedValue("yyyy-MM-dd", cal.time)
            // i=0 -> true, else false
            date.IsToday = i == 0

            calenderDateList.add(date)
        }

        for (j in 1..7) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, +j)
            val date = MedCalender()
            date.Year = getFormattedValue("yyyy", cal.time)
            date.Month = getFormattedValue("MMM", cal.time)
            date.MonthOfYear = getFormattedValue("MM", cal.time)
            date.DayOfWeek = getFormattedValue("EEE", cal.time)
            date.DayOfMonth = getFormattedValue("dd", cal.time)
            date.Date = getFormattedValue("yyyy-MM-dd", cal.time)
            date.IsToday = false
            calenderDateList.add(date)
        }

        var selectedPos: Int = calenderDateList.size / 2
        for (k in calenderDateList.indices) {
            if (medDate.equals(calenderDateList[k].Date, ignoreCase = true)) {
                selectedPos = k
                break
            }
        }
        println("SelectedPosition=>$selectedPos")
        medCalenderAdapter = MedCalenderAdapter(viewModel,requireContext(), selectedPos, this)
        medCalenderAdapter!!.updateList(calenderDateList)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        //layoutManager.scrollToPositionWithOffset(4,0);
        binding.rvMedCalender.layoutManager = layoutManager
        binding.rvMedCalender.layoutManager!!.scrollToPosition(4)
        binding.rvMedCalender.adapter = medCalenderAdapter

    }

    fun updateMedicatinesList() {
        medicineDetailsList = MedicationSingleton.getInstance()!!.geMedicineListByDay()
        if (medicineDetailsList.size > 0) {
            dashboardMedicinesAdapter!!.updateMedicines(medicineDetailsList,selectedDate)
            binding.rvMedicineDetails.scheduleLayoutAnimation()
            binding.rvMedicineDetails.visibility = View.VISIBLE
            binding.layoutNoMedicines.visibility = View.GONE
        } else {
            binding.rvMedicineDetails.visibility = View.GONE
            binding.layoutNoMedicines.visibility = View.VISIBLE
        }
    }

    private fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    private fun startShimmer() {
        binding.layoutMedicineDetailsShimmer.startShimmer()
        binding.layoutMedicineDetailsShimmer.visibility = View.VISIBLE
    }

    fun stopShimmer() {
        binding.layoutMedicineDetailsShimmer.stopShimmer()
        binding.layoutMedicineDetailsShimmer.visibility = View.GONE
    }

    override fun onDateSelection(date: MedCalender, newDayPosition: Int) {
        if (previousPosition != newDayPosition) {
            previousPosition = newDayPosition
            selectedDate = date.Date
            Timber.e("Position,SelectedDate----->$previousPosition---$selectedDate")
            medicineDetailsList.clear()
            binding.layoutNoMedicines.visibility = View.GONE
            dashboardMedicinesAdapter!!.updateMedicines(medicineDetailsList, selectedDate)
            if (NetworkUtility.isOnline(context)) {
                startShimmer()
                viewModel.callMedicineListByDayApi(selectedDate,this)
            } else {
                Utilities.toastMessageShort(context, resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
            }
        }
    }

    fun showUpdateStatusDialog(dialogData: DialogUpdateMedicineStatus.MedicineDetails?) {
        defaultNotificationDialog = DialogUpdateMedicineStatus(context, dialogData!!, this,viewModel,this)
        defaultNotificationDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog!!.show()
    }

    override fun onUpdateClick() {
        defaultNotificationDialog!!.dismiss()
        defaultNotificationDialog!!.cancel()
    }

    fun showUpdateMedicineAlertDialog( medicineDetails : MedicineListByDayModel.Medication ) {
        dialogClickType = "Alert"
        medicine = medicineDetails
        //val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        val medName = if ( !Utilities.isNullOrEmpty(medicineDetails.drug.strength) ) {
            medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        } else {
            medicineDetails.drug.name
        }

        val toDo: String = if (medicine.notification!!.setAlert!!) {
            " " + resources.getString(R.string.ENABLE) + " "
        } else {
            " " + resources.getString(R.string.DISABLE) + " "
        }
        showDialog(
            listener = this,
            title = toDo + resources.getString(R.string.NOTIFICATION_ALERT),
            message = resources.getString(R.string.DO_YOU_WANT_TO) + toDo
                    + resources.getString(R.string.NOTIFICATION_ALERT) + " "
                    + resources.getString(R.string.FOR) + " " + medName,
            leftText = resources.getString(R.string.CANCEL),
            rightText = resources.getString(R.string.CONFIRM),
            showLeftBtn = true)
    }

    fun showRescheduleDialog( medicineDetails : MedicineListByDayModel.Medication ) {
        dialogClickType = "Reschedule"
        medicine = medicineDetails
        Utilities.printData("MedicineDetails",medicine)
        val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        showDialog(
            listener = this,
            title = resources.getString(R.string.RESCHEDULE_MEDICINE),
            message = resources.getString(R.string.MSG_CONFIRMATION_RESCHEDULE_MEDICINE) + " " + medName +"?" ,
            leftText = resources.getString(R.string.CANCEL),
            rightText = resources.getString(R.string.CONFIRM),
            showLeftBtn = true)
    }

    fun showUpdateScheduleListDialog( medicineDetails : MedicineListByDayModel.Medication ) {
        dialogClickType = "Update"
        medicine = medicineDetails
        Utilities.printData("MedicineDetails",medicine)
        showDialog(
            listener = this,
            title = resources.getString(R.string.UPDATE_MEDICINE_SCHEDULES),
            message = resources.getString(R.string.MSG_SCHEDULE_TIME_FOUND) ,
            leftText = resources.getString(R.string.CANCEL),
            rightText = resources.getString(R.string.PROCEED),
            showLeftBtn = true)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if ( isButtonRight ) {
            try {
                when(dialogClickType) {

                    "Alert" -> {
                        viewModel.callSetAlertApi(medicine,this)
                    }

                    "Reschedule","Update" -> {
                        val bundle = Bundle()
                        var medicineName = medicine.drug.name
                        bundle.putString(Constants.FROM,"Dashboard")
                        bundle.putInt(Constants.MEDICATION_ID,medicine.medicationId)
                        bundle.putString(Constants.DATE,selectedDate)
                        if ( !Utilities.isNullOrEmpty(medicine.drug.strength) ) {
                            medicineName = medicineName +" - "+ medicine.drug.strength
                        }
                        bundle.putString(Constants.MEDICINE_NAME,medicineName)
                        if ( !Utilities.isNullOrEmpty(medicine.drugTypeCode) ) {
                            bundle.putString(Constants.DRUG_TYPE_CODE,medicine.drugTypeCode)
                        } else {
                            bundle.putString(Constants.DRUG_TYPE_CODE,"TAB")
                        }
                        bundle.putInt(Constants.Drug_ID,medicine.drugID)
                        findNavController().navigate(R.id.action_medicineDashboardFragment_to_scheduleMedicineFragment,bundle)
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun performBackClick() {
        MedicationSingleton.getInstance()!!.geMedicineListByDay().clear()
        findNavController().navigate(R.id.action_medicineDashboardFragment_to_medicineHome)
    }

}