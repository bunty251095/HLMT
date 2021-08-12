package com.caressa.medication_tracker.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.adapter.MealTimeAdapter
import com.caressa.medication_tracker.adapter.MedFreuencyAdapter
import com.caressa.medication_tracker.adapter.MedScheduleTimeAdapter
import com.caressa.medication_tracker.common.MedicationTrackerHelper
import com.caressa.medication_tracker.databinding.FragmentScheduleDetailsBinding
import com.caressa.medication_tracker.model.InstructionModel
import com.caressa.medication_tracker.model.TimeModel
import com.caressa.medication_tracker.view.CounterView
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.medication.MedicationModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDetailsFragment : BaseFragment(), CounterView.OnCounterSubmitListener,
    MedFreuencyAdapter.OnMedFrequencyListener, MealTimeAdapter.OnInstructionClickListener,
    TimePickerDialog.OnTimeSetListener, KoinComponent {

    private val viewModel: MedicineTrackerViewModel by viewModel()
    private lateinit var binding: FragmentScheduleDetailsBinding

    private val appColorHelper = AppColorHelper.instance!!

    private val medicationTrackerHelper : MedicationTrackerHelper = get()
    private var medFrequencyAdapter: MedFreuencyAdapter? = null
    private var medScheduleTimeAdapter: MedScheduleTimeAdapter? = null
    private var mealTimeAdapter: MealTimeAdapter? = null

    private var from = ""
    private var selectedDate = ""
    private var medicationId = 0
    private var drugId = 0
    private var medicineName = ""
    private var drugTypeCode = ""
    private var drugType = ""
    private var medFreqPos = 0
    private var frequency = ""
    private var serverStartDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var serverEndDate = ""
    private var medDoseCount = 1.0
    private var intakeInstruction = ""
    private var hour = 0
    private var minute = 0
    private var scheduleTimePosition = 0
    private val calendar = Calendar.getInstance()
    private var calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private var calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    private var scheduleList : MutableList<TimeModel> = mutableListOf()
    private var removedScheduleList : MutableList<TimeModel> = mutableListOf()
    private val instructionList: ArrayList<InstructionModel> = medicationTrackerHelper.getMedInstructionList()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,Constants.ADD)
            medicationId = it.getInt(Constants.MEDICATION_ID,0)
            drugId = it.getInt(Constants.Drug_ID,0)
            medicineName = it.getString(Constants.MEDICINE_NAME,"")!!
            drugTypeCode = it.getString(Constants.DRUG_TYPE_CODE,"")!!
            selectedDate = it.getString(Constants.DATE,"")!!
            Timber.e("from,MedicationID,DrugID,MedicineName,DrugTypeCode,SelectedDate--->$from,$medicationId,$drugId,$medicineName,$drugTypeCode,$selectedDate")
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
        binding = FragmentScheduleDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initialise()
            setData()
            setClickable()
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        binding.counterMedDose.setAsNonDecimal(false)
        binding.counterMedDose.setValue("1")
        binding.counterMedDose.setUnit("Dose")
        binding.counterMedDose.setInputRange(0.5, 6.0)
        binding.counterMedDose.setValueToAddSubctract(0.5)
        binding.counterMedDose.setOnCounterSubmitListener(this)

        binding.counterMedTime.setAsNonDecimal(true)
        binding.counterMedTime.setValue("1")
        binding.counterMedTime.setUnit("Time")
        binding.counterMedTime.setInputRange(1.0, 6.0)
        binding.counterMedTime.setValueToAddSubctract(1.0)
        binding.counterMedTime.setOnCounterSubmitListener(this)

        binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverStartDate))
        calendarStart = setDayMonthYearToCalender(serverStartDate,calendarStart)

        medFrequencyAdapter = MedFreuencyAdapter(requireContext(), medicationTrackerHelper.getFrequencyList(), this)
        binding.rvMedFrequency.layoutManager = GridLayoutManager(context, 2)
        binding.rvMedFrequency.adapter = medFrequencyAdapter

        medScheduleTimeAdapter = MedScheduleTimeAdapter(requireContext(), this)
        binding.rvMedTime.layoutManager = GridLayoutManager(context, 3)
        binding.rvMedTime.adapter = medScheduleTimeAdapter

        mealTimeAdapter = MealTimeAdapter(requireContext(), instructionList, this)
        binding.rvMealTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMealTime.adapter = mealTimeAdapter
    }

    @SuppressLint("SetTextI18n")
    fun setData() {
        if ( Utilities.isNullOrEmptyOrZero(medicationId.toString()) ) {
            // Medicine Details
            drugType = "  (" + resources.getString(medicationTrackerHelper.getMedTypeByCode(drugTypeCode)) + ")"
            println("drugTypeCode--->$drugTypeCode")
            binding.txtMedName.text = medicineName + drugType
            binding.imgMedicineType.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))
            medScheduleTimeAdapter!!.addMedTime(0, TimeModel(0, "", "", 0, 0))
        } else {
            viewModel.getMedicineDetailsByMedicationId(medicationId)
            viewModel.medicineDetails.observe( viewLifecycleOwner , {
                if ( it != null ) {
                    val medicineDetails = it
                    Utilities.printData("MedicineDetails",medicineDetails)
                    drugTypeCode = if ( Utilities.isNullOrEmpty(medicineDetails.DrugTypeCode) ) {
                        "TAB"
                    } else {
                        medicineDetails.DrugTypeCode!!
                    }
                    drugType = "  (" + resources.getString(medicationTrackerHelper.getMedTypeByCode(drugTypeCode)) + ")"
                    medicineName = medicineDetails.drug.name!!
                    if ( !Utilities.isNullOrEmpty(medicineDetails.drug.strength) ) {
                        medicineName = medicineName + " - " + medicineDetails.drug.strength
                    }
                    binding.txtMedName.text = medicineName + drugType
                    binding.imgMedicineType.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))

                    // Medication Period
                    if (medicineDetails.medicationPeriod.equals("For X Days", ignoreCase = true)) {
                        binding.layoutXDays.visibility = View.VISIBLE
                        binding.edtDuration.setText(medicineDetails.durationInDays)
                        medFrequencyAdapter!!.updateSelectedPos(1)
                    } else {
                        binding.layoutXDays.visibility = View.GONE
                        medFrequencyAdapter!!.updateSelectedPos(0)
                    }

                    // Star Date and End Date
                    serverStartDate = medicineDetails.PrescribedDate!!
                    //selectionDateStart = DateHelper.getDate(serverStartDate,DateHelper.SHORT_DATE_FORMAT)
                    binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverStartDate))
                    if (!Utilities.isNullOrEmpty(medicineDetails.EndDate)) {
                        serverEndDate = medicineDetails.EndDate!!
                        binding.edtEndDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverEndDate))
                    }

                    // Dosage
                    if ( !medicineDetails.scheduleList.isNullOrEmpty() ) {
                        binding.counterMedDose.setValue(medicineDetails.scheduleList[0].dosage)
                    } else {
                        binding.counterMedDose.setValue("1.0")
                    }

                    // Schedule Time List
                    if ( !medicineDetails.scheduleList.isNullOrEmpty() ) {
                        binding.counterMedTime.setValue(medicineDetails.scheduleList.size.toString())
                        for (i in medicineDetails.scheduleList.indices) {
                            val medSchedule = medicineDetails.scheduleList[i]
                            val scheduleId = medSchedule.scheduleID
                            val scheduleTime = medSchedule.scheduleTime
                            val split = scheduleTime!!.split(":").toTypedArray()
                            val hours = split[0].toInt()
                            val minutes = split[1].toInt()
                            println(i.toString() + ":" + scheduleTime + "=>" + +hours + " : " + minutes)
                            medScheduleTimeAdapter!!.addMedTime(i,TimeModel(scheduleId,getDisplayTime(scheduleTime),scheduleTime,hours,minutes))
                        }
                    } else {
                        Utilities.toastMessageLong(context,resources.getString(R.string.ERROR_SCHEDULE_TIME_NOT_FOUND_PLEASE_SELECT_TO_UPDATE_MEDICINE_DETAILS))
                        binding.counterMedTime.setValue("1")
                        medScheduleTimeAdapter!!.addMedTime(0, TimeModel(0, "", "", 0, 0))
                    }

                    // Intake Instructions
                    for (i in instructionList.indices) {
                        if (instructionList[i].title.equals(medicineDetails.comments,ignoreCase = true)) {
                            mealTimeAdapter!!.updateSelectedPos(i)
                            break
                        }
                    }

                    // Notes
                    if (!Utilities.isNullOrEmpty(medicineDetails.notes)) {
                        binding.edtNotes.setText(medicineDetails.notes)
                    }

                    // Alert Notification
                    binding.swAlert.isChecked = medicineDetails.notification!!.setAlert!!
                }
            })
        }
    }

    private fun setClickable() {

        binding.edtDuration.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence,i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (!Utilities.isNullOrEmptyOrZero(editable.toString())) {
                        val endDate: String = DateHelper.getDateBeforeOrAfterGivenDays(serverStartDate, editable.toString().toInt())
                        serverEndDate = endDate
                        binding.edtEndDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverEndDate))
                    } else {
                        binding.edtEndDate.setText("")
                        serverEndDate = ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.cardStartDate.setOnClickListener {
            showStartDatePicker()
        }

        binding.cardEndDate.setOnClickListener {
            showEndDatePicker()
        }

        binding.edtStartDate.setOnClickListener {
            showStartDatePicker()
        }

        binding.edtEndDate.setOnClickListener {
            showEndDatePicker()
        }

        binding.btnSchedule.setOnClickListener {
            if (NetworkUtility.isOnline(context)) {
                //System.out.println("isValid,size=>" + isValidTimeList()+","+scheduleList.size());
                validateAndSchedule()
            } else {
                Utilities.toastMessageShort(context, resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
            }
        }

        viewModel.saveMedicine.observe(viewLifecycleOwner, {})
        viewModel.updateMedicine.observe(viewLifecycleOwner, {})
    }

    private fun showStartDatePicker() {
        try {
            DialogHelper().showDatePickerDialog("Start Date",requireContext(), Calendar.getInstance(),
                Calendar.getInstance(), null, object :
                DialogHelper.DateListener{
                override fun onDateSet(date: String, year: String, month: String, dayOfMonth: String) {
                    val selectedDate = DateHelper.convertDateSourceToDestination(date,DateHelper.DISPLAY_DATE_DDMMMYYYY,DateHelper.SERVER_DATE_YYYYMMDD)
                    Timber.e("SelectedStartDate--->$selectedDate")
                    if ( !Utilities.isNullOrEmpty(selectedDate) ) {
                        if (!Utilities.isNullOrEmpty(serverEndDate)) {
                            val startDate = DateHelper.getDate(selectedDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            val endDate = DateHelper.getDate(serverEndDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            if (startDate == endDate || startDate.before(endDate)) {
                                serverStartDate = selectedDate
                                val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(selectedDate)
                                binding.edtStartDate.setText(date)
                            } else {
                                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_START_DATE_MUST_BE_LESS_THAN_OR_EQUAL_TO_END_DATE))
                            }
                        } else {
                            serverStartDate = selectedDate
                            binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(selectedDate))
                        }

                        if (medFreqPos == 1 && !Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                            val endDate = DateHelper.getDateBeforeOrAfterGivenDays(serverStartDate,binding.edtDuration.text.toString().toInt())
                            serverEndDate = endDate
                            binding.edtEndDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(endDate))
                        }
                    }
                }
            })
            /*val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            materialDatePicker.setTitleText("Start Date")

            val constraintsBuilder = CalendarConstraints.Builder()
            calendarStart = setDayMonthYearToCalender(serverStartDate,calendarStart)
            constraintsBuilder.setStart(calendarStart.timeInMillis)
            materialDatePicker.setSelection(calendarStart.timeInMillis)
            //constraintsBuilder.setValidator(MaterialCalenderValidator(calendarStart.timeInMillis))
            calendarStart = setDayMonthYearToCalenderValidatorPoint(serverStartDate,calendarStart)
            val dateValidator = DateValidatorPointForward.from(calendarStart.timeInMillis)
            constraintsBuilder.setValidator(dateValidator)
            materialDatePicker.setCalendarConstraints(constraintsBuilder.build())
            val picker = materialDatePicker.build()
            picker.show(activity?.supportFragmentManager!!,picker.toString())

            picker.addOnPositiveButtonClickListener {
                Timber.e( "Start_Date----->${picker.headerText}:: Date epoch value = $it")

                val selectedDate = DateHelper.convertMaterialPickerDateToServerdate(picker.headerText)
                Timber.e("SelectedStartDate--->$selectedDate")
                if ( !Utilities.isNullOrEmpty(selectedDate) ) {
                    if (!Utilities.isNullOrEmpty(serverEndDate)) {
                        val startDate = DateHelper.getDate(selectedDate, DateHelper.SERVER_DATE_YYYYMMDD)
                        val endDate = DateHelper.getDate(serverEndDate, DateHelper.SERVER_DATE_YYYYMMDD)
                        if (startDate == endDate || startDate.before(endDate)) {
                            serverStartDate = selectedDate
                            val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(selectedDate)
                            binding.edtStartDate.setText(date)
                        } else {
                            Utilities.toastMessageShort(context, "Start Date must be less than or Equal to End Date")
                        }
                    } else {
                        serverStartDate = selectedDate
                        binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(selectedDate))
                    }

                    if (medFreqPos == 1 && !Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                        val endDate = DateHelper.getDateBeforeOrAfterGivenDays(serverStartDate,binding.edtDuration.text.toString().toInt())
                        serverEndDate = endDate
                        binding.edtEndDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(endDate))
                    }
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEndDatePicker() {
        try {
            DialogHelper().showDatePickerDialog("End Date",requireContext(), Calendar.getInstance(),
                Calendar.getInstance(), null, object :
                    DialogHelper.DateListener {
                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String) {
                        val selectedDate = DateHelper.convertDateSourceToDestination(date,DateHelper.DISPLAY_DATE_DDMMMYYYY,DateHelper.SERVER_DATE_YYYYMMDD)
                        Timber.e("SelectedEndDate--->$selectedDate")
                        if (!Utilities.isNullOrEmpty(selectedDate)) {
                            val startDate =
                                DateHelper.getDate(serverStartDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            val endDate =
                                DateHelper.getDate(selectedDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            if (endDate == startDate || endDate.after(startDate)) {
                                serverEndDate = selectedDate
                                binding.edtDuration.setText((DateHelper.getDateDifference(serverStartDate, selectedDate) + 1).toString())
                            } else {
                                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_END_DATE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_START_DATE))
                            }
                        }
                    }

                })

            /*val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            materialDatePicker.setTitleText("End Date")
            val constraintsBuilder = CalendarConstraints.Builder()
            calendarStart = setDayMonthYearToCalender(serverStartDate,calendarStart)
            constraintsBuilder.setStart(calendarStart.timeInMillis)
            //constraintsBuilder.setValidator(MaterialCalenderValidator(calendarStart.timeInMillis))
            calendarStart = setDayMonthYearToCalenderValidatorPoint(serverStartDate,calendarStart)
            val dateValidator = DateValidatorPointForward.from(calendarStart.timeInMillis)
            constraintsBuilder.setValidator(dateValidator)

            if ( !Utilities.isNullOrEmptyOrZero(serverEndDate) ) {
                calendarEnd = setDayMonthYearToCalender(serverEndDate,calendarEnd)
            }
            materialDatePicker.setSelection(calendarEnd.timeInMillis)
            materialDatePicker.setCalendarConstraints(constraintsBuilder.build())
            val picker = materialDatePicker.build()
            picker.show(activity?.supportFragmentManager!!,picker.toString())

            picker.addOnPositiveButtonClickListener {
                Timber.e( "End_Date----->${picker.headerText}:: Date epoch value = $it")
                val selectedDate = DateHelper.convertMaterialPickerDateToServerdate(picker.headerText)
                Timber.e("SelectedEndDate--->$selectedDate")
                if ( !Utilities.isNullOrEmpty(selectedDate) ) {
                    val startDate = DateHelper.getDate(serverStartDate, DateHelper.SERVER_DATE_YYYYMMDD)
                    val endDate = DateHelper.getDate(selectedDate, DateHelper.SERVER_DATE_YYYYMMDD)
                    if (endDate == startDate || endDate.after(startDate)) {
                        serverEndDate = selectedDate
                        binding.edtDuration.setText((DateHelper.getDateDifference(serverStartDate, selectedDate) + 1).toString())
                    } else {
                        Utilities.toastMessageShort(context, "End Date must be Greater than or Equal to Start Date")
                    }
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateAndSchedule() {
        try {
            if (isValidTimeList()) {
                if (medFreqPos == 1 && Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                    Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_PLEASE_ENTER_VALID_NUMBER_OF_DAYS))
                } else {
                    val medicine = MedicationModel.Medication()
                    medicine.drug.drugId = drugId
                    medicine.drug.name = medicineName
                    medicine.drug.drugTypeCode = drugTypeCode
                    medicine.medicationID = medicationId
                    medicine.drugID = drugId
                    medicine.drugTypeCode = drugTypeCode
                    medicine.prescribedDate = serverStartDate
                    medicine.endDate = serverEndDate
                    medicine.medicationPeriod = frequency
                    if (!Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                        medicine.durationInDays = binding.edtDuration.text.toString()
                    }
                    medicine.comments = intakeInstruction
                    medicine.notes = binding.edtNotes.text.toString()
                    medicine.notification.setAlert = if (binding.swAlert.isChecked)
                        Constants.TRUE.toLowerCase(Locale.ENGLISH)
                    else
                        Constants.FALSE.toLowerCase(Locale.ENGLISH)

                    viewModel.callAddOrUpdateMedicineApi(medicine,getScheduleTimeList(),removedScheduleList,medDoseCount)
                }
            } else {
                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_PLEASE_SELECT_VALID_SCHEDULE_TIME))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minitue: Int) {
        println("hour,minute=>$hour : $minitue")
        val time: String = DateHelper.getFormattedTime("" + hour, "" + minitue)
        medScheduleTimeAdapter!!.updateTime(scheduleTimePosition, TimeModel(0, getDisplayTime(time), time, hour, minitue))
    }

    override fun onCounterSubmit() {
        try {
            medDoseCount = binding.counterMedDose.value
            //medDetailsMap.put(GlobalConstants.DOSAGE, medDoseCount.toString())
            val medScheduleTimeCount = binding.counterMedTime.value
            val medScheduleListSize: Int = medScheduleTimeAdapter!!.medScheduleTimeList.size
            if (medScheduleListSize >= 1 && medScheduleTimeCount > medScheduleListSize) {
                medScheduleTimeAdapter!!.addMedTime(medScheduleListSize, TimeModel(0, "", "", 0, 0))
            } else if (medScheduleTimeCount < medScheduleListSize) {
                val pos = medScheduleListSize - 1
                val time = medScheduleTimeAdapter!!.medScheduleTimeList[pos]
                removeAndAddToRemovedScheduleList(medScheduleListSize - 1, time, false)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onMedFrequencySelection(medFreq: String?, position: Int) {
        println("Position,MedicationPeriod=>$position,$medFreq")
        if (position == 0) {
            medFreqPos = 0
            binding.layoutXDays.visibility = View.GONE
            frequency = "Daily"
            binding.edtDuration.setText("")
        } else {
            medFreqPos = 1
            binding.layoutXDays.visibility = View.VISIBLE
            frequency = medFreq!!
        }
    }

    override fun onInstructionSelection(position: Int, instruction: InstructionModel) {
        intakeInstruction = instruction.title
        println("Instruction=>$intakeInstruction")
    }

    fun showTimePicker(position: Int, h: Int, m: Int) {
        println("$position => $h : $m")
        try {
            scheduleTimePosition = position
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
            val tpd = TimePickerDialog(context, R.style.TimePickerTheme, this, h, m, false)
            //tpd.getButton(1).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            if (h == 0 && m == 0) {
                tpd.updateTime(hour, minute)
            }
            tpd.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun removeAndAddToRemovedScheduleList(position: Int, time: TimeModel, fromAdapter: Boolean) {
        if (time.scheduleId != 0) {
            println("RemovedTime,ScheduleID=>" + time.displayTime + "," + time.scheduleId)
            removedScheduleList.add(time)
        }
        medScheduleTimeAdapter!!.removeMedTime(position)
        if (fromAdapter) {
            binding.counterMedTime.setValue(java.lang.String.valueOf(medScheduleTimeAdapter!!.medScheduleTimeList.size))
        }
    }

    private fun isValidTimeList(): Boolean {
        var isValid = false
        scheduleList.clear()
        var timeModel: TimeModel
        for (i in 0 until medScheduleTimeAdapter!!.medScheduleTimeList.size) {
            timeModel = medScheduleTimeAdapter!!.medScheduleTimeList[i]
            isValid = if (!Utilities.isNullOrEmpty(timeModel.time)
                && !containsTimeModel(scheduleList, timeModel.time)) {
                scheduleList.add(timeModel)
                true
            } else {
                false
            }
        }
        return isValid
    }

    private fun getDisplayTime(time: String): String {
        val displayTime: String
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        var dateObj: Date? = null
        try {
            dateObj = sdf.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println(time)
        displayTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(dateObj!!)
        println(displayTime)
        return displayTime
    }

    private fun containsTimeModel(list: List<TimeModel>, time: String): Boolean {
        for (`object` in list) {
            if (`object`.time.equals(time,ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun getScheduleTimeList(): List<TimeModel> {
        return scheduleList
    }

    private fun setDayMonthYearToCalender( server_date_YYYYMMDD : String,cal:Calendar ) : Calendar {
        val dob = server_date_YYYYMMDD.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val years = Integer.parseInt(dob[0])
        val months = Integer.parseInt(dob[1])
        val days = Integer.parseInt(dob[2])

        //val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = years
        cal[Calendar.MONTH] = months-1
        cal[Calendar.DAY_OF_MONTH] = days
        return cal
    }

    private fun setDayMonthYearToCalenderValidatorPoint( server_date_YYYYMMDD : String,cal:Calendar ) : Calendar {
        val dob = server_date_YYYYMMDD.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val years = Integer.parseInt(dob[0])
        val months = Integer.parseInt(dob[1])
        val days = Integer.parseInt(dob[2])

        //val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = years
        cal[Calendar.MONTH] = months-1
        cal[Calendar.DAY_OF_MONTH] = days-1
        return cal
    }

    fun performBackClick() {
        val bundle = Bundle()
        when {
            from.equals(Constants.ADD,ignoreCase = true) -> {
                bundle.putInt(Constants.MEDICATION_ID,medicationId)
                bundle.putInt(Constants.Drug_ID,drugId)
                bundle.putString(Constants.MEDICINE_NAME,medicineName)
                bundle.putString(Constants.DRUG_TYPE_CODE,drugTypeCode)
                findNavController().navigate(R.id.action_scheduleMedicineFragment_to_addMedicineFragment,bundle)
            }
            from.equals("Dashboard",ignoreCase = true) -> {
                bundle.putString(Constants.DATE,selectedDate)
                findNavController().navigate(R.id.action_scheduleMedicineFragment_to_medicineDashboardFragment,bundle)
            }
            else -> {
                bundle.putString(Constants.FROM,from)
                findNavController().navigate(R.id.action_scheduleMedicineFragment_to_myMedicationsFragment,bundle)
            }
        }
    }

}