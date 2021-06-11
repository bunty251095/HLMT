package com.caressa.security.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.Validation
import com.caressa.model.entity.Users
import com.caressa.model.security.GenerateOtpModel
import com.caressa.model.security.PhoneExistsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.ui.LoginWithOtpFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class LoginWithOtpViewModel (private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                             private val sharedPref: SharedPreferences , val context: Context) : BaseViewModel() {

    var userSource: LiveData<Resource<Users>> = MutableLiveData()
    private val _user = MediatorLiveData<Users>()
    val user: LiveData<Users> get() = _user

    var userSourceOTP: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    private val _otpLoginData = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val otpLoginData: LiveData<PhoneExistsModel.IsExistResponse> get() = _otpLoginData

    var userSourceGenerateOTP: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpGenerateData = MediatorLiveData<GenerateOtpModel.GenerateOTPResponse>()
    val otpGenerateData: LiveData<GenerateOtpModel.GenerateOTPResponse> get() = _otpGenerateData

    private val _otpScreenData = MediatorLiveData<JSONObject>()
    val otpScreenData: LiveData<JSONObject> get() = _otpScreenData

    private val _isLoading = MutableLiveData<Resource.Status>()
    val isLoading: LiveData<Resource.Status> get() = _isLoading

    fun btnNextClick(emailOrPhone: String) = viewModelScope.launch(dispatchers.main) {

        if ( !emailOrPhone.isEmpty() && Validation.isValidEmail(emailOrPhone)) {
            calIGenerateOTPApi(emailOrPhone,"")
        }else if( !emailOrPhone.isEmpty() && Validation.isValidPhoneNumber(emailOrPhone)){
            val requestData = PhoneExistsModel(
                Gson().toJson(
                    PhoneExistsModel.JSONDataRequest(
                        primaryPhone = emailOrPhone), PhoneExistsModel.JSONDataRequest::class.java))

            _otpLoginData.removeSource(userSourceOTP)
            withContext(dispatchers.io) { userSourceOTP = userManagementUseCase.invokePhoneExist(true, requestData) }
            _otpLoginData.addSource(userSourceOTP) {
                _otpLoginData.value = it.data
                //_isLoading.value = it.status

                if (it.status == Resource.Status.SUCCESS) {
                    //toastMessage("Login with OTP Successful")
                    if (it.data?.isExist.equals("true", true)) {
                        if (it.data?.account != null) {
                            if (it.data?.account?.emailAddress != null) {
                                calIGenerateOTPApi(it.data?.account?.emailAddress!!, emailOrPhone)
                            }
                        }
                    } else if (it.data?.isExist.equals("false", true)) {
                        toastMessage(context.resources.getString(R.string.ERROR_PHONE_NOT_REGISTERED))
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    toastMessage(it.errorMessage)
                }
            }
        } else {
            toastMessage(context.resources.getString(R.string.ERROR_INVALID_EMAIL_PHONE))
        }
    }

    fun calIGenerateOTPApi(emailStr: String, phoneNumber: String) = viewModelScope.launch(dispatchers.main) {
        Timber.i("Generate OTP Called")
        val requestData = GenerateOtpModel(
            Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        loginName = phoneNumber,
                        emailAddress = emailStr,
                        primaryPhone = phoneNumber),
                    from = "102", message = "Generating One Time Password (OTP)"), GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Generating OTP...")
        _otpGenerateData.removeSource(userSourceGenerateOTP)
        withContext(dispatchers.io) {
            userSourceGenerateOTP = userManagementUseCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(userSourceGenerateOTP) {
            _otpGenerateData.value = it.data
            val jObject = JSONObject()

            jObject.put("email",emailStr)
            jObject.put("phone",phoneNumber)

            _otpScreenData.value = jObject

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callValidateOTPforUserAPI(otpReceived: String, emailStr: String, phoneNumber: String) = viewModelScope.launch(dispatchers.main) {
            Timber.i("Validate OTP Called")
            val requestData = GenerateOtpModel(
                Gson().toJson(
                    GenerateOtpModel.JSONDataRequest(
                        GenerateOtpModel.UPN(
                            loginName = phoneNumber,
                            emailAddress = emailStr,
                            primaryPhone = phoneNumber),
                        OTP = otpReceived, from = "103", message = "Validating OTP..."), GenerateOtpModel.JSONDataRequest::class.java))

             _progressBar.value = Event("Validating OTP...")
            _otpGenerateData.removeSource(userSourceGenerateOTP)
            withContext(dispatchers.io) {
                userSourceGenerateOTP = userManagementUseCase.invokeValidateOTP(true, requestData)
            }
            _otpGenerateData.addSource(userSourceGenerateOTP) {
                _otpGenerateData.value = it.data
                _isLoading.value = it.status

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.validity.equals("true", true)) {
                        callLogin(forceRefresh = true,emailStr = emailStr,socialLogin = true)
                    } else {
                        toastMessage(context.resources.getString(R.string.ERROR_UNABLE_VERIFY_OTP))
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    fun callLogin(forceRefresh: Boolean, name: String = "", emailStr: String, mobileStr: String = "", passwordStr: String = "", socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main) {

        val argsLogin = LoginViewModel.LoginEncryption.getEncryptedData(
            emailStr, passwordStr, isSocial = socialLogin)

        _progressBar.value = Event("Login to your Account...")
        _user.removeSource(userSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(dispatchers.io) { userSource = userManagementUseCase(isForceRefresh = forceRefresh, data = argsLogin) }
        _user.addSource(userSource){
            _user.value = it.data
            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,it.data?.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,it.data?.phoneNumber).apply()
                sharedPref.edit().putString(PreferenceConstants.TOKEN, it.data?.authToken).apply()
                // sharedPref.edit().putString(PreferenceConstants.PERSONID, it.data?.personId).apply()
                sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, it.data?.accountId?.toDouble()?.toInt().toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, it.data?.firstName).apply()
                sharedPref.edit().putString(PreferenceConstants.GENDER, it.data?.gender).apply()
                val pid = it.data?.personId?.toDouble()?.toInt()
                Timber.i("Person Id => "+pid)
                sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                navigate(LoginWithOtpFragmentDirections.actionLoginViaOTPFragmentToMainActivity())
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }

        }

    }

}