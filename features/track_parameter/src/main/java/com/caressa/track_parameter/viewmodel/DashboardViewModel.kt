package com.caressa.track_parameter.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.caressa.common.base.BaseViewModel
import com.caressa.track_parameter.R
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.caressa.track_parameter.ui.DashboardFragmentDirections
import com.caressa.track_parameter.util.TrackParameterHelper
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.StringBuilder
import java.util.*

@SuppressLint("StaticFieldLeak")
class DashboardViewModel (
    private val dispatchers: AppDispatchers,
    private val useCase: ParameterManagementUseCase,
    private val sharedPref: SharedPreferences,
    val context: Context): BaseViewModel(){

    private val personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!

    //val listAllProfiles: MutableLiveData<List<ParameterProfile>> = MutableLiveData()
    var listAllProfiles = MutableLiveData<List<ParameterProfile>>()

    val selectParamLiveData: MutableLiveData<List<ParameterListModel.SelectedParameter>> = MutableLiveData()
    val parameterLiveData: MutableLiveData<List<TrackParameterMaster.Parameter>> = MutableLiveData()
    val dashboardLiveData: MutableLiveData<List<DashboardObservationData>> = MutableLiveData()

    var paramPreferenceUserSource: LiveData<Resource<ParameterPreferenceModel.Response>> = MutableLiveData()
    private val _paramPreferenceList = MediatorLiveData<ParameterPreferenceModel.Response>()
    val paramPreferenceList: LiveData<ParameterPreferenceModel.Response> get() = _paramPreferenceList

    var listHistoryWithLatestRecord = MutableLiveData<List<DashboardParamGridModel>>()

    fun listHistoryWithLatestRecord( fitnessData : FitnessData ) = viewModelScope.launch {
        withContext(dispatchers.io){
            try {
                val steps = fitnessData.stepsCount
                val calories = fitnessData.calories
                var systolic = -1
                var diastolic = -1
                var color = R.color.hlmt_warm_grey
                val list: ArrayList<DashboardParamGridModel> = ArrayList()

                val paramWeight = useCase.invokeListHistoryWithLatestRecord(personId,"BMI","WEIGHT")
                val paramWaist = useCase.invokeListHistoryWithLatestRecord(personId,"WHR","WAIST")
                val paramBMI = useCase.invokeListHistoryWithLatestRecord(personId,"BMI","BMI")
                val paramWhr = useCase.invokeListHistoryWithLatestRecord(personId,"WHR","WHR")
                val paramBp = useCase.invokeListHistoryWithLatestRecord(personId,"BLOODPRESSURE","")
                val paramPulse = useCase.invokeListHistoryWithLatestRecord(personId,"BLOODPRESSURE","BP_PULSE")
                val paramSugar = useCase.invokeListHistoryWithLatestRecord(personId,"DIABETIC","DIAB_RA")
                val paramChol = useCase.invokeListHistoryWithLatestRecord(personId,"LIPID","CHOL_TOTAL")
                val paramHb = useCase.invokeListHistoryWithLatestRecord(personId,"HEMOGRAM","HAEMOGLOBIN")

                if ( !paramBp.isNullOrEmpty() ) {
                    for ( i in paramBp.indices) {
                        if ( paramBp[i].parameterCode.equals("BP_SYS",ignoreCase = true) ) {
                            systolic = i
                        } else if ( paramBp[i].parameterCode.equals("BP_DIA",ignoreCase = true) ) {
                            diastolic = i
                        }
                    }
                }

                if ( !paramWeight.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramWeight[0].observation!!,paramWeight[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_weight,color,context.resources.getString(R.string.weight), paramWeight[0].value!!.toString(), "WEIGHT"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_weight,R.color.hlmt_warm_grey,context.resources.getString(R.string.weight), " -- ", "WEIGHT"))
                }

                if ( !paramWaist.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramWaist[0].observation!!,paramWeight[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_waist,color,context.resources.getString(R.string.waist), paramWaist[0].value!!.toString(), "WAIST"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_waist,R.color.hlmt_warm_grey,context.resources.getString(R.string.waist), " -- ", "WAIST"))
                }

                if ( !paramBMI.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramBMI[0].observation!!,paramBMI[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_profile_bmi,color,context.resources.getString(R.string.bmi), paramBMI[0].value!!.toString(), "BMI"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_profile_bmi,R.color.hlmt_warm_grey,context.resources.getString(R.string.bmi), " -- ", "BMI"))
                }

                if ( !paramWhr.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramWhr[0].observation!!,paramWhr[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_profile_whr,color,context.resources.getString(R.string.whr), paramWhr[0].value!!.toString(), "WHR"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_profile_whr,R.color.hlmt_warm_grey,context.resources.getString(R.string.whr), " -- ", "WHR"))
                }


                if (systolic != -1 && diastolic != -1) {
                    color = TrackParameterHelper.getObservationColor(TrackParameterHelper.getBPObservation(systolic, diastolic),"")
                    if ( !Utilities.isNullOrEmpty( paramBp[systolic].value.toString())
                        && !Utilities.isNullOrEmpty( paramBp[diastolic].value.toString()) ) {
                        list.add(DashboardParamGridModel(R.drawable.img_profile_bp,color,context.resources.getString(R.string.bp),paramBp[systolic].value!!.toDouble().toInt().toString() + " / " + paramBp[diastolic].value!!.toDouble().toInt().toString(), "BP"))
                    } else {
                        list.add(DashboardParamGridModel(R.drawable.img_profile_bp,R.color.hlmt_warm_grey,context.resources.getString(R.string.bp), " -- ", "BP"))
                    }
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_profile_bp,R.color.hlmt_warm_grey,context.resources.getString(R.string.bp), " -- ", "BP"))
                }

                if ( !paramPulse.isNullOrEmpty() ) {
                    val pulseValue = paramPulse[0].value!!.toString()
                    color = TrackParameterHelper.getObservationColor(TrackParameterHelper.getPulseObservation(pulseValue), "")
                    list.add(DashboardParamGridModel(R.drawable.img_heart_risk,color,context.resources.getString(R.string.pulse), pulseValue, "PULSE"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_heart_risk,R.color.hlmt_warm_grey,context.resources.getString(R.string.pulse), " -- ", "PULSE"))
                }

                if ( !paramSugar.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramSugar[0].observation!!,paramSugar[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_profile_blood_sugar,color,context.resources.getString(R.string.bs), paramSugar[0].value!!.toString(), "SUGAR"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_profile_blood_sugar,R.color.hlmt_warm_grey,context.resources.getString(R.string.bs), " -- ", "SUGAR"))
                }

                if ( !paramChol.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramChol[0].observation!!,paramChol[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_cholesteroal,color,context.resources.getString(R.string.cholesterol), paramChol[0].value!!.toString(), "CHOL"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_cholesteroal,R.color.hlmt_warm_grey,context.resources.getString(R.string.cholesterol), " -- ", "CHOL"))
                }

                if ( !paramHb.isNullOrEmpty() ) {
                    color = TrackParameterHelper.getObservationColor(paramHb[0].observation!!,paramHb[0].profileCode!!)
                    list.add(DashboardParamGridModel(R.drawable.img_hemoglobin,color,context.resources.getString(R.string.hemoglobin), paramHb[0].value!!.toString(), "HEMOGLOBIN"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_hemoglobin,R.color.hlmt_warm_grey,context.resources.getString(R.string.hemoglobin), " -- ", "HEMOGLOBIN"))
                }

                if ( !Utilities.isNullOrEmptyOrZero(steps) ) {
                    list.add(DashboardParamGridModel(R.drawable.img_step,R.color.vivant_dark_sky_blue,context.resources.getString(R.string.steps), steps, "STEPS"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_step,R.color.hlmt_warm_grey,context.resources.getString(R.string.steps), "0", "STEPS"))
                }

                if ( !Utilities.isNullOrEmptyOrZero(calories) ) {
                    list.add(DashboardParamGridModel(R.drawable.img_calories,R.color.vivant_pumpkin_orange,context.resources.getString(R.string.calories), calories, "CAL"))
                } else {
                    list.add(DashboardParamGridModel(R.drawable.img_calories,R.color.hlmt_warm_grey,context.resources.getString(R.string.calories), "0", "CAL"))
                }

                list.add(DashboardParamGridModel(R.drawable.add,R.color.white,context.resources.getString(R.string.add_more), "", "ADD"))

                listHistoryWithLatestRecord.postValue(list)

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSelectParameterList() = viewModelScope.launch {
        withContext(dispatchers.io){
            selectParamLiveData.postValue(useCase.invokeGetSelectParamList(sharedPref.getString(PreferenceConstants.SELECTION_PARAM,"")!!))
        }
    }

    fun showProgressBar()  {
        _progressBar.value = Event("")
    }

    fun getAllProfileCodes() = viewModelScope.launch   {
        withContext(dispatchers.io) {
            listAllProfiles.postValue(useCase.invokeGetAllProfileCodes())
        }
    }

    fun getAllProfilesWithRecentSelectionList() = viewModelScope.launch   {
        _progressBar.value = Event("Profiles List..")
        withContext(dispatchers.io) {
            listAllProfiles.postValue(useCase.invokeGetAllProfilesWithRecentSelectionList(personId))
        }
    }

    fun updateSelection( adapterPosition: Int ) {
        selectParamLiveData.value?.get(adapterPosition)?.selectionStatus = !selectParamLiveData.value?.get(adapterPosition)?.selectionStatus!!
        selectParamLiveData.value = selectParamLiveData.value
    }

    fun saveSelectedParameter( dataList: MutableList<ParameterProfile> ) {
        val selectedParameter = StringBuilder("")
        for (item in dataList) {
            if (item.isSelection) {
                selectedParameter.append(item.profileCode+"|")
            }
        }
        Timber.i("SelectedParam--->$selectedParameter")
        sharedPref.edit().putString(PreferenceConstants.SELECTION_PARAM,selectedParameter.toString()).apply()
    }

    fun getTrackParameters() = viewModelScope.launch {
        withContext(dispatchers.io){
            parameterLiveData.postValue(useCase.invokeGetDBParamList())
        }
    }

    fun getDashboardParamList() = viewModelScope.launch {
        withContext(dispatchers.io){
            Timber.i("DashboardData=> "+useCase.invokeGetDashboardData())
            dashboardLiveData.postValue(useCase.invokeGetDashboardData())
        }
    }

    fun getPreferenceList() = viewModelScope.launch(dispatchers.main) {

        val requestData = ParameterPreferenceModel(
            Gson().toJson(
                ParameterPreferenceModel.JSONDataRequest(
                    personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!), ParameterPreferenceModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

        _paramPreferenceList.removeSource(paramPreferenceUserSource)
        withContext(dispatchers.io){ paramPreferenceUserSource = useCase.invokeParameterPreference(data = requestData)}
        _paramPreferenceList.addSource(paramPreferenceUserSource){
            _paramPreferenceList.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
            }

            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun navigateParam(action: NavDirections) {
        navigate(action)
    }

    fun openParameterDetails(item: TrackParamDashboardDataSet) {
        navigate(DashboardFragmentDirections.actionDashboardFragmentToParameterDetailFragment(item.profileName!!,item.profileCode!!))
    }
}