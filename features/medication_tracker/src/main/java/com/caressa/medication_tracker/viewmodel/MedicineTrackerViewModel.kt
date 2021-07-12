package com.caressa.medication_tracker.viewmodel

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.*
import com.caressa.medication_tracker.MedNotificationApiService
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.common.MedicationSingleton
import com.caressa.medication_tracker.common.MedicationTrackerHelper
import com.caressa.medication_tracker.domain.MedicationManagementUseCase
import com.caressa.medication_tracker.model.ReminderNotification
import com.caressa.medication_tracker.model.TimeModel
import com.caressa.medication_tracker.ui.MedicineDashboardFragment
import com.caressa.medication_tracker.ui.MyMedicationsFragment
import com.caressa.medication_tracker.ui.ScheduleDetailsFragmentDirections
import com.caressa.model.entity.MedicationEntity
import com.caressa.model.entity.UserRelatives
import com.caressa.model.medication.*
import com.caressa.model.tempconst.Configuration
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
class MedicineTrackerViewModel(
    private val dispatchers: AppDispatchers,
    private val sharedPref: SharedPreferences,
    private val useCase: MedicationManagementUseCase,
    val medicationTrackerHelper: MedicationTrackerHelper,
     private val context: Context): BaseViewModel() {

    private var medDashboardFragment: MedicineDashboardFragment? = null

    val personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!
    private val authToken = sharedPref.getString(PreferenceConstants.TOKEN, "")!!
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val medicinesList = MutableLiveData<List<MedicationEntity.Medication>>()
    val medicineDetails = MutableLiveData<MedicationEntity.Medication>()

    private var drugsSource: LiveData<Resource<DrugsModel.DrugsResponse>> = MutableLiveData()
    private val _drugsList = MediatorLiveData<DrugsModel.DrugsResponse>()
    val drugsList: LiveData<DrugsModel.DrugsResponse> get() = _drugsList

    private var medicineListDataSource: LiveData<Resource<MedicationListModel.Response>> = MutableLiveData()
    private val _medicineList = MediatorLiveData<MedicationListModel.Response>()
    val medicineList: LiveData<MedicationListModel.Response> get() = _medicineList

    private var medicineListByDaySource: LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> = MutableLiveData()
    private val _medicineListByDay = MediatorLiveData<MedicineListByDayModel.MedicineListByDayResponse>()
    val medicineListByDay: LiveData<MedicineListByDayModel.MedicineListByDayResponse> get() = _medicineListByDay

    private var saveMedicineSource: LiveData<Resource<AddMedicineModel.AddMedicineResponse>> = MutableLiveData()
    private val _saveMedicine = MediatorLiveData<AddMedicineModel.AddMedicineResponse>()
    val saveMedicine: LiveData<AddMedicineModel.AddMedicineResponse> get() = _saveMedicine

    private var updateMedicineSource: LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>> = MutableLiveData()
    private val _updateMedicine = MediatorLiveData<UpdateMedicineModel.UpdateMedicineResponse>()
    val updateMedicine: LiveData<UpdateMedicineModel.UpdateMedicineResponse> get() = _updateMedicine

    private var deleteMedicineSource: LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>> = MutableLiveData()
    private val _deleteMedicine = MediatorLiveData<DeleteMedicineModel.DeleteMedicineResponse>()
    val deleteMedicine: LiveData<DeleteMedicineModel.DeleteMedicineResponse> get() = _deleteMedicine

    private var setAlertSource: LiveData<Resource<SetAlertModel.SetAlertResponse>> = MutableLiveData()
    private val _setAlert = MediatorLiveData<SetAlertModel.SetAlertResponse>()
    val setAlert: LiveData<SetAlertModel.SetAlertResponse> get() = _setAlert

/*    private var getMedicineSource: LiveData<Resource<GetMedicineModel.GetMedicineResponse>> = MutableLiveData()
    private val _getMedicine = MediatorLiveData<GetMedicineModel.GetMedicineResponse>()
    val getMedicine: LiveData<GetMedicineModel.GetMedicineResponse> get() = _getMedicine*/

    private var listMedicationInTakeSource: LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>> = MutableLiveData()
    private val _listMedicationInTake = MediatorLiveData<MedicineInTakeModel.MedicineDetailsResponse>()
    val listMedicationInTake: LiveData<MedicineInTakeModel.MedicineDetailsResponse> get() = _listMedicationInTake

    private var addMedicineIntakeSource: LiveData<Resource<AddInTakeModel.AddInTakeResponse>> = MutableLiveData()
    private val _addMedicineIntake = MediatorLiveData<AddInTakeModel.AddInTakeResponse>()
    val addMedicineIntake: LiveData<AddInTakeModel.AddInTakeResponse> get() = _addMedicineIntake

    fun fetchDrugsList(str: String) = viewModelScope.launch(dispatchers.main) {
        if (str.length > 2) {
            val requestData = DrugsModel(Gson().toJson(DrugsModel.JSONDataRequest(
                name = str,
                OrAndFlag = 1,
                message = "Getting List.."), DrugsModel.JSONDataRequest::class.java), authToken)

            _drugsList.removeSource(drugsSource)
            withContext(dispatchers.io) {
                drugsSource = useCase.invokeDrugsList(requestData)
            }
            _drugsList.addSource(drugsSource) {
                _drugsList.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }
    }

    fun fetchMedicationList() = viewModelScope.launch(dispatchers.main) {

        val requestData = MedicationListModel(Gson().toJson(MedicationListModel.JSONDataRequest(
            personId = personId,
            toDate = DateHelper.currentDateAsStringyyyyMMdd),MedicationListModel.JSONDataRequest::class.java), authToken)

        //_progressBar.value = Event("Loading...")
        _medicineList.removeSource(medicineListDataSource)
        withContext(dispatchers.io) {
            medicineListDataSource = useCase.fetchMedicationList(requestData, personId)
        }
        _medicineList.addSource(medicineListDataSource) {
            _medicineList.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callMedicineListByDayApi(medicationDate: String, fragment: MedicineDashboardFragment) = viewModelScope.launch(dispatchers.main) {

            medDashboardFragment = fragment
            val requestData = MedicineListByDayModel(Gson().toJson(MedicineListByDayModel.JSONDataRequest(
                personID = personId,
                medicationDate = medicationDate), MedicineListByDayModel.JSONDataRequest::class.java), authToken)

            //_progressBar.value = Event("Getting Medicines...")
            _medicineListByDay.removeSource(medicineListByDaySource)
            withContext(dispatchers.io) {
                medicineListByDaySource = useCase.invokeGetMedicationListByDay(requestData)
            }
            _medicineListByDay.addSource(medicineListByDaySource) {
                _medicineListByDay.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    try {
                        if ( it.data!!.medications.isNotEmpty() ) {
                            val medicineListByDay = it.data!!.medications.toMutableList()
                            Timber.e("medicineListByDay---> ${medicineListByDay.size}")
                            medicineListByDay.sortByDescending { med -> med.notification!!.setAlert }
                            MedicationSingleton.getInstance()!!.setMedicineListByDay(medicineListByDay)
                        }
                        fragment.stopShimmer()
                        fragment.updateMedicatinesList()
                    } catch ( e : Exception ) {
                        e.printStackTrace()
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callAddOrUpdateMedicineApi(medicine: MedicationModel.Medication, scheduleList: List<TimeModel>,
        removedScheduleList: List<TimeModel>, dosage: Double) {
        medicine.personID = personId
        var timeModel: TimeModel
        val medicationScheduleList: MutableList<MedicationModel.MedicationSchedule> = mutableListOf()
        for (i in scheduleList.indices) {
            val medSchedule = MedicationModel.MedicationSchedule()
            timeModel = scheduleList[i]
            medSchedule.medicationID = medicine.medicationID.toString()
            medSchedule.scheduleId = timeModel.scheduleId
            medSchedule.scheduleTime = timeModel.time
            medSchedule.dosage = dosage.toString()
            medicationScheduleList.add(medSchedule)
        }
        medicine.medicationScheduleList = medicationScheduleList
        //***********************************
        if (removedScheduleList.isNotEmpty()) {
            //deleteRemovedSchedules(medicineId, removedScheduleList)
        }
        //***********************************
        if (medicine.medicationID == 0) {
            Timber.e("from--->Add")
            callSaveMedicineApi(medicine)
        } else {
            Timber.e("from--->Update")
            callUpdateMedicineApi(medicine)
        }
    }

    private fun callSaveMedicineApi(medicine: MedicationModel.Medication) = viewModelScope.launch(dispatchers.main) {

           val requestData = AddMedicineModel(Gson().toJson(AddMedicineModel.JSONDataRequest(
               medication = medicine), AddMedicineModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Adding Medicine...")
        _saveMedicine.removeSource(saveMedicineSource)
        withContext(dispatchers.io) {
            saveMedicineSource = useCase.invokeSaveMedicine(requestData)
        }
        _saveMedicine.addSource(saveMedicineSource) {
            _saveMedicine.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.medication.medicationId != 0) {
                    toastMessage(context.resources.getString(R.string.MEDICINE_ADDED))
                    navigate(ScheduleDetailsFragmentDirections.actionScheduleMedicineFragmentToMedicineHome("", ""))
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.MEDICINE_UPLOAD_EVENT)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun callUpdateMedicineApi(medicine: MedicationModel.Medication) = viewModelScope.launch(dispatchers.main) {

        val requestData = UpdateMedicineModel(Gson().toJson(UpdateMedicineModel.JSONDataRequest(
            medication = medicine), UpdateMedicineModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Updating Medicine...")
        _updateMedicine.removeSource(updateMedicineSource)
        withContext(dispatchers.io) {
            updateMedicineSource = useCase.invokeUpdateMedicine(requestData)
        }
        _updateMedicine.addSource(updateMedicineSource) {
            _updateMedicine.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.medication.medicationId != 0) {
                    toastMessage(context.resources.getString(R.string.MEDICINE_UPDATED))
                    navigate(ScheduleDetailsFragmentDirections.actionScheduleMedicineFragmentToMedicineHome("", ""))
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callDeleteMedicineApi(spinnerPos: Int, medicationID: String, fragment: MyMedicationsFragment)
    = viewModelScope.launch(dispatchers.main) {

        val requestData = DeleteMedicineModel(Gson().toJson(DeleteMedicineModel.JSONDataRequest(
            medicationID = medicationID), DeleteMedicineModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Deleting medicine....")
        _deleteMedicine.removeSource(deleteMedicineSource)
        withContext(dispatchers.io) {
            deleteMedicineSource = useCase.invokeDeleteMedicine(requestData, medicationID)
        }
        _deleteMedicine.addSource(deleteMedicineSource) {
            _deleteMedicine.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.isProcessed) {
                    toastMessage(context.resources.getString(R.string.MEDICINE_DELETED))
                }
                fragment.updateData(spinnerPos)

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }
    }

    fun callSetAlertApi(medicine: MedicineListByDayModel.Medication,
                        fragment: MedicineDashboardFragment) = viewModelScope.launch(dispatchers.main) {

        val medicationID = medicine.medicationId.toString()
        val setAlert = medicine.notification!!.setAlert
        val requestData = SetAlertModel(Gson().toJson(SetAlertModel.JSONDataRequest(
               medicationID = medicationID,
            setAlert = setAlert!!), SetAlertModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Updating Notification Alert...")
        _setAlert.removeSource(setAlertSource)
        withContext(dispatchers.io) {
            setAlertSource = useCase.invokeSetAlert(requestData)
        }
        _setAlert.addSource(setAlertSource) {
            _setAlert.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                val msgTxt: String = if (setAlert) {
                    context.resources.getString(R.string.ALERT_ENABLED)
                } else {
                    context.resources.getString(R.string.ALERT_DISABLED)
                }
                if (it.data!!.isProcessed) {
                    val list = MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    for (medicineDetails in list) {
                        if (medicineDetails.medicationId.toString().equals(medicationID, ignoreCase = true)) {
                            medicineDetails.notification!!.setAlert = setAlert
                            break
                        }
                    }
                    fragment.updateMedicatinesList()
                    updateNotificationAlert(medicationID, setAlert)
                    toastMessage(msgTxt)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun getMedicationInTakeByScheduleID(notificationData: Intent, fragment: MedicineDashboardFragment) = viewModelScope.launch(dispatchers.main) {

        val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
        val date = notificationData.getStringExtra(Constants.DATE)!!
        val requestData = MedicineInTakeModel(Gson().toJson(MedicineInTakeModel.JSONDataRequest(
            scheduleID = scheduleID.toInt(),
            recordDate = date), MedicineInTakeModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Loading...")
        _listMedicationInTake.removeSource(listMedicationInTakeSource)
        withContext(dispatchers.io) {
            listMedicationInTakeSource = useCase.invokeGetMedicationInTakeByScheduleID(requestData, scheduleID.toInt())
        }
        _listMedicationInTake.addSource(listMedicationInTakeSource) {
            _listMedicationInTake.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                val medicationInTakes = it.data!!.medicationInTakes
                var status = ""
                var medicationInTakeID = 0
                if (medicationInTakes.isNotEmpty()) {
                    status = medicationInTakes[0].status
                    medicationInTakeID = medicationInTakes[0].medicineInTakeId
                }
                fragment.defaultNotificationDialog!!.updateMedicineDetails(status, medicationInTakeID)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callAddMedicineIntakeApi(medicationInTakeList:List<AddInTakeModel.MedicationInTake>,fragment: MedicineDashboardFragment) = viewModelScope.launch(dispatchers.main) {
/*        fun callAddMedicineIntakeApi(medicationInTakeList: List<AddInTakeModel.MedicationInTake>) =
            viewModelScope.launch(dispatchers.main) {*/

        val requestData = AddInTakeModel(Gson().toJson(AddInTakeModel.JSONDataRequest(
            medicationInTake = medicationInTakeList), AddInTakeModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Adding Medicine Intake...")
        _addMedicineIntake.removeSource(addMedicineIntakeSource)
        withContext(dispatchers.io) {
            addMedicineIntakeSource = useCase.invokeAddMedicineIntake(requestData)
        }
        _addMedicineIntake.addSource(addMedicineIntakeSource) {
            _addMedicineIntake.value = it.data
            //updateBroadcastReceiver()

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)

                if (it.data!!.medicationInTake.isNotEmpty()) {
                    val list = it.data!!.medicationInTake
                    var status = ""
                    val medicineDetailsList =
                        MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    if (medicineDetailsList.size > 0) {
                        for (inTakeObject in list) {
                            for (medicineDetails in medicineDetailsList) {
                                if (inTakeObject.medicationID == medicineDetails.medicationId) {
                                    for (scheduleDetails in medicineDetails.medicationScheduleList) {
                                        if (inTakeObject.scheduleID == scheduleDetails.scheduleId) {
                                            scheduleDetails.status = inTakeObject.status
                                            status = inTakeObject.status
                                            scheduleDetails.medicationInTakeID = inTakeObject.medicationInTakeID
                                            Timber.e("Intake Updated.....")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fragment.updateMedicatinesList()
                    updateAndCancelNotification(status)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun updateAndCancelNotification(takeStatus: String) {
        try {
            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
            val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
            val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
            println("notificationData=>$notificationData")
            if (notificationID != -1 && !Utilities.isNullOrEmpty(medName)) {
                val text = "You have $takeStatus : $medName"

                //Cancel notification
                cancelNotification(notificationManager, notificationID)
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                //Update the Notification to show that the Status has been Updated.
                val repliedNotification = NotificationCompat.Builder(context, "fcm_medication_channel")
                    .setSmallIcon(R.drawable.img_hlmt_logo_notification)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSound(alarmSound)
                    .setTicker(context.resources.getString(R.string.app_name))
                    .setContentTitle(text)
                notificationManager.notify(notificationID, repliedNotification.build())
                Handler(Looper.getMainLooper()).postDelayed({
                    cancelNotification(notificationManager,notificationID)
                }, 3000)
                MedicationSingleton.getInstance()!!.setNotificationIntent(Intent())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkRelativeExistAndShowNotification(context: Context, data: ReminderNotification) = viewModelScope.launch {
        val relativeId = data.personID
        var relativeDetails: UserRelatives
        withContext(dispatchers.io) {
            relativeDetails = useCase.invokeGetUserRelativeDetailsByRelativeId(relativeId)
            if (relativeDetails != null && relativeId == relativeDetails.relativeID) {
                medicationTrackerHelper.displayMedicineReminderNotification(context, data, relativeDetails)
            } else {
                Timber.e("Relative Details not Exist for PersonID--->$relativeId")
            }
        }
    }

    fun checkRelativeExistAndLaunchApp( intent: Intent ) = viewModelScope.launch {
        val relativeId = intent.getStringExtra(Constants.PERSON_ID)!!
        var relativeDetails: UserRelatives
        withContext(dispatchers.io) {
            relativeDetails = useCase.invokeGetUserRelativeDetailsByRelativeId(relativeId)
            if (relativeDetails != null && relativeId == relativeDetails.relativeID) {

                //Switch Profile Details
                sharedPref.edit().putString(PreferenceConstants.PERSONID,relativeDetails.relativeID).apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE,relativeDetails.relationshipCode).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,relativeDetails.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,relativeDetails.contactNo).apply()
                sharedPref.edit().putString(PreferenceConstants.FIRSTNAME,relativeDetails.firstName).apply()
                sharedPref.edit().putString(PreferenceConstants.GENDER,relativeDetails.gender).apply()

                val launchIntent = Intent()
                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                launchIntent.putExtra(Constants.DATE,intent.getStringExtra(Constants.DATE))
                context.startActivity(launchIntent)
            } else {
                Utilities.toastMessageLong( context,"You have Already Deleted Family Member Related to this Notification" )
                Timber.e("Relative Details not Exist for PersonID--->$relativeId")
            }
        }
    }

    fun routeToScheduleDetails(view: View, bundle: Bundle) = viewModelScope.launch {
        var isAdded = false
        withContext(dispatchers.io) {
            val medName = bundle.get(Constants.MEDICINE_NAME).toString().split(" ")[0]
            val medList = useCase.getOngoingMedicines()
            val distinctMedList = ArrayList<String>()
            for (i in medList.indices) {
                if (!distinctMedList.contains(medList[i].drug.name)) {
                    distinctMedList.add(medList[i].drug.name!!)
                }
            }
            for (j in distinctMedList.indices) {
                if (distinctMedList[j].equals(medName, ignoreCase = true)) {
                    isAdded = true
                    break
                }
            }
        }
        if (isAdded) {
            Utilities.toastMessageShort(context, "You are already taking " + bundle.get(Constants.MEDICINE_NAME))
        } else {
            view.findNavController().navigate(R.id.action_addMedicineFragment_to_scheduleMedicineFragment, bundle)
        }
    }

    fun getMedicineDetailsByMedicationId(medicationId: Int) = viewModelScope.launch {
        withContext(dispatchers.io) {
            val medicine = useCase.getMedicineDetailsByMedicationId(medicationId)
            val start = medicine.PrescribedDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            medicine.PrescribedDate = start[0]
            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                val end = medicine.EndDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                medicine.EndDate = end[0]
            }
            medicineDetails.postValue(medicine)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean(PreferenceConstants.IS_LOGIN, false)
    }

    private fun updateNotificationAlert(medicationID: String, setAlert: Boolean) = viewModelScope.launch {
        withContext(dispatchers.io) {
            useCase.invokeUpdateNotificationAlert(medicationID, setAlert)
        }
    }

    fun getOngoingMedicines() = viewModelScope.launch {
        withContext(dispatchers.io) {
            medicinesList.postValue(useCase.getOngoingMedicines())
        }
    }

    fun getCompletedMedicines() = viewModelScope.launch {
        withContext(dispatchers.io) {
            medicinesList.postValue(useCase.getCompletedMedicines())
        }
    }

    fun getAllMyMedicines() = viewModelScope.launch {
        withContext(dispatchers.io) {
            medicinesList.postValue(useCase.getAllMyMedicines())
        }
    }

    fun getPastMedicines() = viewModelScope.launch {
        withContext(dispatchers.io) {
            medicinesList.postValue(useCase.getPastMedicines())
        }
    }

    private fun cancelNotification(manager: NotificationManager, notificationId: Int) {
        manager.cancel(notificationId)
    }

    private fun closeNotificationDrawer(context: Context) {
        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
    }

    /////////////////////////////

    fun getMedDetailsByMedicationIdApi(medicationID: String) {
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("MedicationID", medicationID)
        jsonObjectJSONData.put("Message", "Get medicine....")
        getResponseFromServer(jsonObjectJSONData,"1")
    }

    private fun getMedDetailsByMedicationIdApiResp(response: String) {
        if ( !Utilities.isNullOrEmpty(response) ) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: GetMedicineModel.GetMedicineResponse = gson.fromJson(response,
                object : TypeToken<GetMedicineModel.GetMedicineResponse?>() {}.type)
            val medication = jsonData.medication
            Timber.e("MedicationID--->${medication.medicationId}")
            try {
                val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
                val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
                val scheduleTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)?.substring(0, 5)
                val time = notificationData.getStringExtra(Constants.TIME)

                if (!Utilities.isNullOrEmptyOrZero(medication.medicationId.toString())) {
                    var isExist = false
                    for (i in medication.scheduleList) {
                        if (i.scheduleTime!!.substring(0, 5) == scheduleTime) {
                            isExist = true
                            break
                        }
                    }

                    if (isExist) {
                        getMedInTakeFromNotificationByScheduleIDApi()
                    } else {
                        closeNotificationDrawer(context)
                        cancelNotification(notificationManager, notificationID)
                        Utilities.toastMessageShort(context, "You have Removed or Rescheduled Time $time for $medName")
                    }

                } else {
                    closeNotificationDrawer(context)
                    cancelNotification(notificationManager, notificationID)
                    Utilities.toastMessageShort(context, "You have Already Deleted $medName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Timber.e("Error while fetching Response from server")
        }
    }

    private fun getMedInTakeFromNotificationByScheduleIDApi() {
        val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
        val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
        val recordDate = notificationData.getStringExtra(Constants.DATE)!!
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("ScheduleID", scheduleID)
        jsonObjectJSONData.put("RecordDate", recordDate)
        getResponseFromServer(jsonObjectJSONData,"2")
    }

    private fun getMedInTakeFromNotificationByScheduleIDResp(response: String) {
        if ( !Utilities.isNullOrEmpty(response) ) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: MedicineInTakeModel.MedicineDetailsResponse = gson.fromJson(response,
                object : TypeToken<MedicineInTakeModel.MedicineDetailsResponse?>() {}.type)
            val medicationInTakes = jsonData.medicationInTakes
            val medicationInTakeID = if (medicationInTakes.isNotEmpty()) {
                medicationInTakes[0].medicineInTakeId
            } else {
                0
            }
            Timber.e("MedicationInTakeID--->$medicationInTakeID")
            addMedIntakeApi(medicationInTakeID)
        } else {
            Timber.e("Error while fetching Response from server")
        }
    }

    private fun addMedIntakeApi(medicationInTakeID : Int ) {
        val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
        val medicationInTakes = JSONArray()
        val intake = JSONObject()
        intake.put("MedicationID", notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toInt())
        intake.put("ID", medicationInTakeID)
        intake.put("ScheduleID", notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toInt())
        intake.put("Status", notificationData.getStringExtra(Constants.NOTIFICATION_ACTION)!!)
        intake.put("FeelStatus", "")
        intake.put("MedDate", notificationData.getStringExtra(Constants.DATE)!!)
        intake.put("MedTime", notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!)
        intake.put("Dosage", notificationData.getStringExtra(Constants.DOSAGE)!!)
        intake.put("Description", "")
        intake.put("CreatedDate", notificationData.getStringExtra(Constants.DATE)!!)
        medicationInTakes.put(intake)

        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("MedicationInTake",medicationInTakes)
        getResponseFromServer(jsonObjectJSONData,"3")
    }

    private fun addMedIntakeResp(response: String) {
        if ( !Utilities.isNullOrEmpty(response) ) {
            val jsonObjectResponse = JSONObject(response)
            val intakeArray = jsonObjectResponse.get("MedicationInTake")
            Timber.e("MedicationInTake--->$response")
            if (!Utilities.isNullOrEmpty(intakeArray.toString())) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonData: AddInTakeModel.AddInTakeResponse = gson.fromJson(response,
                    object : TypeToken<AddInTakeModel.AddInTakeResponse?>() {}.type)
                val medicationInTake = jsonData.medicationInTake
                Timber.e("MedicationInTake--->$medicationInTake")

                if (medicationInTake.isNotEmpty()) {
                    val medicineDetailsList = MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    if (medicineDetailsList.size > 0) {
                        for (inTakeObject in medicationInTake) {
                            for (medicineDetails in medicineDetailsList) {
                                if (inTakeObject.medicationID == medicineDetails.medicationId) {
                                    for (scheduleDetails in medicineDetails.medicationScheduleList) {
                                        if (inTakeObject.scheduleID == scheduleDetails.scheduleId) {
                                            scheduleDetails.status = inTakeObject.status
                                            scheduleDetails.medicationInTakeID = inTakeObject.medicationInTakeID
                                            Timber.e("Intake Updated.....")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    updateAndCancelNotification(medicationInTake[0].status)
                    //meddashboardFragment!!.updateMedicatinesList()
                }
            }
        } else {
            Timber.e("Error while fetching Response from server")
        }
    }

    private fun getResponseFromServer( jsonRequest:JSONObject,from: String) : String {
        var jsonData = JSONObject()
        try {
            val jsonObject = JSONObject()

            val jsonObjectHeader = JSONObject()
            jsonObjectHeader.put("RequestID", Configuration.RequestID)
            jsonObjectHeader.put("DateTime", DateHelper.currentDateAsStringddMMMyyyy)
            jsonObjectHeader.put("ApplicationCode", Configuration.ApplicationCode)
            jsonObjectHeader.put("AuthTicket", authToken)
            jsonObjectHeader.put("PartnerCode", Configuration.PartnerCode)
            jsonObjectHeader.put("EntityType", Configuration.EntityType)
            jsonObjectHeader.put("HandShake", Configuration.Handshake)

            jsonObject.put("Header",jsonObjectHeader)
            jsonObject.put("JSONData",jsonRequest.toString())

            val logging = HttpLoggingInterceptor {
                //Timber.i("HttpLogging--> $it")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            // Create Retrofit
            val retrofit = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .addInterceptor(logging)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .build())
                .baseUrl(Constants.strAPIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            // Create Service
            val service = retrofit.create(MedNotificationApiService::class.java)
            Timber.e("Request--->$jsonObject")
            val body = getEncryptedRequestBody(jsonObject.toString())

            CoroutineScope(Dispatchers.Main).launch {
                var response : Response<ResponseBody>? = null
                when(from) {
                    "1" -> response = service.getMedicineDetailsApi(body)
                    "2" -> response = service.getMedicationInTakeApi(body)
                    "3" -> response = service.addIntakeApi(body)
                }
                //val response = service.getMedicineDetailsApi(body)
                withContext(Dispatchers.IO) {
                    if (response!!.isSuccessful) {
                        val resp = getResponse(response.body()!!.string())
                        Timber.e("Resp--->$resp")
                        val jsonObjectResponse = JSONObject(resp)
                        if (!jsonObjectResponse.isNull("JSONData")) {
                            jsonData = JSONObject(jsonObjectResponse.optString("JSONData"))

                            when(from) {
                                "1" -> getMedDetailsByMedicationIdApiResp(jsonObjectResponse.optString("JSONData"))
                                "2" -> getMedInTakeFromNotificationByScheduleIDResp(jsonObjectResponse.optString("JSONData"))
                                "3" -> addMedIntakeResp(jsonObjectResponse.optString("JSONData"))
                            }
                        }
                    } else {
                        Timber.e("RETROFIT_ERROR--->${response.code()}")
                    }
                }
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
        return jsonData.toString()
    }

    private fun getEncryptedRequestBody(request: String): RequestBody {
        val encryptedReq = EncryptionUtility.encrypt(Configuration.SecurityKey, request, Configuration.SecurityKey)
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), encryptedReq)
        //return encryptedReq.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun getResponse( response : String) : String {
        val decrypted = EncryptionUtility.decrypt(Configuration.SecurityKey,response,Configuration.SecurityKey)
        val decryptedResponse: String = decrypted
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse
    }

/*    fun getMedicineDetailsByMedicationIdApi(medicationID: String, from: String) = viewModelScope.launch(dispatchers.main) {

        val requestData = GetMedicineModel(Gson().toJson(GetMedicineModel.JSONDataRequest(
            medicationID = medicationID), GetMedicineModel.JSONDataRequest::class.java), authToken)

        //_progressBar.value = Event("Getting Medicine Details...")
        _getMedicine.removeSource(getMedicineSource)
        withContext(dispatchers.main) {
            getMedicineSource = useCase.invokeGetMedicine(requestData)
        }
        _getMedicine.addSource(getMedicineSource) {
            _getMedicine.value = it.data
            //updateBroadcastReceiver()

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                val medication = it.data!!.medication
                val medicationId = it.data!!.medication.MedicationID
                Timber.i("Medication--->${it.data!!.medication}")
                if (from.equals(Constants.NOTIFICATION, ignoreCase = true)) {
                    try {
                        val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                        val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
                        val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
                        val scheduleTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)?.substring(0, 5)
                        val time = notificationData.getStringExtra(Constants.TIME)

                        if (!Utilities.isNullOrEmptyOrZero(medicationId.toString())) {
                            var isExist = false
                            for (i in medication.scheduleList) {
                                if (i.scheduleTime!!.substring(0, 5) == scheduleTime) {
                                    isExist = true
                                    break
                                }
                            }

                            if (isExist) {
                                getMedicationInTakeFromNotificationByScheduleID(notificationData)
                            } else {
                                closeNotificationDrawer(context)
                                cancelNotification(notificationManager, notificationID)
                                Utilities.toastMessageShort(context, "You have Removed or Rescheduled Time $time for $medName")
                            }

                        } else {
                            closeNotificationDrawer(context)
                            cancelNotification(notificationManager, notificationID)
                            Utilities.toastMessageShort(context, "You have Already Deleted $medName")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun getMedicationInTakeFromNotificationByScheduleID(notificationData: Intent) = viewModelScope.launch(dispatchers.main) {

        val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
        val date = notificationData.getStringExtra(Constants.DATE)!!
        val requestData = MedicineInTakeModel(Gson().toJson(MedicineInTakeModel.JSONDataRequest(
            scheduleID = scheduleID.toInt(),
            recordDate = date), MedicineInTakeModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Loading...")
        _listMedicationInTake.removeSource(listMedicationInTakeSource)
        withContext(dispatchers.io) {
            listMedicationInTakeSource = useCase.invokeGetMedicationInTakeByScheduleID(requestData, scheduleID.toInt())
        }
        _listMedicationInTake.addSource(listMedicationInTakeSource) {
            _listMedicationInTake.value = it.data
            //updateBroadcastReceiver()

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                val medicationInTakes = it.data!!.medicationInTakes
                val medicationInTakeID: Int
                medicationInTakeID = if (medicationInTakes.isNotEmpty()) {
                    medicationInTakes[0].medicineInTakeId
                } else {
                    0
                }
                val medicationInTakeList: MutableList<AddInTakeModel.MedicationInTake> = mutableListOf()
                val intake = AddInTakeModel.MedicationInTake()
                intake.medicationID = notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toString().toInt()
                intake.medicationInTakeID = medicationInTakeID
                intake.scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toString().toInt()
                intake.status = notificationData.getStringExtra(Constants.NOTIFICATION_ACTION)!!.toString()
                intake.medDate = notificationData.getStringExtra(Constants.DATE)!!
                intake.medTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!
                intake.dosage = notificationData.getStringExtra(Constants.DOSAGE)!!
                intake.createdDate = notificationData.getStringExtra(Constants.DATE)!!
                medicationInTakeList.add(intake)
                //callAddMedicineIntakeApi(medicationInTakeList)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }*/

}