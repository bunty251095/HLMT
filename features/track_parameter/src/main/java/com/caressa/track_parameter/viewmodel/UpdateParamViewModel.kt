package com.caressa.track_parameter.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.FirebaseHelper
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UpdateParamViewModel(
    private val useCase: ParameterManagementUseCase,
    private val dispatchers: AppDispatchers,
    private val sharedPref: SharedPreferences
) : BaseViewModel() {

    val selectParamLiveData: MutableLiveData<List<ParameterListModel.SelectedParameter>> = MutableLiveData()
    val parameterLiveData: MutableLiveData<List<TrackParameterMaster.Parameter>> = MutableLiveData()

    var allParamList: List<TrackParameterMaster.Parameter> = listOf()

    var paramSelectedSource: List<ParameterListModel.SelectedParameter> = listOf()
    private val _paramSelectedList = MediatorLiveData<ParameterListModel.SelectedParameter>()
    val paramSelectedList: LiveData<ParameterListModel.SelectedParameter> get() = _paramSelectedList

    private val _selectedParam = MediatorLiveData<List<ParameterListModel.SelectedParameter>>()
    val selectedParameter: LiveData<List<ParameterListModel.SelectedParameter>> get() = _selectedParam

    private val _inputParamList = MediatorLiveData<List<ParameterListModel.InputParameterModel>>()
    val inputParamList: LiveData<List<ParameterListModel.InputParameterModel>> get() = _inputParamList
//    val inputParamListLiveData: MutableLiveData<List<ParameterListModel.InputParameterModel>> = MutableLiveData()

    var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam= MediatorLiveData<SaveParameterModel.Response>()
    val saveParam: LiveData<SaveParameterModel.Response> get() = _saveParam

    fun getTrackParameters() = viewModelScope.launch {
        withContext(dispatchers.io){
            if (allParamList.size <= 0) {
                val data = useCase.invokeGetDBParamList()
                allParamList = data!!
                Timber.i("Size: " + data?.size)
                Timber.i("Data :: " + data)
            }
            parameterLiveData.postValue(allParamList)
        }
    }

//    fun getParameterByProfileCode(profileCode: String) = viewModelScope.launch(dispatchers.main) {
//        withContext(dispatchers.io){
//            val data = useCase.invokeParameterListBaseOnCode(profileCode)
////            Timber.i("Data :: " + data)
//            var filterData = getFilterData(data)
////            Timber.i("Data :: " + filterData)
//            _inputParamList.postValue(filterData)
//        }
//
//    }

    fun getParameterByProfileCodeAndDate(profileCode: String,serverDate:String) = viewModelScope.launch(dispatchers.main) {
        Timber.i("Server Date :: " + serverDate)
        withContext(dispatchers.io){
            if(serverDate.equals(DateHelper.currentDateAsStringddMMMyyyy) && profileCode.equals("BMI",true)) {
                val data = useCase.invokeGetLatestParametersData(
                    profileCode,
                    sharedPref.getString(PreferenceConstants.PERSONID, "0")!!
                )
                Timber.i("Data :: " + data)
                _inputParamList.postValue(data)
            }else{
                val data = useCase.invokeGetParameterDataBasedOnRecordDate(
                    profileCode,
                    sharedPref.getString(PreferenceConstants.PERSONID, "0")!!,
                    serverDate
                )
                Timber.i("Data :: " + data)
                _inputParamList.postValue(data)
            }
        }

    }

    private fun getFilterData(data: List<TrackParameterMaster.Parameter>?): List<ParameterListModel.InputParameterModel> {
        val list:MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        for(item in data!!){
            Timber.i("DataItem=> "+item)
            val dataItem = ParameterListModel.InputParameterModel(parameterCode = item.code,parameterType = item.parameterType,description = item.description,profileCode = item.profileCode,profileName = item.profileName,parameterUnit = item.unit
            ,minPermissibleValue = if(item.minPermissibleValue.isNullOrEmpty())"" else item.minPermissibleValue,maxPermissibleValue = if(item.maxPermissibleValue.isNullOrEmpty()) "" else item.maxPermissibleValue,parameterTextVal = "",parameterVal = "")
            list.add(dataItem)
        }
        return list
    }

    fun refreshSelectedParamList(showAllProfile: String) = viewModelScope.launch(dispatchers.main){
        withContext(dispatchers.io){
            _selectedParam.postValue(useCase.invokeGetSelectParamList(sharedPref.getString(PreferenceConstants.SELECTION_PARAM,"")!!,showAllProfile))
        }
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
    }

    fun saveParameter(parameterDataList: ArrayList<ParameterListModel.InputParameterModel>, recordDate: String) = viewModelScope.launch(dispatchers.main) {

        val recordList = arrayListOf<SaveParameterModel.Record>()
        val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!

        for (item in parameterDataList){
            if(!item.parameterVal.isNullOrEmpty()) {
                val record = SaveParameterModel.Record(
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    item.parameterCode!!,
                    personId,
                    item.profileCode!!,
                    recordDate,
                    item.parameterUnit!!,
                    item.parameterVal!!,
                    ""
                )
                recordList.add(record)
            }else if(!item.parameterTextVal.isNullOrEmpty()){
                val record = SaveParameterModel.Record(
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    item.parameterCode!!,
                    personId,
                    item.profileCode!!,
                    recordDate,
                    "",
                    "",
                    item.parameterTextVal!!
                )
                recordList.add(record)

            }
        }

        if(!recordList.isEmpty()) {
            val requestData = SaveParameterModel(
                Gson().toJson(
                    SaveParameterModel.JSONDataRequest(recordList),
                    SaveParameterModel.JSONDataRequest::class.java
                ), sharedPref.getString(PreferenceConstants.TOKEN, "")!!
            )
            _progressBar.value = Event("Saving Parameter values...")
            _saveParam.removeSource(saveParamUserSource)
            withContext(dispatchers.io) {
                saveParamUserSource = useCase.invokeSaveParameter(data = requestData)
            }
            _saveParam.addSource(saveParamUserSource) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                _saveParam.value = it.data
                if (it.status == Resource.Status.SUCCESS) {
                    toastMessage("Parameter is successfully updated")
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEALTH_PARAM_UPLOAD_EVENT)
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
        }else{
            _snackbarMessage.value = Event("No Data ")
        }
    }

    fun showMessage(validationMessage: String) {
        _snackbarMessage.value = Event(validationMessage)
    }


}