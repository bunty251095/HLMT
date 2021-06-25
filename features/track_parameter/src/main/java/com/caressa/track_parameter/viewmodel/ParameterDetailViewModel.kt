package com.caressa.track_parameter.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParameterData
import com.caressa.model.parameter.SaveParameterModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ParameterDetailViewModel(
    private val dispatchers: AppDispatchers,
    private val sharedPref: SharedPreferences,
    private val useCase: ParameterManagementUseCase
) : BaseViewModel() {

    val paramList: MutableLiveData<List<TrackParameterMaster.Parameter>> by lazy {
        MutableLiveData<List<TrackParameterMaster.Parameter>>()
    }

    val paramHistory: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    var saveParamUserSource: LiveData<Resource<SaveParameterModel.Response>> = MutableLiveData()
    private val _saveParam= MediatorLiveData<SaveParameterModel.Response>()
    val saveParam: LiveData<SaveParameterModel.Response> get() = _saveParam

    fun getParameterList(profileCode: String) = viewModelScope.launch {
        withContext(dispatchers.main) {
            Timber.i("TrackParamList=> "+useCase.invokeParameterListBaseOnCode(profileCode)?.size)
            paramList.postValue(useCase.invokeParameterListBaseOnCode(profileCode))
        }
    }

    fun getParameterHistory(parameterCode: String) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            paramHistory.postValue(useCase.invokeParameterHisBaseOnCode(parameterCode, sharedPref.getString(PreferenceConstants.PERSONID,"0")!!))
        }
    }

    fun saveParameter(parameterDataList: ArrayList<ParameterData>, recordDate: String) = viewModelScope.launch(dispatchers.main) {

        val recordList = arrayListOf<SaveParameterModel.Record>()
        val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!

        for (item in parameterDataList){
            if(!item.value.isNullOrEmpty()) {
                val record = SaveParameterModel.Record(
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    personId,
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    item.parameterCode,
                    personId,
                    item.profileCode,
                    recordDate,
                    item.unit,
                    item.value
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

            _saveParam.removeSource(saveParamUserSource)
            withContext(dispatchers.io) {
                saveParamUserSource = useCase.invokeSaveParameter(data = requestData)
            }
            _saveParam.addSource(saveParamUserSource) {
                _saveParam.value = it.data
                if (it.status == Resource.Status.SUCCESS) {
                    snackMessage("Parameter is successfully updated")
                }

                if (it.status == Resource.Status.ERROR) {
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

    fun showMessage(msg: String){
        _toastMessage.value = Event(msg)
    }


}