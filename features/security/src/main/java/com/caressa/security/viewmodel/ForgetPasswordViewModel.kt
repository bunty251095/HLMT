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
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.LocaleHelper
import com.caressa.model.entity.Users
import com.caressa.model.security.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.model.UserInfo
import com.caressa.security.ui.ChangePasswordFragment
import com.caressa.security.ui.ChangePasswordFragmentDirections
import com.caressa.security.ui.ForgetPasswordFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

@SuppressLint("StaticFieldLeak")
class ForgetPasswordViewModel(private val userCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                              private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

    private val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!

    var emailSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> = MutableLiveData()
    val _isEmail = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val isEmail: LiveData<EmailExistsModel.IsExistResponse> get() = _isEmail

    var otpGenerateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> = MutableLiveData()
    var otpValidateSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    val _otpGenerateData = MediatorLiveData<GenerateOtpModel.GenerateOTPResponse>()
    val otpGenerateData: LiveData<GenerateOtpModel.GenerateOTPResponse> get() = _otpGenerateData

    var updatePasswordSource: LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>> = MutableLiveData()
    val _updatePassword = MediatorLiveData<ChangePasswordModel.ChangePasswordResponse>()
    val updatePassword: LiveData<ChangePasswordModel.ChangePasswordResponse> get() = _updatePassword

    var resetPasswordSource: LiveData<Resource<ResetPasswordModel.ResetPasswordResponse>> = MutableLiveData()
    val _resetPassword = MediatorLiveData<ResetPasswordModel.ResetPasswordResponse>()
    val resetPassword: LiveData<ResetPasswordModel.ResetPasswordResponse> get() = _resetPassword

    private val _loginResponse = MediatorLiveData<LoginModel.Response>()
    val loginResponse: LiveData<LoginModel.Response> get() = _loginResponse
    var hlmtLoginUserSource: LiveData<Resource<LoginModel.Response>> = MutableLiveData()

    fun checkEmailExistOrNot( emailStr: String ) = viewModelScope.launch(dispatchers.main) {

        val requestData = EmailExistsModel(Gson().toJson(EmailExistsModel.JSONDataRequest(
                    emailAddress = emailStr), EmailExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Email..")
        _isEmail.removeSource(emailSource)
        withContext(dispatchers.io){ emailSource = userCase.invokeEmailExist(true,requestData)}
        _isEmail.addSource(emailSource){
            _isEmail.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals(Constants.TRUE,true)) {
                    UserInfo.from = Constants.FORGET
                    UserInfo.emailAddress = emailStr
                    navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToLoginWithOtpFragment())
//                    callGenerateVerificationCode(email = emailStr , from = "ForgetPassword")
                }else if(it.data?.isExist.equals(Constants.FALSE,true)) {
                    toastMessage(localResource.getString(R.string.ERROR_EMAIL_NOT_REGISTERED))
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callGenerateVerificationCode( email: String , from : String = "" , otp : String = "" )
            = viewModelScope.launch(dispatchers.main) {

        val requestData = GenerateOtpModel(Gson().toJson(GenerateOtpModel.JSONDataRequest(
            GenerateOtpModel.UPN(
                emailAddress = email),
                message = "Generating Verification Code"), GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Generating Verification Code")
        _otpGenerateData.removeSource(otpGenerateSource)
        withContext(dispatchers.io) {
            otpGenerateSource = userCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(otpGenerateSource) {
            _otpGenerateData.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it.data!!.status.equals(Constants.SUCCESS,ignoreCase = false) ) {
                    if ( from.equals("ForgetPassword",ignoreCase = true) ) {
                        navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToLoginWithOtpFragment())
                    } else if ( from.equals("VerifyCode",ignoreCase = true) ) {
                        toastMessage("Verification Code has been sent to your Registered Email Address")
                    }
                } else {
                    toastMessage(it.errorMessage)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callValidateVerificationCode( otpReceived: String, email: String )
            = viewModelScope.launch(dispatchers.main) {

        val requestData = GenerateOtpModel(Gson().toJson(GenerateOtpModel.JSONDataRequest(
            GenerateOtpModel.UPN(
                emailAddress = email),
                OTP = otpReceived,
                message = "Verifing Code..."), GenerateOtpModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Verifing Code...")
        _otpGenerateData.removeSource(otpGenerateSource)
        withContext(dispatchers.io) {
            otpGenerateSource = userCase.invokeValidateOTP(true, requestData)
        }
        _otpGenerateData.addSource(otpGenerateSource) {
            _otpGenerateData.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.validity.equals(Constants.TRUE, true)) {
                    //navigate(VerifyCodeFragmentDirections.actionVerifyCodeFragmentToChangePasswordFragment(email))
                } else if (it.data?.validity.equals(Constants.FALSE, true)) {
                    toastMessage("Please Enter Valid Verification Code sent to your Email Address.")
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callResetPassword(email: String, newPassword: String) =
        viewModelScope.launch(dispatchers.main) {

            val requestData = ResetPasswordModel(
                Gson().toJson(
                    ResetPasswordModel.JSONDataRequest(
                        emailAddress = email,
                        newPassword = newPassword,
                        oldPassword = UserInfo.tempPassword
                    ), ResetPasswordModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Changing Password...")
            _resetPassword.removeSource(updatePasswordSource)
            withContext(dispatchers.io) {
                resetPasswordSource = userCase.invokeResetPassword(true, requestData)
            }
            _resetPassword.addSource(resetPasswordSource) {
                _resetPassword.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if(it.data!!.success.equals("TRUE",true)){
                        toastMessage("Password changed successfully.")
                        UserInfo.fromChangePassword = true
                        navigate(ChangePasswordFragmentDirections.actionChangePasswordFragmentToLoginFragment())
                        it.data!!.newPassword?.let { it1 -> fetchLoginResponse(username = UserInfo.emailAddress, passwordStr = it1) }
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }


    fun callUpdatePassword(fragment: ChangePasswordFragment, email: String, newPassword: String) =
        viewModelScope.launch(dispatchers.main) {

            val requestData = ChangePasswordModel(
                Gson().toJson(
                    ChangePasswordModel.JSONDataRequest(
                        emailAddress = email,
                        newPassword = newPassword
                    ), ChangePasswordModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Changing Password...")
            _updatePassword.removeSource(updatePasswordSource)
            withContext(dispatchers.io) {
                updatePasswordSource = userCase.invokeUpdatePassword(true, requestData)
            }
            _updatePassword.addSource(updatePasswordSource) {
                _updatePassword.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data!!.isPasswordUpdate.equals(Constants.TRUE, true)) {
                        fragment.showPasswordUpdatedDialog()
                    } else {
                        toastMessage(localResource.getString(R.string.ERROR_UNABLE_TO_RESET_PASSWORD))
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    fun fetchLoginResponse(name: String = "", username: String, passwordStr: String = "") = viewModelScope.launch(dispatchers.main){

        val requestData = LoginModel(Gson().toJson(
            LoginModel.JSONDataRequest(
                mode = "LOGIN",name=name, emailAddress = username,password = passwordStr), LoginModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _loginResponse.removeSource(hlmtLoginUserSource)
        withContext(dispatchers.io){ hlmtLoginUserSource = userCase.invokeLoginResponse(true,requestData)}
        _loginResponse.addSource(hlmtLoginUserSource){
            _loginResponse.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Timber.i("Data=> "+it)
                val loginData = it.data?.response?.loginData!!

                if(loginData.personID.isNotEmpty()){
                    toastMessage("Invalid Credentials Provided")
                }else{
                    sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                    sharedPref.edit().putString(PreferenceConstants.EMAIL,loginData.emailAddress).apply()
                    sharedPref.edit().putString(PreferenceConstants.PHONE,loginData.phoneNumber).apply()
                    sharedPref.edit().putString(PreferenceConstants.TOKEN, loginData.context).apply()
                    sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, loginData.accountID.toString()).apply()
                    sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, loginData.name).apply()
                    sharedPref.edit().putString(PreferenceConstants.GENDER, if(loginData.gender.equals("Male",true))"1" else "2").apply()
                    sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                    sharedPref.edit().putString(PreferenceConstants.DOB,loginData.dateOfBirth).apply()
                    sharedPref.edit().putString(PreferenceConstants.IS_HLMT_USER,loginData.IsHLMTUser).apply()
                    sharedPref.edit().putString(PreferenceConstants.HLMT_USER_ID,loginData.HLMTUserID).apply()
                    sharedPref.edit().putString(PreferenceConstants.HLMT_USERNAME,loginData.HLMTUserName).apply()
                    sharedPref.edit().putString(PreferenceConstants.ACCOUNT_LINK_STATUS,loginData.accountLinkStatus).apply()

                    val pid = loginData.personID.toDouble().toInt()
                    Timber.i("Person Id => "+pid)
                    sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                    sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()

                    saveUserData(loginData)
                    navigate(ChangePasswordFragmentDirections.actionChangePasswordFragmentToMainActivity())
                }

                if (loginData.gender.isNullOrEmpty() || loginData.dateOfBirth.isNullOrEmpty()){
//                    navigate(HlmtLoginFragmentDirections.actionLoginFragmentToUserDetailFragment(hlmtEmployeeID = username, hlmtUserID = it.data!!.HLMTUserID.toString(),loginStatus = it.data!!.loginStatus.toString(),isRegister = "false",mobileNo = ""))
                }

            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }
    private fun saveUserData(usr: LoginModel.Data)= viewModelScope.launch {
        var user = Users(accountId = usr.accountID,personId = usr.personID.toDouble().toInt(),firstName = usr.firstName,dateOfBirth = usr.dateOfBirth,gender = if(usr.gender.equals("Male",true))"1" else "2",age = usr.age
            ,emailAddress = usr.emailAddress,phoneNumber = usr.phoneNumber,path = "",authToken = usr.context,
            partnerCode = usr.partnerCode,accountStatus = usr.accountStatus,accountType = usr.accountType,countryName = "",
            dialingCode = usr.dialingCode, isActive = usr.isActive, isAuthenticated = usr.isAuthenticated,profileImgPath = "",
            maritalStatus = usr.maritalStatus,lastName = if(usr.lastName.isNullOrEmpty()) "" else usr.lastName,name = usr.name)

        withContext(dispatchers.io) {
            userCase.invokeAddUserInfo(user)
        }
    }
}