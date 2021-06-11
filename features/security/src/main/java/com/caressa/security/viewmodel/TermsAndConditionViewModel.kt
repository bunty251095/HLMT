package com.caressa.security.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.utils.Event
import com.caressa.model.security.TermsConditionsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.domain.UserManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TermsAndConditionViewModel(private val userManagementUseCase: UserManagementUseCase,
                                 private val dispatchers: AppDispatchers) : BaseViewModel() {

    var userSourceTerms: LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> = MutableLiveData()
    val _termsConditions = MediatorLiveData<TermsConditionsModel.TermsConditionsResponse>()
    val termsConditions : LiveData<TermsConditionsModel.TermsConditionsResponse> get() = _termsConditions

    fun getTermsAndConditionsData( forceRefresh: Boolean ) = viewModelScope.launch(dispatchers.main) {

        val requestData = TermsConditionsModel(Gson().toJson(TermsConditionsModel.JSONDataRequest(
                    applicationCode = Configuration.ApplicationCode, partnerCode = Configuration.PartnerCode),
                TermsConditionsModel.JSONDataRequest::class.java))
        _progressBar.value = Event("Loading")
        _termsConditions.removeSource(userSourceTerms)
        withContext(dispatchers.io) { userSourceTerms = userManagementUseCase.invokeTermsCondition(isForceRefresh = forceRefresh, data = requestData) }
        _termsConditions.addSource(userSourceTerms) {
            _termsConditions.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
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

}