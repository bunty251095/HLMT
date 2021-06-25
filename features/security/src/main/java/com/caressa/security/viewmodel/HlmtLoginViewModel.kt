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
import com.caressa.model.entity.Users
import com.caressa.model.security.EmailExistsModel
import com.caressa.model.security.LoginModel
import com.caressa.model.security.LoginNameExistsModel
import com.caressa.model.security.PhoneExistsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.ui.LoginFragmentDirections
import com.caressa.security.ui.LoginWithOtpFragmentDirections
import com.caressa.security.ui.UserDetailsFragmentDirections
import com.caressa.security.viewmodel.LoginViewModel.LoginEncryption.getEncryptedData
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class HlmtLoginViewModel(private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                         private val sharedPref: SharedPreferences, val context: Context) : BaseViewModel() {

    // PRIVATE DATA
    private lateinit var argsLogin: String
    var loginUserSource: LiveData<Resource<Users>> = MutableLiveData()
    var registerUserSource: LiveData<Resource<Users>> = MutableLiveData()
    var socialLoginUserSource: LiveData<Resource<LoginNameExistsModel.IsExistResponse>> = MutableLiveData()

    private val _user = MediatorLiveData<Users>()
    val user: LiveData<Users> get() = _user

    private val _isLoginName = MediatorLiveData<LoginNameExistsModel.IsExistResponse>()
    val isLoginName: LiveData<LoginNameExistsModel.IsExistResponse> get() = _isLoginName

    private val _loginResponse = MediatorLiveData<LoginModel.Response>()
    val loginResponse: LiveData<LoginModel.Response> get() = _loginResponse
    var hlmtLoginUserSource: LiveData<Resource<LoginModel.Response>> = MutableLiveData()

    private val _socialUser = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val socialUser: LiveData<EmailExistsModel.IsExistResponse> get() = _socialUser

    private val _isPhone = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val isPhone: LiveData<PhoneExistsModel.IsExistResponse> get() = _isPhone
    var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()


    fun callLogin(forceRefresh: Boolean, name: String = "", mobileStr: String = "", passwordStr: String = "") = viewModelScope.launch(dispatchers.main) {
        argsLogin = LoginEncryption.getHlmtEncryptedData("LOGIN",mobileStr,passwordStr,name)
        _progressBar.value = Event("Authenticating..")
        _user.removeSource(loginUserSource) // We make sure there is only one source of livedata (allowing us properly refresh)
        withContext(dispatchers.io) { loginUserSource = userManagementUseCase(isForceRefresh = forceRefresh, data = argsLogin) }
        _user.addSource(loginUserSource) {
            _progressBar.value = Event(Event.HIDE_PROGRESS)
            Timber.i("Login Data=> "+it.data)
            _user.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                /*sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
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
                navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())*/
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun checkLoginNameExistOrNot(name: String = "", username: String, passwordStr: String = "") = viewModelScope.launch(dispatchers.main){

        val requestData = LoginNameExistsModel(Gson().toJson(LoginNameExistsModel.JSONDataRequest(
            loginName = username), LoginNameExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _isLoginName.removeSource(socialLoginUserSource)
        withContext(dispatchers.io){ socialLoginUserSource = userManagementUseCase.invokeLoginNameExist(true,requestData)}
        _isLoginName.addSource(socialLoginUserSource){
            _isLoginName.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data?.isExist.equals("true",true)) {
                    fetchLoginResponse(name=name,username=username,passwordStr=passwordStr)
                }else {
                    toastMessage("Invalid User Details")
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
                mode = "LOGIN",name=name,phoneNumber = username,password = passwordStr), LoginModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _loginResponse.removeSource(hlmtLoginUserSource)
        withContext(dispatchers.io){ hlmtLoginUserSource = userManagementUseCase.invokeLoginResponse(true,requestData)}
        _loginResponse.addSource(hlmtLoginUserSource){
            _loginResponse.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Timber.i("Data=> "+it)
                var loginData = it.data?.response?.loginData!!
                sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN,true).apply()
                sharedPref.edit().putString(PreferenceConstants.EMAIL,loginData.emailAddress).apply()
                sharedPref.edit().putString(PreferenceConstants.PHONE,loginData.phoneNumber).apply()
                sharedPref.edit().putString(PreferenceConstants.TOKEN, loginData.context).apply()
                sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, loginData.accountID.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, loginData.name).apply()
                sharedPref.edit().putString(PreferenceConstants.GENDER, "1").apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()

                val pid = loginData?.personID?.toDouble()?.toInt()
                Timber.i("Person Id => "+pid)
                sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()
                // Added by Rohit
                //RealPathUtil.creatingLocalDirctories()
                saveUserData(loginData)
                navigate(LoginWithOtpFragmentDirections.actionLoginViaOTPFragmentToMainActivity())
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }
    fun fetchRegistrationResponse(name: String = "", phoneNumber: String, passwordStr: String = "",gender:String,dob:String,emailStr: String) = viewModelScope.launch(dispatchers.main){

        if(validateData(name,phoneNumber,emailStr,dob)) {
            val requestData = LoginModel(
                Gson().toJson(
                    LoginModel.JSONDataRequest(
                        mode = "REGISTER",
                        name = name,
                        phoneNumber = phoneNumber,
                        password = passwordStr,
                        gender = gender,
                        dateOfBirth = dob,
                        emailAddress = emailStr
                    ), LoginModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Validating Username..")
            _loginResponse.removeSource(hlmtLoginUserSource)
            withContext(dispatchers.io) {
                hlmtLoginUserSource = userManagementUseCase.invokeLoginResponse(true, requestData)
            }
            _loginResponse.addSource(hlmtLoginUserSource) {
                _loginResponse.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Timber.i("Data=> " + it)
                    var loginData = it.data?.response?.loginData!!
                    sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN, true).apply()
                    sharedPref.edit().putString(PreferenceConstants.EMAIL, loginData.emailAddress)
                        .apply()
                    sharedPref.edit().putString(PreferenceConstants.PHONE, loginData.phoneNumber)
                        .apply()
                    sharedPref.edit().putString(PreferenceConstants.TOKEN, loginData.context)
                        .apply()
                    sharedPref.edit()
                        .putString(PreferenceConstants.ACCOUNTID, loginData.accountID.toString())
                        .apply()
                    sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, loginData.name)
                        .apply()
                    sharedPref.edit().putString(PreferenceConstants.GENDER, "1").apply()
                    sharedPref.edit().putString(
                        PreferenceConstants.RELATIONSHIPCODE,
                        Constants.SELF_RELATIONSHIP_CODE
                    ).apply()

                    val pid = loginData?.personID?.toDouble()?.toInt()
                    Timber.i("Person Id => " + pid)
                    sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString())
                        .apply()
                    sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                        .apply()
                    // Added by Rohit
                    //RealPathUtil.creatingLocalDirctories()
                    saveUserData(loginData)
                    navigate(UserDetailsFragmentDirections.actionUserDetailsToHomeScreen())
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun validateData(
        name: String,
        phoneNumber: String,
        emailStr: String,
        dob: String
    ): Boolean {
        var isValid = false
        val emailPattern:Regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

        if (name.isNullOrEmpty()){
            toastMessage("Please Enter Name.")
        }else if(phoneNumber.isNullOrEmpty() || phoneNumber.length < 10){
            toastMessage("Invalid Phone Number")
        }else if(emailStr.isNullOrEmpty() || !emailStr.matches(emailPattern)){
            toastMessage("Invalid email Address")
        }else if (dob.isNullOrEmpty()){
            toastMessage("Invalid Date of Birth")
        }else if (!DateHelper.isDateAbove18Years(dob)) {
            toastMessage("Application user must be 18 years old")
        }else{
            isValid = true
        }
        return isValid
    }

    private fun saveUserData(usr: LoginModel.Data)= viewModelScope.launch {
        var user = Users(accountId = usr.accountID,personId = usr.personID.toDouble().toInt(),firstName = usr.firstName,dateOfBirth = usr.dateOfBirth,gender = if(usr.gender.equals("Male",true))"0" else "1",age = usr.age
            ,emailAddress = usr.emailAddress,phoneNumber = usr.phoneNumber,path = "",authToken = usr.context,
            partnerCode = usr.partnerCode,accountStatus = usr.accountStatus,accountType = usr.accountType,countryName = "",
            dialingCode = usr.dialingCode, isActive = usr.isActive, isAuthenticated = usr.isAuthenticated,profileImgPath = "",
            maritalStatus = usr.maritalStatus,lastName = if(usr.lastName.isNullOrEmpty()) "" else usr.lastName,name = usr.name)

        withContext(dispatchers.io) {
            userManagementUseCase.invokeAddUserInfo(user)
        }
    }

    fun getTermsScreen() {
        navigate(LoginFragmentDirections.actionLoginTerms())
    }

    object LoginEncryption {
        fun getHlmtEncryptedData(mode: String,
                                 phoneNumber: String = "", password: String = "",
                                 name: String = ""): String {
            val registerObject = JSONObject()
            registerObject.put("Mode", mode)
            registerObject.put("PartnerCode", Configuration.PartnerCode)
            registerObject.put("PhoneNumber", phoneNumber)
            registerObject.put("Password", password)
            registerObject.put("Name", name)

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
                navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
            }
        }

    }

    fun getLoginStatus(): Boolean{
        return sharedPref.getBoolean(PreferenceConstants.IS_LOGIN,false)
    }

}