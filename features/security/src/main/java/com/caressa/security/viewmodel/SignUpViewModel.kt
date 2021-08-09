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
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.model.security.EmailExistsModel
import com.caressa.model.security.GenerateOtpModel
import com.caressa.model.security.PhoneExistsModel
import com.caressa.model.entity.Users
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.ui.UserInfoFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class SignUpViewModel(private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                      private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

    // PRIVATE DATA
    private lateinit var argsLogin: String
    var registerUserSource: LiveData<Resource<Users>> = MutableLiveData()
    var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()

    private val _user = MediatorLiveData<Users>()
    val user: LiveData<Users> get() = _user

    var emailSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isEmail = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val isEmail: LiveData<EmailExistsModel.IsExistResponse> get() = _isEmail

    var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    private val _isPhone = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val isPhone: LiveData<PhoneExistsModel.IsExistResponse> get() = _isPhone

    private val _otpData = MediatorLiveData<GenerateOtpModel.GenerateOTPResponse>()
    val otpData: LiveData<GenerateOtpModel.GenerateOTPResponse> get() = _otpData

    private val regDetail: RegistrationDetails = RegistrationDetails("","","","")

    data class RegistrationDetails(var name: String, var mobileNo:String, var emailID:String, var passcode:String)

    fun checkEmailExistOrNot(
        name: String = "",
        emailStr: String,
        passwordStr: String = "",
        phoneNumber: String = "",
        socialLogin: Boolean = false,
        socialId: String = "",
        dateOfBirthCal: Calendar = Calendar.getInstance()) = viewModelScope.launch(dispatchers.main) {

        _progressBar.value = Event("Validating Email..")
        val requestData = EmailExistsModel(Gson().toJson(EmailExistsModel.JSONDataRequest(
                    emailAddress = emailStr), EmailExistsModel.JSONDataRequest::class.java))

        _isEmail.removeSource(emailSource)
        withContext(dispatchers.io) { emailSource = userManagementUseCase.invokeEmailExist(true, requestData) }
        _isEmail.addSource(emailSource) {
            _isEmail.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals("true", true)) {
                    if (socialLogin) {
                        callLogin(forceRefresh = true,name = name, emailStr = emailStr, passwordStr =  passwordStr, socialLogin =  socialLogin, socialId = socialId)
                    } else {
                        toastMessage(context.resources.getString(R.string.ERROR_EMAIL_REGISTERED))
                    }
                } else if (it.data?.isExist.equals("false", true)) {
                    if (socialLogin) {
                        //val action = SignUpFragmentDirections.actionNextUserInfo(name = name,mobileNo = phoneNumber, passCode = "123456", email = emailStr)
                        //navigate(action)
                        //callRegisterAPI(name = name, emailStr = emailStr, passwordStr = passwordStr, phoneNumber = phoneNumber, dateOfBirthCal = dateOfBirthCal, socialLogin = socialLogin)
                    } else {
                        //allCheckPhoneExistAPI(name = name, emailStr = emailStr, passwordStr = passwordStr, phoneNumber = phoneNumber, dateOfBirthCal = dateOfBirthCal, socialLogin = socialLogin)
                            if ( !Utilities.isNullOrEmpty(phoneNumber) ) {
                                callCheckPhoneExistAPI(name = name, emailStr = emailStr, passwordStr = passwordStr, phoneNumber = phoneNumber, dateOfBirthCal = dateOfBirthCal, socialLogin = socialLogin)
                            } else {
                                //val action = SignUpFragmentDirections.actionNextUserInfo(name = name,mobileNo = phoneNumber, passCode = passwordStr, email = emailStr)
                                //navigate(action)
                            }
                    }
                }
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

    private fun callCheckPhoneExistAPI(name: String = "", emailStr: String, passwordStr: String = "", phoneNumber: String = "", dateOfBirthCal: Calendar = Calendar.getInstance(), socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main){

        val requestData = PhoneExistsModel(Gson().toJson(PhoneExistsModel.JSONDataRequest(
                    primaryPhone = phoneNumber), PhoneExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Phone Number..")
        _isPhone.removeSource(phoneSource)
        withContext(dispatchers.io) { phoneSource = userManagementUseCase.invokePhoneExist(true, requestData) }
        _isPhone.addSource(phoneSource) {
            _isPhone.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals(Constants.FALSE, true)) {
                    regDetail.name = name
                    regDetail.emailID = emailStr
                    regDetail.mobileNo = phoneNumber
                    regDetail.passcode = passwordStr
                    //val action = SignUpFragmentDirections.actionNextUserInfo(name = name,mobileNo = phoneNumber, passCode = passwordStr, email = emailStr)
                    //navigate(action)
                } else if ( it.data?.isExist.equals(Constants.TRUE, true) ) {
                    regDetail.name = name
                    regDetail.emailID = emailStr
                    regDetail.mobileNo = phoneNumber
                    regDetail.passcode = passwordStr
                    toastMessage(context.resources.getString(R.string.ERROR_PHONE_REGISTERED))
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

     fun callRegisterAPI(
        name: String = "",
        emailStr: String,
        passwordStr: String = "",
        phoneNumber: String = "",
        dateOfBirthCal: Calendar = Calendar.getInstance(),
        socialLogin: Boolean = false,
        socialId: String = "",
        dob: String = "",
        gender: String = "2") = viewModelScope.launch(dispatchers.main) {

        _progressBar.value = Event("Authentication User")

        val registerEnc = LoginViewModel.LoginEncryption.getEncryptedData(
            username = emailStr,
            password = passwordStr,
            dob = if(dob.equals("") )DateHelper.convertDateToStr(dateOfBirthCal.time, DateHelper.SERVER_DATE_YYYYMMDD) else dob,
            phoneNumber = phoneNumber,
            name = name,
            isSocial = socialLogin,
            gender = gender)

        _user.removeSource(registerUserSource)
        withContext(dispatchers.io) { registerUserSource = userManagementUseCase.invokeRegistration(data = registerEnc) }
        _user.addSource(registerUserSource) {
            _user.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                _snackbarMessage.value = Event("Register Successful")
                sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,it.data?.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,it.data?.phoneNumber).apply()
                sharedPref.edit().putString(PreferenceConstants.TOKEN, it.data?.authToken).apply()
                sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, it.data?.accountId?.toDouble()?.toInt().toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, it.data?.firstName).apply()
                sharedPref.edit().putString(PreferenceConstants.GENDER, it.data?.gender).apply()
                val pid = it.data?.personId?.toDouble()?.toInt()
                Timber.i("Person Id => "+pid)
                sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                if(!dob.equals("")){
                    navigate(UserInfoFragmentDirections.actionUserInfoFragmentToMainActivity())
                }else {
                    //navigate(SignUpFragmentDirections.actionSignUpFragmentToMainActivity())
                }
            }

            if(it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }
        }

    }

    private fun callLogin(forceRefresh: Boolean, name: String = "", emailStr: String, mobileStr: String = "", passwordStr: String = "", socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main) {

        _progressBar.value = Event("Authenticating User")
        argsLogin = LoginViewModel.LoginEncryption.getEncryptedData(
            emailStr,
            passwordStr,
            isSocial = socialLogin)
        _user.removeSource(loginUserSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(dispatchers.io) { loginUserSource = userManagementUseCase(isForceRefresh = forceRefresh, data = argsLogin) }
        _user.addSource(loginUserSource){
            _user.value = it.data
            _progressBar.value = Event(Event.HIDE_PROGRESS)

            if (it.status == Resource.Status.SUCCESS){
                sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,it.data?.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,it.data?.phoneNumber).apply()
                sharedPref.edit().putString(PreferenceConstants.TOKEN, it.data?.authToken).apply()
                sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, it.data?.accountId?.toDouble()?.toInt().toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, it.data?.firstName).apply()
                sharedPref.edit().putString(PreferenceConstants.GENDER, it.data?.gender).apply()
                val pid = it.data?.personId?.toDouble()?.toInt()
                Timber.i("Person Id => "+pid)
                sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                //navigate(SignUpFragmentDirections.actionSignUpFragmentToMainActivity())
            }

            if (it.status == Resource.Status.ERROR) {
                toastMessage(it.errorMessage)
            }

        }

    }



}