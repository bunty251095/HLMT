package com.caressa.security.viewmodel

import android.annotation.SuppressLint
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
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Validation
import com.caressa.model.entity.Users
import com.caressa.model.security.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.model.UserInfo
import com.caressa.security.ui.LoginWithOtpFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*

@SuppressLint("StaticFieldLeak")
class LoginWithOtpViewModel (private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                             private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

    private val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!

    private lateinit var argsLogin: String
    private var isLogin = false

    private val _loginResponse = MediatorLiveData<LoginModel.Response>()
    val loginResponse: LiveData<LoginModel.Response> get() = _loginResponse
    var hlmtLoginUserSource: LiveData<Resource<LoginModel.Response>> = MutableLiveData()

    var userSourceGenerateOTP: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpGenerateData = MediatorLiveData<GenerateOtpModel.GenerateOTPResponse>()
    val otpGenerateData: LiveData<GenerateOtpModel.GenerateOTPResponse> get() = _otpGenerateData

    var userSourceVerifyOTP: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    private val _otpVerifyData = MediatorLiveData<GenerateOtpModel.GenerateOTPResponse>()
    val otpVerifyData: LiveData<GenerateOtpModel.GenerateOTPResponse> get() = _otpVerifyData

    private val _isLoading = MutableLiveData<Resource.Status>()
    val isLoading: LiveData<Resource.Status> get() = _isLoading

    private val _isLoginName = MediatorLiveData<LoginNameExistsModel.IsExistResponse>()
    val isLoginName: LiveData<LoginNameExistsModel.IsExistResponse> get() = _isLoginName
    var loginNameUserSource: LiveData<Resource<LoginNameExistsModel.IsExistResponse>> = MutableLiveData()

    var forgetPasswordSource: LiveData<Resource<ForgetPasswordModel.ForgetPasswordResponse>> = MutableLiveData()
    val _forgetPassword = MediatorLiveData<ForgetPasswordModel.ForgetPasswordResponse>()
    val forgetPassword: LiveData<ForgetPasswordModel.ForgetPasswordResponse> get() = _forgetPassword

    var fromResendOTP = false

    init {
        calIGenerateOTPApi(UserInfo.emailAddress)
    }

    fun checkLoginNameExistOrNot(emailAddress: String) = viewModelScope.launch(dispatchers.main){

        if(!emailAddress.isEmpty() && Validation.isValidEmail(emailAddress)) {
            val requestData = LoginNameExistsModel(
                Gson().toJson(
                    LoginNameExistsModel.JSONDataRequest(
                        loginName = emailAddress
                    ), LoginNameExistsModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Validating Username..")
            _isLoginName.removeSource(loginNameUserSource)
            withContext(dispatchers.io) {
                loginNameUserSource = userManagementUseCase.invokeLoginNameExist(true, requestData)
            }
            _isLoginName.addSource(loginNameUserSource) {
                _isLoginName.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.isExist.equals("true", true)) {
                        isLogin = true
                        calIGenerateOTPApi(emailAddress)
                    } else {
                        isLogin = false
                        calIGenerateOTPApi(emailAddress)
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }else{
            toastMessage(localResource.getString(R.string.ERROR_INVALID_EMAIL))
        }
    }

    fun calIGenerateOTPApi(emailAddress: String) = viewModelScope.launch(dispatchers.main) {
        Timber.i("Generate OTP Called")
        val requestData = GenerateOtpModel(
            Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        emailAddress = emailAddress,
                        ),
                    from = "102", message = "Generating One Time Password (TAC)"), GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Generating TAC...")
        _otpGenerateData.removeSource(userSourceGenerateOTP)
        withContext(dispatchers.io) {
            userSourceGenerateOTP = userManagementUseCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(userSourceGenerateOTP) {
            _otpGenerateData.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (fromResendOTP){
                    toastMessage("TAC sent successfully.")
                    fromResendOTP = false
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callValidateOTPforUserAPI(otpReceived: String, emailAddress: String,con: Context) = viewModelScope.launch(dispatchers.main) {
            Timber.i("Validate OTP Called")
        if(validateDetails(otpReceived,emailAddress)) {
            val requestData = GenerateOtpModel(
                Gson().toJson(
                    GenerateOtpModel.JSONDataRequest(
                        GenerateOtpModel.UPN(
                            emailAddress = emailAddress,
                        ),
                        OTP = otpReceived, from = "103", message = "Validating OTP..."
                    ), GenerateOtpModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Validating OTP...")
            _otpVerifyData.removeSource(userSourceVerifyOTP)
            withContext(dispatchers.io) {
                userSourceVerifyOTP = userManagementUseCase.invokeValidateOTP(true, requestData)
            }
            _otpVerifyData.addSource(userSourceVerifyOTP) {
                _otpVerifyData.value = it.data
                _isLoading.value = it.status

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.validity.equals("true", true)) {
                        if (UserInfo.from.equals(Constants.LOGIN)) {
                            fetchLoginResponse(username = UserInfo.emailAddress, passwordStr = UserInfo.password,con = con)
                        }else if(UserInfo.from.equals(Constants.REGISTER)){
                            navigate(LoginWithOtpFragmentDirections.actionLoginWithOtpFragmentToUserDetailsFragment())
                        } else if(UserInfo.from.equals(Constants.FORGET)){
                            callForgetPassword(UserInfo.emailAddress)
                        }
                        //toastMessage("OTP verified successfully")
                    } else {
                        toastMessage(localResource.getString(R.string.ERROR_UNABLE_VERIFY_OTP))
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun validateDetails(otpReceived: String, phoneNumber: String): Boolean {
        if (otpReceived.isEmpty()){
            toastMessage("Please Enter Valid OTP")
        }else if (otpReceived.length != 6){
            toastMessage("Invalid OTP entered.")
        }else{
            return true
        }
        return false
    }

    fun fetchLoginResponse(name: String = "", username: String, passwordStr: String = "",con: Context) = viewModelScope.launch(dispatchers.main){

        val requestData = LoginModel(Gson().toJson(
            LoginModel.JSONDataRequest(
            mode = "LOGIN",name=name, emailAddress = username,password = passwordStr), LoginModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _loginResponse.removeSource(hlmtLoginUserSource)
        withContext(dispatchers.io){ hlmtLoginUserSource = userManagementUseCase.invokeLoginResponse(true,requestData)}
        _loginResponse.addSource(hlmtLoginUserSource){
            _loginResponse.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Timber.i("Data=> "+it)
                try {
                    var loginData = it.data?.response?.loginData!!
                    if(!loginData!!.personID.isNullOrEmpty()) {
                        sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN, true).apply()
                        sharedPref.edit().putBoolean(PreferenceConstants.STEPS_TRACKER_FIRST, true).apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.EMAIL, loginData.emailAddress)
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.PHONE, loginData.phoneNumber)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.TOKEN, loginData.context)
                            .apply()
                        sharedPref.edit()
                            .putString(
                                PreferenceConstants.ACCOUNTID,
                                loginData.accountID.toString()
                            )
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, loginData.name)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.GENDER, if(loginData.gender.equals("Male",true))"1" else "2").apply()
                        sharedPref.edit().putString(
                            PreferenceConstants.RELATIONSHIPCODE,
                            Constants.SELF_RELATIONSHIP_CODE
                        ).apply()

                        sharedPref.edit().putString(PreferenceConstants.IS_HLMT_USER,loginData.IsHLMTUser).apply()
                        sharedPref.edit().putString(PreferenceConstants.HLMT_USER_ID,loginData.HLMTUserID).apply()
                        sharedPref.edit().putString(PreferenceConstants.HLMT_USERNAME,loginData.HLMTUserName).apply()
                        sharedPref.edit().putString(PreferenceConstants.ACCOUNT_LINK_STATUS,loginData.accountLinkStatus).apply()

//                        sharedPref.edit().putString(PreferenceConstants.IS_HLMT_USER,"true").apply()
//                        sharedPref.edit().putString(PreferenceConstants.HLMT_USER_ID,"211").apply()
//                        sharedPref.edit().putString(PreferenceConstants.HLMT_USERNAME,"Mayuresh21").apply()
//                        sharedPref.edit().putString(PreferenceConstants.ACCOUNT_LINK_STATUS,"true").apply()

                        val pid = loginData?.personID?.toDouble()?.toInt()
                        Timber.i("Person Id => " + pid)
                        sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString())
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                            .apply()
                        // Added by Rohit
                        //RealPathUtil.creatingLocalDirctories()
//                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.NON_HLMT_LOGIN_SUCCESSFUL_EVENT)
                        saveUserData(loginData)
                        if ( loginData.languageCode.equals("my",ignoreCase = true) ) {
                            LocaleHelper.setLocale(con,"ms")
                        } else {
                            LocaleHelper.setLocale(con, "en")
                        }
                        navigate(LoginWithOtpFragmentDirections.actionLoginWithOtpFragmentToMainActivity())
                    }else{
//                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.NON_HLMT_LOGIN_FAIL_EVENT)
                        toastMessage("Unable to login with this user.")
                    }
                }catch (e: Exception){e.printStackTrace()}
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callForgetPassword(email: String) =
        viewModelScope.launch(dispatchers.main) {

            val requestData = ForgetPasswordModel(
                Gson().toJson(
                    ForgetPasswordModel.JSONDataRequest(
                        emailAddress = email,
                    ), ForgetPasswordModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Changing Password...")
            _forgetPassword.removeSource(forgetPasswordSource)
            withContext(dispatchers.io) {
                forgetPasswordSource = userManagementUseCase.invokeForgetPassword(true, requestData)
            }
            _forgetPassword.addSource(forgetPasswordSource) {
                _forgetPassword.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data!!.success.equals(Constants.TRUE, true)) {
                        UserInfo.tempPassword = it.data!!.newPassword
                        navigate(LoginWithOtpFragmentDirections.actionLoginWithOtpFragmentToChangePasswordFragment())
                    } else {
                        toastMessage(localResource.getString(R.string.ERROR_GENERAL))
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    private fun saveUserData(usr: LoginModel.Data)= viewModelScope.launch {
        var user = Users(accountId = usr.accountID,personId = usr.personID.toDouble().toInt(),firstName = usr.name,dateOfBirth = usr.dateOfBirth,gender = if(usr.gender.equals("Male",true))"1" else "2",age = usr.age
            ,emailAddress = usr.emailAddress,phoneNumber = usr.phoneNumber,path = "",authToken = usr.context,
            partnerCode = usr.partnerCode,accountStatus = usr.accountStatus,accountType = usr.accountType,countryName = "",
            dialingCode = usr.dialingCode, isActive = usr.isActive, isAuthenticated = usr.isAuthenticated,profileImgPath = "",
            maritalStatus = usr.maritalStatus,lastName = if(usr.lastName.isNullOrEmpty()) "" else usr.lastName,name = usr.name)

        withContext(dispatchers.io) {
            userManagementUseCase.invokeAddUserInfo(user)
        }
    }

}