package com.caressa.track_parameter.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.PreferenceConstants
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParameterListModel
import com.caressa.model.parameter.ParameterProfile
import com.caressa.model.parameter.ParentProfileModel
import com.caressa.repository.AppDispatchers
import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.caressa.track_parameter.ui.RevHistoryFragmentDirections
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HistoryViewModel(
    private val useCase: ParameterManagementUseCase,
    private val dispatchers: AppDispatchers,
    private val sharedPref: SharedPreferences) : BaseViewModel() {

    private val personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!

    var listAllProfiles = MutableLiveData<List<ParameterProfile>>()

    var paramSelectedSource: List<ParameterListModel.SelectedParameter> = listOf()
    private val _paramSelectedList = MediatorLiveData<ParameterListModel.SelectedParameter>()
    val paramSelectedList: LiveData<ParameterListModel.SelectedParameter> get() = _paramSelectedList

    private val _selectedParam = MediatorLiveData<List<ParameterListModel.SelectedParameter>>()
    val selectedParameter: LiveData<List<ParameterListModel.SelectedParameter>> get() = _selectedParam

    private val _savedParamList = MediatorLiveData<List<TrackParameterMaster.History>>()
    val savedParamList: LiveData<List<TrackParameterMaster.History>> get() = _savedParamList

    private val _observationList = MediatorLiveData<List<TrackParameterMaster.TrackParameterRanges>>()
    val observationList: LiveData<List<TrackParameterMaster.TrackParameterRanges>> get() = _observationList

    val paramHistory: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    val paramBPHistory: MutableLiveData<Map<String,List<TrackParameterMaster.History>>> by lazy {
        MutableLiveData<Map<String,List<TrackParameterMaster.History>>>()
    }

    val spinnerHistoryLiveData: MutableLiveData<List<TrackParameterMaster.History>> by lazy {
        MutableLiveData<List<TrackParameterMaster.History>>()
    }

    fun getParameterHistory(parameterCode: String) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            val data = useCase.invokeParameterHisBaseOnCode(parameterCode, sharedPref.getString(PreferenceConstants.PERSONID,"0")!!)
            if (data!!.isNotEmpty())
                paramHistory.postValue(data.reversed())
            else
                paramHistory.postValue(data)
        }
    }

    var listHistoryByProfileCodeAndMonthYear =
        MutableLiveData<ArrayList<TrackParameterMaster.History>>()
    var parentDataList = MutableLiveData<List<ParentProfileModel>>()

    fun getBPParameterHistory() = viewModelScope.launch(dispatchers.main) {
        var bpList:MutableMap<String,List<TrackParameterMaster.History>>  = mutableMapOf()
        withContext(dispatchers.io) {
            val sysList = useCase.invokeParameterHisBaseOnCode("BP_SYS", sharedPref.getString(PreferenceConstants.PERSONID,"0")!!)
            val diaList = useCase.invokeParameterHisBaseOnCode("BP_DIA", sharedPref.getString(PreferenceConstants.PERSONID,"0")!!)
            if(sysList!!.isNotEmpty() && diaList!!.isNotEmpty()) {
                bpList.put("BP_SYS", sysList.reversed())
                bpList.put("BP_DIA", diaList.reversed())
            }
            paramBPHistory.postValue(bpList)
        }
    }

    fun getSpinnerData(profileCode: String) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            spinnerHistoryLiveData.postValue(
                useCase.invokeLatestParametersHisBaseOnProfileCode(
                    profileCode, sharedPref.getString(
                        PreferenceConstants.PERSONID, ""
                    )!!
                )
            )

        }
    }

    fun getParentDataList(mapProfilesAll:MutableList<ParameterProfile>,month: String,year: String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            try {
                val parentProfilesList = ArrayList<ParentProfileModel>()
                val m = "%$month%"
                val y = "%$year%"
                Timber.i("month,year--->$month,$year")
                for (i in mapProfilesAll.indices) {
                    val paramHistoryList : ArrayList<TrackParameterMaster.History> = arrayListOf()
                    paramHistoryList.addAll(useCase.invokeListHistoryByProfileCodeAndMonthYear(personId,mapProfilesAll[i].profileCode,m,y))
                    if (paramHistoryList.isNotEmpty()) {
                        val parentMap = ParentProfileModel()
                        parentMap.profileCode = mapProfilesAll[i].profileCode
                        parentMap.profileName = mapProfilesAll[i].profileName
                        parentMap.childParameterList = paramHistoryList
                        parentProfilesList.add(parentMap)
                    }
                }
                parentDataList.postValue(parentProfilesList)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllProfileCodes() = viewModelScope.launch(dispatchers.main)  {
        withContext(dispatchers.io) {
            listAllProfiles.postValue(useCase.invokeGetAllProfileCodes())
        }
    }

    fun parameterHistoryByProfileCode(profileCode: String) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io){
            val data = useCase.invokeLatestParametersHisBaseOnProfileCode(profileCode,sharedPref.getString(
                PreferenceConstants.PERSONID,"")!!)

            _savedParamList.postValue(data)
        }
    }

    fun parameterObservationListByParameterCode(parameterCode: String) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io){
            val data = useCase.invokeGetParameterObservationList(parameterCode,sharedPref.getString(
                PreferenceConstants.PERSONID,"")!!)
            _observationList.postValue(data)
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

    fun refreshSelectedParamList()= viewModelScope.launch(dispatchers.main){
        withContext(dispatchers.io){
            _selectedParam.postValue(useCase.invokeGetSelectParamList(sharedPref.getString(
                PreferenceConstants.SELECTION_PARAM,"")!!))
        }
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
    }

    fun goToDetailHistory() {
        navigateParam(RevHistoryFragmentDirections.actionHistoryFragmentToDetailHistoryFragment())
    }

    fun updateSpinner() {

    }

}