package com.caressa.home.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.ApiConstants
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.home.common.DataHandler
import com.caressa.home.domain.BackgroundCallUseCase
import com.caressa.home.ui.DialogUpdateApp
import com.caressa.model.entity.AppVersion
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.home.CheckAppUpdateModel
import com.caressa.model.home.SaveCloudMessagingIdModel
import com.caressa.model.home.SaveFeedbackModel
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.model.medication.MedicationListModel
import com.caressa.model.parameter.*
import com.caressa.model.shr.ListDocumentTypesModel
import com.caressa.model.shr.ListRelativesModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BackgroundCallViewModel(private val useCase: BackgroundCallUseCase, private val dispatchers: AppDispatchers,
                              private val sharedPref: SharedPreferences, private val dataHandler : DataHandler) : BaseViewModel() {

    private var personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    private var accountId = sharedPref.getString(PreferenceConstants.ACCOUNTID,"")!!
    private var email = sharedPref.getString(PreferenceConstants.EMAIL,"")!!
    private var phone = sharedPref.getString(PreferenceConstants.PHONE,"")!!
    private var firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME, "")!!
    private var gender = sharedPref.getString(PreferenceConstants.GENDER, "")!!
    private val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!
    private val fcmToken = sharedPref.getString(PreferenceConstants.FCM_REGISTRATION_ID,"")!!

    var isBackgroundApiCall = false
    var profileSwitched = false

    var saveCloudMessagingIdSource: LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> = MutableLiveData()
    val _saveCloudMessagingId = MediatorLiveData<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>()
    val saveCloudMessagingId: LiveData<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse> get() = _saveCloudMessagingId

    var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    val _medicalProfileSummary = MediatorLiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>()
    val medicalProfileSummary: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> get() = _medicalProfileSummary

    var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> = MutableLiveData()
    val _listRelatives = MediatorLiveData<Resource<ListRelativesModel.ListRelativesResponse>>()
    val listRelatives: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> get() = _listRelatives

    var listDocumentTypesSource: LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> = MutableLiveData()
    val _listDocumentTypes = MediatorLiveData<ListDocumentTypesModel.ListDocumentTypesResponse>()
    val listDocumentTypes: LiveData<ListDocumentTypesModel.ListDocumentTypesResponse> get() = _listDocumentTypes

    var paramUserSource: LiveData<Resource<ParameterListModel.Response>> = MutableLiveData()
    val _paramList = MediatorLiveData<ParameterListModel.Response>()
    val paramList: LiveData<ParameterListModel.Response> get() = _paramList

    var bmiUserSource: LiveData<Resource<BMIHistoryModel.Response>> = MutableLiveData()
    private val _bmiHistoryList = MediatorLiveData<BMIHistoryModel.Response>()
    val bmiHistoryList: LiveData<BMIHistoryModel.Response> get() = _bmiHistoryList

    var whrUserSource: LiveData<Resource<WHRHistoryModel.Response>> = MutableLiveData()
    private val _whrHistoryList = MediatorLiveData<WHRHistoryModel.Response>()
    val whrHistoryList: LiveData<WHRHistoryModel.Response> get() = _whrHistoryList

    var bloodPressureUserSource: LiveData<Resource<BloodPressureHistoryModel.Response>> = MutableLiveData()
    private val _bloodPressureHistoryList = MediatorLiveData<BloodPressureHistoryModel.Response>()
    val bloodPressureHistoryList: LiveData<BloodPressureHistoryModel.Response> get() = _bloodPressureHistoryList

    var labRecordUserSource: LiveData<Resource<TrackParameterMaster.HistoryResponse>> = MutableLiveData()
    val _labRecordList = MediatorLiveData<TrackParameterMaster.HistoryResponse>()
    val labRecordList: LiveData<TrackParameterMaster.HistoryResponse> get() = _labRecordList

    var getStepsGoalSource: LiveData<Resource<GetStepsGoalModel.Response>> = MutableLiveData()
    val _getStepsGoal = MediatorLiveData<GetStepsGoalModel.Response>()
    val getStepsGoal: LiveData<GetStepsGoalModel.Response> get() = _getStepsGoal

    val  navDrawerOptionList = MutableLiveData<List<DataHandler.NavDrawerOption>>()

    var medicineListDataSource: LiveData<Resource<MedicationListModel.Response>> = MutableLiveData()
    val _medicineList = MediatorLiveData<MedicationListModel.Response>()
    val medicineList: LiveData<MedicationListModel.Response> get() = _medicineList

    var checkAppUpdateSource: LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> = MutableLiveData()
    val _checkAppUpdate = MediatorLiveData<CheckAppUpdateModel.CheckAppUpdateResponse>()
    val checkAppUpdate: LiveData<CheckAppUpdateModel.CheckAppUpdateResponse> get() = _checkAppUpdate

    fun callBackgroundApiCall(showProgress:Boolean) = viewModelScope.launch(dispatchers.main){
        if ( showProgress ) {
            _progressBar.value = Event("Synchronising Profile...")
        }
        Timber.i("Inside callBackgroundApiCall=> $isBackgroundApiCall")
        if(!isBackgroundApiCall) {
            withContext(dispatchers.io) {
                val dataSyncList = useCase.invokeGetSyncMasterData(personId)
                //Weekly Sync Api Calls
                Timber.i("Data Sync--->$dataSyncList")
                Timber.i("PersonId--->$personId")
                if (dataSyncList.find { it.apiName == ApiConstants.TRACK_PARAM_LIST_MASTER } == null) {
                    Timber.e("Inside Fresh Login")
                    getParameterList()
                    callDocumentTypesApi(forceRefresh = false)
                    getMedicalProfileSummary(forceRefresh = false)
                    callListRelativesApi(forceRefresh = false)
                    getBMIHistory("")
                    getBloodPressureHistory("")
                    getWHRHistory("")
                    getLabRecordList("")
                    fetchStepsGoal()
                    fetchMedicationList("")
                } else if ( profileSwitched ) {
                    Timber.e("Inside Switch Profile")
                    useCase.invokeDeleteHistoryWithOtherPersonId(personId)
                    getMedicalProfileSummary(forceRefresh = false)
                    getBMIHistory("")
                    getBloodPressureHistory("")
                    getWHRHistory("")
                    getLabRecordList("")
                    fetchStepsGoal()
                    fetchMedicationList("")
                    callListRelativesApi(forceRefresh = false)
                    profileSwitched = false
                } else {
                    Timber.e("Inside Normal Flow")
                    for (item in dataSyncList) {
                        when (item.apiName) {
                            ApiConstants.TRACK_PARAM_LIST_MASTER -> {
                                if (DateHelper.differenceInDays(DateHelper.convertStringDateToDate(
                                        item.syncDate, DateHelper.SERVER_DATE_YYYYMMDD), DateHelper.currentDate!!) >= 7) {
                                    getParameterList()
                                }
                            }
                            ApiConstants.DOC_TYPE_MASTER -> {
                                if (DateHelper.differenceInDays(DateHelper.convertStringDateToDate(
                                            item.syncDate, DateHelper.SERVER_DATE_YYYYMMDD), DateHelper.currentDate!!) >= 7) {
                                    callDocumentTypesApi(forceRefresh = true)
                                }
                            }
                            ApiConstants.BMI_HISTORY -> {
                                getBMIHistory(item.syncDate!!)
                            }
                            ApiConstants.BLOOD_PRESSURE_HISTORY -> {
                                getBloodPressureHistory(item.syncDate!!)
                            }
                            ApiConstants.WHR_HISTORY -> {
                                getWHRHistory(item.syncDate!!)
                            }
                            ApiConstants.PARAMETER_HISTORY -> {
                                getLabRecordList(item.syncDate!!)
                            }
                            ApiConstants.MEDICATION_LIST -> {
                                fetchMedicationList(item.syncDate!!)
                            }
                        }
                    }
                    getMedicalProfileSummary(forceRefresh = false)
                    fetchStepsGoal()
                    callListRelativesApi(forceRefresh = false)
                }

            }
            isBackgroundApiCall = true
        }
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun callSaveCloudMessagingIdApi(newFcmToken:String,forceRefresh: Boolean)
            = viewModelScope.launch(dispatchers.main) {

        Timber.e("\nOldFcmToken--->$fcmToken")
        if ( Utilities.isNullOrEmpty(fcmToken) || newFcmToken != fcmToken ) {
            val requestData = SaveCloudMessagingIdModel(Gson().toJson(SaveCloudMessagingIdModel.JSONDataRequest(
                key = SaveCloudMessagingIdModel.Key(
                    accountID = accountId,
                    registrationID = newFcmToken)),SaveCloudMessagingIdModel.JSONDataRequest::class.java),authToken )

            Timber.e("\n*************Sending RegistrationId to Server*************")
            Timber.e("\nRegistrationID----->$newFcmToken")
            _saveCloudMessagingId.removeSource(saveCloudMessagingIdSource)
            withContext(dispatchers.io) {
                saveCloudMessagingIdSource = useCase.invokeSaveCloudMessagingId(isForceRefresh = forceRefresh, data = requestData)
            }
            _saveCloudMessagingId.addSource(saveCloudMessagingIdSource) {
                _saveCloudMessagingId.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    val registrationID = it.data!!.registrationID
                    if ( !Utilities.isNullOrEmpty(registrationID) ) {
                        refreshFcmToken(registrationID)
                        Timber.e("\nRefreshedFcmToken--->$registrationID")
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    if(it.errorNumber.equals("1100014",true)){
                        _toastError.value = Event(it.errorMessage)
                        logoutUser()
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    }

    private fun callListRelativesApi(forceRefresh: Boolean) = viewModelScope.launch(dispatchers.main) {

        val adminPersonId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,"")!!
        val requestData = ListRelativesModel(Gson().toJson(ListRelativesModel.JSONDataRequest(
                personID = adminPersonId ), ListRelativesModel.JSONDataRequest::class.java) , authToken )

        _listRelatives.removeSource(listRelativesSource)
        withContext(dispatchers.io) {
            listRelativesSource = useCase.invokeRelativesList(isForceRefresh = forceRefresh, data = requestData )
        }
        _listRelatives.addSource(listRelativesSource) {
            _listRelatives.value = it

            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    private fun callDocumentTypesApi(forceRefresh: Boolean) = viewModelScope.launch(dispatchers.main) {

        val requestData = ListDocumentTypesModel(Gson().toJson(ListDocumentTypesModel.JSONDataRequest(
                from = "70" ), ListDocumentTypesModel.JSONDataRequest::class.java) , authToken )

        _listDocumentTypes.removeSource(listDocumentTypesSource)
        withContext(dispatchers.io) {
            listDocumentTypesSource = useCase.invokeDocumentType(isForceRefresh = forceRefresh, data = requestData)
        }
        _listDocumentTypes.addSource(listDocumentTypesSource) {
            _listDocumentTypes.value = it.data

            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    private fun getParameterList() = viewModelScope.launch(dispatchers.main) {

        val requestData = ParameterListModel(Gson().toJson(ParameterListModel.JSONDataRequest(
                    from = "60",
                    message = "Getting List.."), ParameterListModel.JSONDataRequest::class.java), authToken )

        _paramList.removeSource(paramUserSource)
        withContext(dispatchers.io){ paramUserSource = useCase.invokeParamList(data = requestData)}
        _paramList.addSource(paramUserSource){
            _paramList.value = it.data
            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    fun getBMIHistory(lastSyncDate : String) = viewModelScope.launch(dispatchers.main) {

/*        val requestData = BMIHistoryModel(Gson().toJson(BMIHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BMIHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*/

        val jsonReq: BMIHistoryModel.JSONDataRequest
        if(lastSyncDate.isNotEmpty()) {
            jsonReq = BMIHistoryModel.JSONDataRequest( personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        } else {
            jsonReq = BMIHistoryModel.JSONDataRequest( personID = personId)
        }
        val requestData = BMIHistoryModel(Gson().toJson(jsonReq, BMIHistoryModel.JSONDataRequest::class.java),authToken )

        _bmiHistoryList.removeSource(bmiUserSource)
        withContext(dispatchers.io){ bmiUserSource = useCase.invokeBMIHistory(data = requestData,personId)}
        _bmiHistoryList.addSource(bmiUserSource){
            _bmiHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    snackMessage(it.errorMessage)
                }            }
        }
    }

    fun getBloodPressureHistory(lastSyncDate : String) = viewModelScope.launch(dispatchers.main) {

/*        val requestData = BloodPressureHistoryModel(Gson().toJson(BloodPressureHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BloodPressureHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*/

        val jsonReq: BloodPressureHistoryModel.JSONDataRequest
        if(lastSyncDate.isNotEmpty()) {
            jsonReq = BloodPressureHistoryModel.JSONDataRequest( personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        } else {
            jsonReq = BloodPressureHistoryModel.JSONDataRequest( personID = personId)
        }
        val requestData = BloodPressureHistoryModel(Gson().toJson(jsonReq, BloodPressureHistoryModel.JSONDataRequest::class.java),authToken )

        _bloodPressureHistoryList.removeSource(bloodPressureUserSource)
        withContext(dispatchers.io){ bloodPressureUserSource = useCase.invokeBloodPressureHistory(data = requestData,personId)}
        _bloodPressureHistoryList.addSource(bloodPressureUserSource){
            _bloodPressureHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    snackMessage(it.errorMessage)
                }            }
        }
    }

    fun getWHRHistory(lastSyncDate : String) = viewModelScope.launch(dispatchers.main) {

/*        val requestData = WHRHistoryModel(Gson().toJson(WHRHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), WHRHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )*/

        val jsonReq: WHRHistoryModel.JSONDataRequest
        if(lastSyncDate.isNotEmpty()) {
            jsonReq = WHRHistoryModel.JSONDataRequest( personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        } else {
            jsonReq = WHRHistoryModel.JSONDataRequest( personID = personId)
        }
        val requestData = WHRHistoryModel(Gson().toJson(jsonReq, WHRHistoryModel.JSONDataRequest::class.java),authToken )

        _whrHistoryList.removeSource(whrUserSource)
        withContext(dispatchers.io){ whrUserSource = useCase.invokeWHRHistory(data = requestData,personId)}
        _whrHistoryList.addSource(whrUserSource){
            _whrHistoryList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    snackMessage(it.errorMessage)
                }            }
        }
    }

    private fun getLabRecordList(lastSyncDate : String) = viewModelScope.launch(dispatchers.main) {

        val jsonReq: LabRecordsListModel.JSONDataRequest
        if(lastSyncDate.isNotEmpty()) {
            jsonReq = LabRecordsListModel.JSONDataRequest( personID = personId,
                lastSyncDate = DateHelper.convertDateTimeValue(lastSyncDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        }else{
            jsonReq = LabRecordsListModel.JSONDataRequest( personID = personId)
        }
        val requestData = LabRecordsListModel(Gson().toJson(jsonReq, LabRecordsListModel.JSONDataRequest::class.java),authToken )

        _labRecordList.removeSource(labRecordUserSource)
        withContext(dispatchers.io){ labRecordUserSource = useCase.invokeLabRecordsList(data = requestData,personId = personId)}
        _labRecordList.addSource(labRecordUserSource){
            _labRecordList.value = it.data
            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    private fun fetchStepsGoal() = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = GetStepsGoalModel(Gson().toJson(GetStepsGoalModel.JSONDataRequest(
                        personID = personId), GetStepsGoalModel.JSONDataRequest::class.java), authToken)

            _getStepsGoal.removeSource(getStepsGoalSource)
            withContext(dispatchers.io) {
                getStepsGoalSource = useCase.invokeFetchStepsGoal(requestData)
            }
            _getStepsGoal.addSource(getStepsGoalSource) {
                _getStepsGoal.value = it.data
                if (it.status == Resource.Status.SUCCESS) { }

                if (it.status == Resource.Status.ERROR) {
                    if(it.errorNumber.equals("1100014",true)){
                        _toastError.value = Event(it.errorMessage)
                        logoutUser()
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMedicalProfileSummary(forceRefresh: Boolean) = viewModelScope.launch(dispatchers.main) {

        val requestData = HraMedicalProfileSummaryModel(Gson().toJson(
                HraMedicalProfileSummaryModel.JSONDataRequest(PersonID = personId),
                HraMedicalProfileSummaryModel.JSONDataRequest::class.java),authToken)

        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(dispatchers.io) {
            medicalProfileSummarySource = useCase.invokeMedicalProfileSummary(isForceRefresh = forceRefresh,data = requestData,personId = personId )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            _medicalProfileSummary.value = it

            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    private fun fetchMedicationList(syncDate: String) = viewModelScope.launch(dispatchers.main) {
        val jsonReq: MedicationListModel.JSONDataRequest

        if(syncDate.isNotEmpty()){
            jsonReq = MedicationListModel.JSONDataRequest( personId = personId,
                toDate = DateHelper.convertDateTimeValue(syncDate,DateHelper.SERVER_DATE_YYYYMMDD,DateHelper.DISPLAY_DATE_DDMMMYYYY)!!)
        }else{
            jsonReq = MedicationListModel.JSONDataRequest(
                personId = personId,
                toDate = DateHelper.currentDateAsStringyyyyMMdd)
        }

        val requestData = MedicationListModel(
            Gson().toJson(jsonReq, MedicationListModel.JSONDataRequest::class.java), authToken)

        _medicineList.removeSource(medicineListDataSource)
        withContext(dispatchers.io) { medicineListDataSource = useCase.fetchMedicationList(requestData,personId) }
        _medicineList.addSource(medicineListDataSource) {
            _medicineList.value = it.data

            if (it.status == Resource.Status.SUCCESS) { }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callCheckAppUpdateApi(context: Context) = viewModelScope.launch(dispatchers.main) {

        val requestData = CheckAppUpdateModel(Gson().toJson(
            CheckAppUpdateModel.JSONDataRequest(
                app = Configuration.strAppIdentifier,
                device = "ANDROID",
                appVersion = Utilities.getAppVersion(context).toString()),
            CheckAppUpdateModel.JSONDataRequest::class.java),authToken)

        _checkAppUpdate.removeSource(checkAppUpdateSource)
        withContext(dispatchers.io) {
            checkAppUpdateSource = useCase.invokeCheckAppUpdate(isForceRefresh = true, data = requestData)
        }
        _checkAppUpdate.addSource(checkAppUpdateSource) {
            _checkAppUpdate.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    Timber.i("UpdateData=>${it.data}")
                    if ( !it.data!!.result.appVersion.isNullOrEmpty() ) {
                        val appVersion = it.data!!.result.appVersion[0]
                        val currentVersion = appVersion.currentVersion!!.toDouble().toInt()
                        //val currentVersion = 103
                        val existingVersion = Utilities.getAppVersion(context)
                        Timber.i("CurrentVersion,ExistingVersion=>$currentVersion , $existingVersion")
                        if (existingVersion < currentVersion) {
                            val dialogUpdateApp = DialogUpdateApp(context,appVersion)
                            dialogUpdateApp.show()
                        }
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }

    }

    fun getAppVersionDetails(context: Context) = viewModelScope.launch {
        var versionDetails : AppVersion? = null
        withContext(dispatchers.io) {
            versionDetails = useCase.invokeGetAppVersionDetails()
            Timber.e("appVersionDetails--->$versionDetails")
        }
        if ( versionDetails != null ) {
            val lastUpdateDate = versionDetails!!.lastUpdateDate!!
            val updateCallInterval = versionDetails!!.apiCallInterval
            val forceUpdate = versionDetails!!.forceUpdate
            val currentVersion = versionDetails!!.currentVersion!!.toDouble().toInt()
            //val currentVersion = 103
            val existingVersion = Utilities.getAppVersion(context)
            if ( forceUpdate ) {
                if (existingVersion < currentVersion) {
                    val dialogUpdateApp = DialogUpdateApp(context,versionDetails!!)
                    dialogUpdateApp.show()
                } else {
                    callCheckAppUpdateApi(context)
                }
            } else {
                if (existingVersion < currentVersion) {
                    if ( DateHelper.getDifferenceInDays(lastUpdateDate,DateHelper.currentDateAsStringyyyyMMdd) >= updateCallInterval ) {
                        val dialogUpdateApp = DialogUpdateApp(context,versionDetails!!)
                        dialogUpdateApp.show()
                    }
                }
            }
        } else {
            callCheckAppUpdateApi(context)
        }
    }

    fun logoutUser() = viewModelScope.launch(dispatchers.main){
        sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,false).apply()
        sharedPref.edit().putString(PreferenceConstants.EMAIL,"").apply()
        sharedPref.edit().putString(PreferenceConstants.PHONE,"").apply()
        sharedPref.edit().putString(PreferenceConstants.TOKEN,"").apply()
        //sharedPref.edit().putString(PreferenceConstants.FCM_REGISTRATION_ID,"").apply()
        sharedPref.edit().putString(PreferenceConstants.PERSONID,"").apply()
        sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID,"").apply()
        sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE,"").apply()
        sharedPref.edit().putString(PreferenceConstants.ACCOUNTID,"").apply()
        sharedPref.edit().putString(PreferenceConstants.FIRSTNAME,"").apply()
        sharedPref.edit().putString(PreferenceConstants.GENDER,"").apply()
        sharedPref.edit().putString(PreferenceConstants.SELECTION_PARAM,"").apply()
        withContext(dispatchers.io){
            useCase.invokeLogout()
        }
    }

    fun refreshPersonId(){
        personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
        email = sharedPref.getString(PreferenceConstants.EMAIL,"")!!
        phone = sharedPref.getString(PreferenceConstants.PHONE,"")!!
        firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME,"")!!
        gender = sharedPref.getString(PreferenceConstants.GENDER,"")!!
    }

    fun refreshFcmToken( newToken:String ){
        sharedPref.edit().putString(PreferenceConstants.FCM_REGISTRATION_ID, newToken).apply()
    }

    fun isSelfUser(): Boolean {
        return personId.equals(sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,""))
    }

}