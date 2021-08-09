package com.caressa.security.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Event
import com.caressa.model.security.ChangePasswordModel
import com.caressa.model.security.EmailExistsModel
import com.caressa.model.security.GenerateOtpModel
import com.caressa.model.security.PhoneExistsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ForgetPasswordViewModel(private val userCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                              private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

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
                    callGenerateVerificationCode(email = emailStr , from = "ForgetPassword")
                }else if(it.data?.isExist.equals(Constants.FALSE,true)) {
                    toastMessage("This Email Address is not Registered with us")
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
                loginName = email,
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
                if ( it.data!!.status.equals(Constants.SUCCESS,ignoreCase = true) ) {
                    if ( from.equals("ForgetPassword",ignoreCase = true) ) {
                        //navigate(ForgetPasswordFragmentDirections.actionForgotPasswordFragmentToVerifyCodeFragment(email))
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

    fun callUpdatePassword( email: String , newPassword : String )
            = viewModelScope.launch(dispatchers.main) {

        val requestData = ChangePasswordModel(Gson().toJson(ChangePasswordModel.JSONDataRequest(
            emailAddress = email ,
            newPassword = newPassword), ChangePasswordModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Changing Password...")
        _updatePassword.removeSource(updatePasswordSource)
        withContext(dispatchers.io){ updatePasswordSource = userCase.invokeUpdatePassword(true,requestData)}
        _updatePassword.addSource(updatePasswordSource){
            _updatePassword.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.isPasswordUpdate.equals(Constants.TRUE,true)) {

                } else {
                    toastMessage("Unable to Reset Password")
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

}