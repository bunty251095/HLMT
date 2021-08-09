package com.caressa.security.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.EncryptionUtility
import com.caressa.common.utils.Event
import com.caressa.model.security.EmailExistsModel
import com.caressa.model.entity.Users
import com.caressa.model.security.PhoneExistsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.viewmodel.LoginViewModel.LoginEncryption.getEncryptedData
import com.caressa.security.domain.UserManagementUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class LoginViewModel(private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                     private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

    // PRIVATE DATA
    private lateinit var argsLogin: String
    var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()
    var registerUserSource: LiveData<Resource<Users>> = MutableLiveData()
    var socialLoginUserSource: LiveData<Resource<EmailExistsModel.IsExistResponse>> = MutableLiveData()

    private val _user = MediatorLiveData<Users>()
    val user: LiveData<Users> get() = _user

    private val _isEmail = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val isEmail: LiveData<EmailExistsModel.IsExistResponse> get() = _isEmail

    private val _socialUser = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val socialUser: LiveData<EmailExistsModel.IsExistResponse> get() = _socialUser

    private val _isPhone = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val isPhone: LiveData<PhoneExistsModel.IsExistResponse> get() = _isPhone
    var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()


    fun callLogin(forceRefresh: Boolean, name: String = "", emailStr: String, mobileStr: String = "", passwordStr: String = "", socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main) {
        argsLogin = getEncryptedData(username = emailStr,password = passwordStr,name = emailStr,isSocial = socialLogin)
        _progressBar.value = Event("Authenticating..")
        _user.removeSource(loginUserSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(dispatchers.io) { loginUserSource = userManagementUseCase(isForceRefresh = forceRefresh, data = argsLogin) }
        _user.addSource(loginUserSource) {
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
                // Added by Rohit
                //RealPathUtil.creatingLocalDirctories()
                //navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun checkEmailExistOrNot(name: String = "", emailStr: String, passwordStr: String = "", socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main){

        val requestData = EmailExistsModel(Gson().toJson(EmailExistsModel.JSONDataRequest(
                    emailAddress = emailStr), EmailExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _isEmail.removeSource(socialLoginUserSource)
        withContext(dispatchers.io){ socialLoginUserSource = userManagementUseCase.invokeEmailExist(true,requestData)}
        _isEmail.addSource(socialLoginUserSource){
            _isEmail.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals("true",true)) {
                    callLogin(forceRefresh = true, name = name, emailStr = emailStr, passwordStr = passwordStr, socialLogin = socialLogin, socialId = socialId)
                }else {
                    callRegisterAPI(name = name, emailStr = emailStr, socialLogin = false)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    object LoginEncryption {
        fun getEncryptedData(username: String, password: String,
                             phoneNumber: String = "", otp: String = "",
                             name: String = "", dob: String = "", gender: String = "2",
                             employeeId: String = "", isSocial: Boolean = false): String {
        val registerObject = JSONObject()
        registerObject.put(Constants.UserConstants.EMAIL_ADDRESS, username)
        registerObject.put(Constants.UserConstants.PASSWORD, password)
        registerObject.put(Constants.UserConstants.AUTH_TYPE, if (isSocial) Constants.SSO else Constants.NSSO)
        registerObject.put(Constants.UserConstants.PHONE_NUMBER, phoneNumber)
        registerObject.put(Constants.UserConstants.PARTNER_CODE, Configuration.PartnerCode)
        registerObject.put(Constants.UserConstants.OTP, otp)
        registerObject.put(Constants.UserConstants.CLUSTER_CODE, "")
        registerObject.put(Constants.UserConstants.NAME, name)
        registerObject.put(Constants.UserConstants.DOB, dob)
        registerObject.put(Constants.UserConstants.GENDER, gender)
        registerObject.put(Constants.UserConstants.EMPLOYEE_ID, employeeId)
        Timber.i( "BEFORE -> %s", registerObject.toString())
        val loginEncrypted = EncryptionUtility.encrypt(Configuration.SecurityKey, registerObject.toString(), Configuration.SecurityKey)
        return loginEncrypted
    }
    }

    private fun callRegisterAPI(
        name: String = "",
        emailStr: String,
        passwordStr: String = "",
        phoneNumber: String = "",
        dateOfBirthCal: Calendar = Calendar.getInstance(),
        socialLogin: Boolean = false,
        socialId: String = "") = viewModelScope.launch(dispatchers.main) {

        val registerEnc = getEncryptedData(username = emailStr, password = passwordStr, dob = DateHelper.convertDateToStr(dateOfBirthCal.time, DateHelper.SERVER_DATE_YYYYMMDD), phoneNumber = phoneNumber, name = name, gender = "1", isSocial = socialLogin)
        _user.removeSource(registerUserSource)
        withContext(dispatchers.io) { registerUserSource = userManagementUseCase.invokeRegistration(data = registerEnc) }
        _user.addSource(registerUserSource) {
            _user.value = it.data
            if (it.status == Resource.Status.SUCCESS) {
                _snackbarMessage.value = Event("Register Successful")
                sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,it.data?.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,it.data?.phoneNumber).apply()
                sharedPref.edit().putString(PreferenceConstants.TOKEN, it.data?.authToken).apply()
                sharedPref.edit().putString(PreferenceConstants.PERSONID, it.data?.personId.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, it.data?.personId.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                //navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
            }
        }

    }

    fun getLoginStatus(): Boolean{
        return sharedPref.getBoolean(PreferenceConstants.IS_LOGIN,false)
    }

    fun checkPhoneExistAPI(name: String = "", emailStr: String= "", passwordStr: String = "", phoneNumber: String = "", dateOfBirthCal: Calendar = Calendar.getInstance(), socialLogin: Boolean = false, socialId: String = "") = viewModelScope.launch(dispatchers.main){

        _progressBar.value = Event("Validating Phone Number..")
        val requestData = PhoneExistsModel(Gson().toJson(PhoneExistsModel.JSONDataRequest(
                    primaryPhone = phoneNumber), PhoneExistsModel.JSONDataRequest::class.java))

        _isPhone.removeSource(phoneSource)
        withContext(dispatchers.io) { phoneSource = userManagementUseCase.invokePhoneExist(true, requestData) }
        _isPhone.addSource(phoneSource) {
            _isPhone.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals("false", true)) {
                    toastMessage(context.resources.getString(R.string.ERROR_INVALID_EMAIL_PHONE))
                } else if ( it.data?.isExist.equals("true", true) ) {
                    callLogin(forceRefresh = true,emailStr = it.data!!.account!!.emailAddress.toString(), passwordStr = passwordStr)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }




}