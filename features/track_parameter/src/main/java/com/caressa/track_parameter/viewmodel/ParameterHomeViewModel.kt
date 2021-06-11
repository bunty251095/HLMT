package com.caressa.track_parameter.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParameterHomeViewModel(
private val useCase: ParameterManagementUseCase,
private val dispatchers: AppDispatchers,
private val sharedPref: SharedPreferences) : BaseViewModel() {

    private var personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!

    var paramUserSource: LiveData<Resource<ParameterListModel.Response>> = MutableLiveData()
    private val _paramList = MediatorLiveData<ParameterListModel.Response>()
    val paramList: LiveData<ParameterListModel.Response> get() = _paramList

    private var labRecordUserSource: LiveData<Resource<TrackParameterMaster.HistoryResponse>> = MutableLiveData()
    private val _labRecordList = MediatorLiveData<TrackParameterMaster.HistoryResponse>()
    val labRecordList: LiveData<TrackParameterMaster.HistoryResponse> get() = _labRecordList

    private var bmiUserSource: LiveData<Resource<BMIHistoryModel.Response>> = MutableLiveData()
    private val _bmiHistoryList = MediatorLiveData<BMIHistoryModel.Response>()
    val bmiHistoryList: LiveData<BMIHistoryModel.Response> get() = _bmiHistoryList

    private var whrUserSource: LiveData<Resource<WHRHistoryModel.Response>> = MutableLiveData()
    private val _whrHistoryList = MediatorLiveData<WHRHistoryModel.Response>()
    val whrHistoryList: LiveData<WHRHistoryModel.Response> get() = _whrHistoryList

    private var bloodPressureUserSource: LiveData<Resource<BloodPressureHistoryModel.Response>> = MutableLiveData()
    private val _bloodPressureHistoryList = MediatorLiveData<BloodPressureHistoryModel.Response>()
    val bloodPressureHistoryList: LiveData<BloodPressureHistoryModel.Response> get() = _bloodPressureHistoryList

    fun getParameterList() = viewModelScope.launch(dispatchers.main) {

        val requestData = ParameterListModel(Gson().toJson(ParameterListModel.JSONDataRequest(
                    from = "60",
                    message = "Getting List.."), ParameterListModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

        _progressBar.value = Event("Fetching Parameter List..")
        _paramList.removeSource(paramUserSource)
        withContext(dispatchers.io){ paramUserSource = useCase.invokeParamList(data = requestData)}
        _paramList.addSource(paramUserSource){
            _paramList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    snackMessage(it.errorMessage)
                }
            }
        }
    }

    fun getLabRecordList() = viewModelScope.launch(dispatchers.main) {

        val requestData = LabRecordsListModel(Gson().toJson(LabRecordsListModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), LabRecordsListModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

        _labRecordList.removeSource(labRecordUserSource)
        withContext(dispatchers.io){ labRecordUserSource = useCase.invokeLabRecordsList(data = requestData,personId = sharedPref.getString(PreferenceConstants.PERSONID,"0")!!)}
        _labRecordList.addSource(labRecordUserSource){
            _labRecordList.value = it.data
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

    fun getBMIHistory() = viewModelScope.launch(dispatchers.main) {

        val requestData = BMIHistoryModel(Gson().toJson(BMIHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BMIHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

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

    fun getWHRHistory() = viewModelScope.launch(dispatchers.main) {

        val requestData = WHRHistoryModel(Gson().toJson(WHRHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), WHRHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

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

    fun getBloodPressureHistory() = viewModelScope.launch(dispatchers.main) {

        val requestData = BloodPressureHistoryModel(Gson().toJson(BloodPressureHistoryModel.JSONDataRequest(
                    personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), BloodPressureHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

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


    fun navigateParam(action: NavDirections) {
        navigate(action)
    }


}