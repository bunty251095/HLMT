package com.caressa.home.viewmodel

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Utilities
import com.caressa.home.common.DataHandler
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.model.home.RefreshTokenModel
import com.caressa.model.home.UpdateLanguageProfileModel
import com.caressa.model.security.TermsConditionsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

@SuppressLint("StaticFieldLeak")
class SettingsViewModel(private val homeManagementUseCase: HomeManagementUseCase,
                        private val dispatchers: AppDispatchers,
                        private val dataHandler: DataHandler,
                        private val sharedPref: SharedPreferences,
                        val context: Context) : BaseViewModel() {

    var languageListLiveData = MutableLiveData<List<DataHandler.LanguageModel>>()
    val settingsOptionListData = MutableLiveData<List<DataHandler.Option>>()

    var personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!
    var accountID = sharedPref.getString(PreferenceConstants.ACCOUNTID, "")!!
    //var authToken = sharedPref.getString(PreferenceConstants.TOKEN, "")!!

    var updateProfileSource: LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> = MutableLiveData()
    val _updateProfile = MediatorLiveData<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>()
    val updateProfile: LiveData<UpdateLanguageProfileModel.UpdateLanguageProfileResponse> get() = _updateProfile

    var refreshTokenSource: LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> = MutableLiveData()
    val _refreshToken = MediatorLiveData<RefreshTokenModel.RefreshTokenResponse>()
    val refreshToken: LiveData<RefreshTokenModel.RefreshTokenResponse> get() = _refreshToken

    fun callUpdateProfileListApi(languageCode: String) = viewModelScope.launch(dispatchers.main) {

        val requestData = UpdateLanguageProfileModel(
            Gson().toJson(UpdateLanguageProfileModel.JSONDataRequest(details = UpdateLanguageProfileModel.Details(personId=
            personId,languageCode = languageCode)
            ), UpdateLanguageProfileModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN, "")!!)

        _updateProfile.removeSource(updateProfileSource)
        _progressBar.value = Event("")
        withContext(dispatchers.io) {
            updateProfileSource = homeManagementUseCase.invokeUpdateLanguageSettings(isForceRefresh = true, data = requestData)
        }
        _updateProfile.addSource(updateProfileSource) {
            _updateProfile.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Timber.i("UpdateProfile----->" + it)
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

    fun callRefreshTokenApi() = viewModelScope.launch(dispatchers.main) {

        val requestData = RefreshTokenModel(
            Gson().toJson(RefreshTokenModel.JSONDataRequest(accountID=accountID, personID= personId),
            RefreshTokenModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN, "")!!)
        _progressBar.value = Event("")
        _refreshToken.removeSource(refreshTokenSource)
        withContext(dispatchers.io) {
            refreshTokenSource = homeManagementUseCase.invokeRefreshTokenResponse(isForceRefresh = true, data = requestData)
        }
        _refreshToken.addSource(refreshTokenSource) {
            _refreshToken.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {

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

    fun getLanguageList() = viewModelScope.launch {
        withContext(dispatchers.io) {
            var list: ArrayList<DataHandler.LanguageModel> = arrayListOf()
            if(LocaleHelper.getLanguage(context).equals("en")) {
                list.add(DataHandler.LanguageModel("English", "en",true))
                list.add(DataHandler.LanguageModel("Malaysian","my",false))
            }else{
                list.add(DataHandler.LanguageModel("English", "en",false))
                list.add(DataHandler.LanguageModel("Malaysian","my",true))
            }
            languageListLiveData.postValue(list)
        }
    }

/*    fun getSettingsOptionList() {
        settingsOptionListData.postValue( dataHandler.getSettingsOptionListData() )
    }*/

/*    fun getSettingsOptionList() {
        if (isSelfUser()) {
            settingsOptionListData.postValue( dataHandler.getSettingsOptionListData() )
        } else {
            settingsOptionListData.postValue( dataHandler.getSwitchProfileSettingsOptionListData() )
        }
    }*/

    fun isSelfUser(): Boolean {
        val personId = sharedPref.getString(PreferenceConstants.PERSONID, "0")
        val adminPersonId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID, "0")
        Timber.e("PersonId--->$personId")
        Timber.e("AdminPersonId--->$adminPersonId")
        var isSelfUser = false
        if ( !Utilities.isNullOrEmptyOrZero(personId)
            && !Utilities.isNullOrEmptyOrZero(adminPersonId)
            && personId == adminPersonId ) {
            isSelfUser = true
        }
        return isSelfUser
    }

/*    fun isPushNotificationEnabled(): Boolean {
        return sharedPref.getBoolean(PreferenceConstants.ENABLE_PUSH_NOTIFICATION, true)
    }

    fun setPushNotificationEnableOrDisable( isEnabled : Boolean ) {
        sharedPref.edit().putBoolean(PreferenceConstants.ENABLE_PUSH_NOTIFICATION, isEnabled).apply()
    }*/

    fun updateUserPreference(unit: String?) {
        if (!unit.isNullOrEmpty()) {
            when (unit.toLowerCase()) {
                "cm" -> {
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "cm").apply()
                }
                "kg" -> {
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "kg").apply()
                }
                "lbs" -> {
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "lib")
                        .apply()
                }
                "feet/inch" -> {
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                        .apply()
                }
                "inch" -> {
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                        .apply()
                }
            }
        }
    }


    fun getHeightPreference():String{
        return sharedPref.getString(PreferenceConstants.HEIGHT_PREFERENCE,"feet")!!
    }
    fun getWeightPreference():String{
        return sharedPref.getString(PreferenceConstants.WEIGHT_PREFERENCE,"kg")!!
    }

    fun refreshToken( token: String) {
        sharedPref.edit().putString(PreferenceConstants.TOKEN, token).apply()
        //authToken = token
    }

    fun navigateToSettingsActivity() {

    }

    fun navigateToHomeScreen() {

    }


}