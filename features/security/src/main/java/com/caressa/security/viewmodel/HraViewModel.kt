package com.caressa.security.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.model.hra.HraHistoryModel
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.model.hra.HraStartModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.domain.HraManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HraViewModel(private val hraManagementUseCase: HraManagementUseCase,
    private val dispatchers: AppDispatchers, private val sharedPref: SharedPreferences) : BaseViewModel() {

    //val personId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,"")!!
    private val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!

    var hraStartSource: LiveData<Resource<HraStartModel.HraStartResponse>> = MutableLiveData()
    val _hraStart = MediatorLiveData<HraStartModel.HraStartResponse>()
    val hraStart: LiveData<HraStartModel.HraStartResponse> get() = _hraStart

    var MedicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    val _MedicalProfileSummary = MediatorLiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>()
    val medicalProfileSummary: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> get() = _MedicalProfileSummary

    var hraHistorySource: LiveData<Resource<HraHistoryModel.HRAHistoryResponse>> = MutableLiveData()
    val _hraHistorySummary = MediatorLiveData<Resource<HraHistoryModel.HRAHistoryResponse>>()
    val hraHistorySummary: LiveData<Resource<HraHistoryModel.HRAHistoryResponse>> get() = _hraHistorySummary

    fun getLoginStatus(): Boolean{
        val isLogin = sharedPref.getBoolean(PreferenceConstants.IS_LOGIN,false)
        if (isLogin){
            //val adminPersonId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,"")!!
            //sharedPref.edit().putString(PreferenceConstants.PERSONID, adminPersonId).apply()
        }
        return isLogin
    }

    fun getHraHistory() = viewModelScope.launch(dispatchers.main){

        val requestData = HraHistoryModel(Gson().toJson(HraHistoryModel.JSONDataRequest(
            PersonID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!),
            HraHistoryModel.JSONDataRequest::class.java), authToken)

        _hraHistorySummary.removeSource(hraHistorySource)
        withContext(dispatchers.io){
            hraHistorySource = hraManagementUseCase.invokeHRAHistory(isForceRefresh = true, data = requestData)
        }
        _hraHistorySummary.addSource(hraHistorySource){
            _hraHistorySummary.value = it
            if (it.status == Resource.Status.SUCCESS) {
                // _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                // _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun getMedicalProfileSummary(forceRefresh: Boolean) = viewModelScope.launch(dispatchers.main) {

        val requestData = HraMedicalProfileSummaryModel(Gson().toJson(
            HraMedicalProfileSummaryModel.JSONDataRequest(PersonID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!),
                HraMedicalProfileSummaryModel.JSONDataRequest::class.java), authToken)

        // _progressBar.value = Event("Fetching Medical Summary Details.....")
        _MedicalProfileSummary.removeSource(MedicalProfileSummarySource)
        withContext(dispatchers.io) {
            MedicalProfileSummarySource =
                hraManagementUseCase.invokeMedicalProfileSummary(isForceRefresh = forceRefresh, data = requestData,personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!)
        }
        _MedicalProfileSummary.addSource(MedicalProfileSummarySource) {
            _MedicalProfileSummary.value = it

            if (it.status == Resource.Status.SUCCESS) {
                // _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                // _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

/*    fun startHra(forceRefresh: Boolean) = viewModelScope.launch {

        val requestData = HraStartModel(
            Gson().toJson(
                HraStartModel.JSONDataRequest(personID = personId),
                HraStartModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Starting HRA.....")
        _hraStart.removeSource(hraStartSource)
        withContext(dispatchers.io) { hraStartSource = hraManagementUseCase.invokeStartHra(forceRefresh,requestData,personId) }
        _hraStart.addSource(hraStartSource) {
            _hraStart.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it != null ) {
//                    val startHraResponce = it
//                    val ID = startHraResponce.data!!.template.ID
//                    val GENDER = startHraResponce.data!!.template.gender
//                    val MaxScore = startHraResponce.data!!.template.maxScore
//                    saveHraStartDetails(ID!!,GENDER!!,MaxScore!!)
                }
//                navigate(IntroductionFragmentDirections.actionIntroductionFragmentToHraQuestionsActivity())
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }*/

}