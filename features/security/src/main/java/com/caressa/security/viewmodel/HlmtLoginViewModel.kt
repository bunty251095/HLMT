package com.caressa.security.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.*
import com.caressa.model.entity.Users
import com.caressa.model.home.UpdateUserDetailsModel
import com.caressa.model.home.UploadProfileImageResponce
import com.caressa.model.security.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.ui.HlmtLoginFragmentDirections
import com.caressa.security.ui.UserDetailsFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

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

    private val _hlmt360LoginResponse = MediatorLiveData<HLMTLoginModel.LoginResponse>()
    val hlmt360LoginResponse: LiveData<HLMTLoginModel.LoginResponse> get() = _hlmt360LoginResponse
    var hlmt360LoginUserSource: LiveData<Resource<HLMTLoginModel.LoginResponse>> = MutableLiveData()

    private val _socialUser = MediatorLiveData<EmailExistsModel.IsExistResponse>()
    val socialUser: LiveData<EmailExistsModel.IsExistResponse> get() = _socialUser

    private val _isPhone = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val isPhone: LiveData<PhoneExistsModel.IsExistResponse> get() = _isPhone
    var phoneSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()

    var uploadProfileImageSource: LiveData<Resource<UploadProfileImageResponce>> = MutableLiveData()
    val _uploadProfileImage = MediatorLiveData<UploadProfileImageResponce>()
    val uploadProfileImage: LiveData<UploadProfileImageResponce> get() = _uploadProfileImage

    var updateUserDetailsSource: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> = MutableLiveData()
    val _updateUserDetails = MediatorLiveData<UpdateUserDetailsModel.UpdateUserDetailsResponse>()
    val updateUserDetails: LiveData<UpdateUserDetailsModel.UpdateUserDetailsResponse> get() = _updateUserDetails


    var isAccountExist = false

    fun checkLoginNameExistOrNot(name: String = "", username: String, passwordStr: String = "") = viewModelScope.launch(dispatchers.main){

        if(validateLoginData(username,passwordStr)) {
            val requestData = LoginNameExistsModel(
                Gson().toJson(
                    LoginNameExistsModel.JSONDataRequest(
                        loginName = username
                    ), LoginNameExistsModel.JSONDataRequest::class.java
                )
            )
            _progressBar.value = Event("Validating Username..")
            _isLoginName.removeSource(socialLoginUserSource)
            withContext(dispatchers.io) {
                socialLoginUserSource =
                    userManagementUseCase.invokeLoginNameExist(true, requestData)
            }
            _isLoginName.addSource(socialLoginUserSource) {
                _isLoginName.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.isExist.equals("true", true)) {
                        isAccountExist = true
                    } else {
                        isAccountExist = false
                    }
                    fetchHLMT360LoginResponse(username = username, passwordStr = passwordStr)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun fetchLoginResponse(name: String = "", username: String, passwordStr: String = "",hlmtUserId: String,hlmtEmpId: String,hlmtLoginStatus: String) = viewModelScope.launch(dispatchers.main){

        val requestData = LoginModel(Gson().toJson(
            LoginModel.JSONDataRequest(
                mode = "LOGIN",name=name,phoneNumber = username,password = "123456",hlmtLoginStatus = hlmtLoginStatus,hlmtUserID = hlmtUserId,employeeID = hlmtEmpId), LoginModel.JSONDataRequest::class.java))

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
                sharedPref.edit().putString(PreferenceConstants.GENDER, if(loginData.gender.equals("Male",true))"1" else "2").apply()
                sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE, Constants.SELF_RELATIONSHIP_CODE).apply()
                sharedPref.edit().putString(PreferenceConstants.DOB,loginData.dateOfBirth).apply()
                sharedPref.edit().putString(PreferenceConstants.IS_HLMT_USER,loginData.IsHLMTUser).apply()
                sharedPref.edit().putString(PreferenceConstants.HLMT_USER_ID,loginData.HLMTUserID).apply()
                sharedPref.edit().putString(PreferenceConstants.HLMT_USERNAME,loginData.HLMTUserName).apply()
                sharedPref.edit().putString(PreferenceConstants.ACCOUNT_LINK_STATUS,loginData.accountLinkStatus).apply()

                val pid = loginData?.personID?.toDouble()?.toInt()
                Timber.i("Person Id => "+pid)
                sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString()).apply()
                sharedPref.edit().putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString()).apply()
                // Added by Rohit
                //RealPathUtil.creatingLocalDirctories()
                    saveUserData(loginData)
                    navigate(HlmtLoginFragmentDirections.actionHlmtloginfragmentToMainactivity())

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

    fun fetchHLMT360LoginResponse(username: String, passwordStr: String = "") = viewModelScope.launch(dispatchers.main){

        val requestData = HLMTLoginModel(Gson().toJson(
            HLMTLoginModel.JSONDataRequest(username = username,password = passwordStr,accountID = "",mode = "LOGIN"), HLMTLoginModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Validating Username..")
        _hlmt360LoginResponse.removeSource(hlmt360LoginUserSource)
        withContext(dispatchers.io){ hlmt360LoginUserSource = userManagementUseCase.invokeHLMT360LoginResponse(true,requestData)}
        _hlmt360LoginResponse.addSource(hlmt360LoginUserSource){
            _hlmt360LoginResponse.value = it.data

            if (it.status == Resource.Status.SUCCESS){
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.data!= null && !it.data!!.HLMTUserID.isNullOrEmpty()) {
                    if (!isAccountExist) {
                        navigate(
                            HlmtLoginFragmentDirections.actionLoginFragmentToUserDetailFragment(
                                hlmtEmployeeID = username,
                                hlmtUserID = it.data!!.HLMTUserID.toString(),
                                loginStatus = it.data!!.loginStatus.toString(),
                                isRegister = "true",
                                mobileNo = ""
                            )
                        )
                    } else {
                        fetchLoginResponse(
                            hlmtLoginStatus = it.data!!.loginStatus.toString(),
                            hlmtUserId = it.data!!.HLMTUserID.toString(),
                            hlmtEmpId = username,
                            username = ""
                        )
                    }
                }else{
                    _toastMessage.value = Event("Unable to connect, please try again")
                }
                Timber.i("Data=> "+it)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun fetchRegistrationResponse(name: String = "", phoneNumber: String, passwordStr: String = "",gender:String,dob:String,emailStr: String,fName:String,imgPath:String,
    hlmtUserId:String="",hlmtLoginStatus:String="",hlmtEmpId:String="") = viewModelScope.launch(dispatchers.main){

        if(validateData(name.trim(),phoneNumber.trim(),emailStr.trim(),dob.trim(),hlmtEmpId.trim())) {
            val requestData = LoginModel(
                Gson().toJson(
                    LoginModel.JSONDataRequest(
                        mode = "REGISTER",
                        name = name,
                        phoneNumber = phoneNumber,
                        password = passwordStr,
                        gender = gender,
                        dateOfBirth = dob,
                        emailAddress = emailStr,
                        hlmtUserID = hlmtUserId,
                        hlmtLoginStatus = hlmtLoginStatus,
                        employeeID = hlmtEmpId
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
                    if(it.data != null) {
                        var loginData = it.data?.response?.loginData!!
                        sharedPref.edit().putBoolean(PreferenceConstants.IS_LOGIN, true).apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.EMAIL, loginData.emailAddress)
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.PHONE, loginData.phoneNumber)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.TOKEN, loginData.context)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.ACCOUNTID, loginData.accountID.toString())
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.FIRSTNAME, loginData.name)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.DOB, loginData.dateOfBirth)
                            .apply()
                        sharedPref.edit().putString(PreferenceConstants.GENDER, if(loginData.gender.equals("Male",true))"1" else "2").apply()
                        sharedPref.edit().putString(
                            PreferenceConstants.RELATIONSHIPCODE,
                            Constants.SELF_RELATIONSHIP_CODE
                        ).apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.IS_HLMT_USER, loginData.IsHLMTUser)
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.ACCOUNT_LINK_STATUS, loginData.accountLinkStatus)
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.HLMT_USER_ID, loginData.HLMTUserID)
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.HLMT_USERNAME, loginData.HLMTUserName)
                            .apply()

                        val pid = loginData?.personID?.toDouble()?.toInt()
                        Timber.i("Person Id => " + pid)
                        sharedPref.edit().putString(PreferenceConstants.PERSONID, pid.toString())
                            .apply()
                        sharedPref.edit()
                            .putString(PreferenceConstants.ADMIN_PERSON_ID, pid.toString())
                            .apply()
                        // Added by Rohit
                        //RealPathUtil.creatingLocalDirctories()
                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.NON_HLMT_REGISTRATION_SUCCESSFUL_EVENT)
                        saveUserData(loginData)
                        if (!Utilities.isNullOrEmpty(fName)
                            && !Utilities.isNullOrEmpty(imgPath)
                        ) {
                            callUploadProfileImageApi(
                                pid.toString(),
                                loginData.context,
                                fName,
                                imgPath
                            )
                        } else {
                            navigate(UserDetailsFragmentDirections.actionUserDetailsToHomeScreen())
                        }
                    }else{
                        _toastMessage.value = Event("Unable to connect, please try again")
                    }
                }else{
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.NON_HLMT_REGISTRATION_FAIL_EVENT)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.NON_HLMT_REGISTRATION_FAIL_EVENT)
                }
            }
        }
    }

    fun callUploadProfileImageApi( personId:String,authToken:String,name:String, path:String)
            = viewModelScope.launch(dispatchers.main) {

        var encodedImage =""
        try {
            val file = File(path , name )
            val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
            if (bitmap != null) {
                val bytesFile = ByteArray(file.length().toInt())
                val fileInputStream = FileInputStream(file)
                fileInputStream.read(bytesFile)
                encodedImage = Base64.encodeToString(bytesFile, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val PersonID = RequestBody.create(MediaType.parse("text/plain"), personId)
        val FileName = RequestBody.create(MediaType.parse("text/plain"), name)
        val DocumentTypeCode = RequestBody.create(MediaType.parse("text/plain"), "PROFPIC")
        val ByteArray = RequestBody.create(MediaType.parse("text/plain"), encodedImage)
        val AuthTicket = RequestBody.create(MediaType.parse("text/plain"), authToken)
        //Utilities.deleteFileFromLocalSystem(cropImgPath)

        _progressBar.value = Event("Uploading Profile Photo.....")
        _uploadProfileImage.removeSource(uploadProfileImageSource)
        withContext(dispatchers.io) {
            uploadProfileImageSource = userManagementUseCase.invokeUploadProfileImage(PersonID,FileName,DocumentTypeCode,ByteArray,AuthTicket)
        }
        _uploadProfileImage.addSource(uploadProfileImageSource) {
            _uploadProfileImage.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    val profileImageID = it.data!!.profileImageID
                    Timber.i("UploadProfileImage----->$profileImageID")
                    if ( !Utilities.isNullOrEmptyOrZero(profileImageID) ) {
                        updateUserProfileImgPath( name , path )
                        navigate(UserDetailsFragmentDirections.actionUserDetailsToHomeScreen())
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

    private fun updateUserProfileImgPath(name : String, path : String ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            userManagementUseCase.invokeUpdateUserProfileImgPath( name , path )
        }
    }

    private fun validateData(
        name: String,
        phoneNumber: String,
        emailStr: String,
        dob: String,
        hlmtEmpId: String
    ): Boolean {
        var isValid = false
        val emailPattern:Regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        val namePattern:Regex = "^[a-zA-Z\\s]*\$".toRegex()

        if (name.isNullOrEmpty() || name.length<=3 || !name.matches(namePattern)){
            toastMessage("Please Enter Valid Name.")
        }else if((phoneNumber.isNullOrEmpty() || !Validation.isValidPhoneNumber(phoneNumber)) && hlmtEmpId.isEmpty()){
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

    private fun validateLoginData(username: String, passwordStr: String): Boolean {
        val emailPattern:Regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

        var isValidate:Boolean = false
        if (username.isNullOrEmpty() || !username.matches(emailPattern)){
            toastMessage("Please enter valid user ID.")
        }else if(passwordStr.isNullOrEmpty()){
            toastMessage("Please enter valid password.")
        }else if(passwordStr.length < 6){
            toastMessage("Please enter valid password.")
        }else{
            isValidate = true
        }
        return isValidate
    }

    private fun saveUserData(usr: LoginModel.Data)= viewModelScope.launch {
        var user = Users(accountId = usr.accountID,personId = usr.personID.toDouble().toInt(),firstName = usr.firstName,dateOfBirth = usr.dateOfBirth,gender = if(usr.gender.equals("Male",true))"1" else "2",age = usr.age
            ,emailAddress = usr.emailAddress,phoneNumber = usr.phoneNumber,path = "",authToken = usr.context,
            partnerCode = usr.partnerCode,accountStatus = usr.accountStatus,accountType = usr.accountType,countryName = "",
            dialingCode = usr.dialingCode, isActive = usr.isActive, isAuthenticated = usr.isAuthenticated,profileImgPath = "",
            maritalStatus = usr.maritalStatus,lastName = if(usr.lastName.isNullOrEmpty()) "" else usr.lastName,name = usr.name)

        withContext(dispatchers.io) {
            userManagementUseCase.invokeAddUserInfo(user)
        }
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

    fun getLoginStatus(): Boolean{
        return sharedPref.getBoolean(PreferenceConstants.IS_LOGIN,false)
    }

    fun callUpdateUserDetailsApi( name: String = "", phoneNumber: String, passwordStr: String = "",gender:String,dob:String,emailStr: String,fName:String) = viewModelScope.launch(dispatchers.main) {

        val person = UpdateUserDetailsModel.PersonRequest(
            id = sharedPref.getString(PreferenceConstants.PERSONID,"")!!.toInt(),
            firstName = "",
            dateOfBirth = "",
            gender = "1",
            contact = UpdateUserDetailsModel.Contact(
                emailAddress = emailStr,
                primaryContactNo = "",
                alternateEmailAddress = "",
                alternateContactNo = "",),
            address = UpdateUserDetailsModel.Address(
                addressLine1 = "")
        )

        val requestData = UpdateUserDetailsModel(Gson().toJson(
            UpdateUserDetailsModel.JSONDataRequest(
                personID = sharedPref.getString(PreferenceConstants.PERSONID,"")!!,
                person = person),
            UpdateUserDetailsModel.JSONDataRequest::class.java) , sharedPref.getString(PreferenceConstants.TOKEN,"")!! )

        _progressBar.value = Event("Updating Profile Details.....")
        _updateUserDetails.removeSource(updateUserDetailsSource)
        withContext(dispatchers.io) {
            updateUserDetailsSource = userManagementUseCase.invokeUpdateUserDetails(isForceRefresh = true, data = requestData)
        }
        _updateUserDetails.addSource(updateUserDetailsSource) {
            _updateUserDetails.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    Timber.e("UpdateUserDetails----->${it.data!!.person}")
//                    _toastMessage.value = Event(R.string.PRO)
//                    Utilities.toastMessageShort(context,context.resources.getString(R.string.PROFILE_UPDATED))
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

    fun updateUserPreference() {
        sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "cm").apply()
        sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "kg").apply()
        sharedPref.edit().putString(PreferenceConstants.IS_HLMT_USER,"false").apply()
        sharedPref.edit().putString(PreferenceConstants.HLMT_USER_ID,"").apply()
        sharedPref.edit().putString(PreferenceConstants.HLMT_USERNAME,"").apply()
        sharedPref.edit().putString(PreferenceConstants.ACCOUNT_LINK_STATUS,"false").apply()
    }

}