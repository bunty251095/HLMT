package com.caressa.home.viewmodel

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.home.R
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.PermissionUtil
import com.caressa.common.utils.Utilities
import com.caressa.home.common.DataHandler
import com.caressa.home.common.DataHandler.DashboardFeature
import com.caressa.home.common.DataHandler.NavDrawerOption
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.home.ui.HlmtDashboardFragmentDirections
import com.caressa.home.ui.HomeMainActivity
import com.caressa.home.ui.PasswordChangeActivity
import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.UserRelatives
import com.caressa.model.entity.Users
import com.caressa.model.home.ContactUsModel
import com.caressa.model.home.PasswordChangeModel
import com.caressa.model.home.SaveFeedbackModel
import com.caressa.model.security.HLMTLoginModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
class DashboardViewModel(private val homeManagementUseCase: HomeManagementUseCase, private val dispatchers: AppDispatchers,
                         private val sharedPref: SharedPreferences , private val dataHandler : DataHandler,
                         val context: Context) : BaseViewModel() {

    var personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    private var accountID = sharedPref.getString(PreferenceConstants.ACCOUNTID,"")!!
    private var email = sharedPref.getString(PreferenceConstants.EMAIL,"")!!
    private var phone = sharedPref.getString(PreferenceConstants.PHONE,"")!!
    private var firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME, "")!!
    private var gender = sharedPref.getString(PreferenceConstants.GENDER, "")!!
    private var authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!

    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val currentSelectedPerson = MutableLiveData<UserRelatives>()
    val trackersList = MutableLiveData<List<DashboardFeature>>()
    val facilitiesAndResourcesList = MutableLiveData<List<DashboardFeature>>()
    val dashboardFeatureList = MutableLiveData<List<DataHandler.DashboardFeatureGrid>>()
    val settingsOptionList = MutableLiveData<List<DataHandler.Option>>()
    val navDrawerOptionList = MutableLiveData<List<NavDrawerOption>>()
    var hraDetails = MutableLiveData<HRASummary>()

    private var saveFeedbackSource: LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> = MutableLiveData()
    private val _saveFeedback = MediatorLiveData<SaveFeedbackModel.SaveFeedbackResponse>()
    val saveFeedback: LiveData<SaveFeedbackModel.SaveFeedbackResponse> get() = _saveFeedback

    private var passwordChangeSource: LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> = MutableLiveData()
    private val _passwordChange = MediatorLiveData<PasswordChangeModel.ChangePasswordResponse>()
    val passwordChange: LiveData<PasswordChangeModel.ChangePasswordResponse> get() = _passwordChange

    private val _hlmt360LoginResponse = MediatorLiveData<HLMTLoginModel.LoginResponse>()
    val hlmt360LoginResponse: LiveData<HLMTLoginModel.LoginResponse> get() = _hlmt360LoginResponse
    var hlmt360LoginUserSource: LiveData<Resource<HLMTLoginModel.LoginResponse>> = MutableLiveData()

    private var contactUsSource: LiveData<Resource<ContactUsModel.ContactUsResponse>> = MutableLiveData()
    private val _contactUs = MediatorLiveData<ContactUsModel.ContactUsResponse>()
    val contactUs: LiveData<ContactUsModel.ContactUsResponse> get() = _contactUs
    var hraSummary: HRASummary? =null
    var stepsData: String =""

    fun getLoggedInPersonDetails( ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun getUserRelatives() = viewModelScope.launch {
        withContext(dispatchers.io) {
/*            val list = homeManagementUseCase.invokeGetUserRelatives().toMutableList()
            // Set Selected Member to First in the List
            val relative = list.first { it.relativeID == sharedPref.getString(PreferenceConstants.PERSONID,"") }
            list.remove(relative)
            list.add(0,relative)
            Timber.e("Updated Relative List--->$list")*/
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getDrawerOptionList() {
        if (isSelfUser()) {
            navDrawerOptionList.postValue(dataHandler.getNavDrawerList())
        }else{
            navDrawerOptionList.postValue(dataHandler.getSwitchProfileNavDrawerList())
        }
    }

    fun refreshDashboardFeatureList() {
        if (isSelfUser()) {
            dashboardFeatureList.postValue(dataHandler.getDashboardList(hraSummary,stepsData))
        }else{
            dashboardFeatureList.postValue(dataHandler.getSwitchProfileDashboardList(hraSummary,stepsData))
        }
    }

    fun getSettingsOptionList1() {
        settingsOptionList.postValue( dataHandler.getSettingsOptionList() )
    }

    fun getHraSummaryDetails() = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            hraDetails.postValue( homeManagementUseCase.invokeGetHraSummaryDetails() )
        }
    }

    fun navigateToHospitalsNearMe( context: Context ) {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?z=10&q=hospital near me")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.apps.maps")))
        }
    }

    fun switchProfile( userRelative: UserRelatives )  {
        sharedPref.edit().putString(PreferenceConstants.PERSONID,userRelative.relativeID).apply()
        sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE,userRelative.relationshipCode).apply()
        sharedPref.edit().putString(PreferenceConstants.EMAIL,userRelative.emailAddress).apply()
        sharedPref.edit().putString(PreferenceConstants.PHONE,userRelative.contactNo).apply()
        sharedPref.edit().putString(PreferenceConstants.FIRSTNAME,userRelative.firstName).apply()
        sharedPref.edit().putString(PreferenceConstants.GENDER,userRelative.gender).apply()
        sharedPref.edit().putString(PreferenceConstants.AGE,userRelative.age).apply()
        sharedPref.edit().putString(PreferenceConstants.DOB,userRelative.dateOfBirth).apply()
        clearTablesForSwitchProfile()
    }

    private fun clearTablesForSwitchProfile() = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            homeManagementUseCase.invokeClearTablesForSwitchProfile()
        }
    }

    fun refreshPersonId(){
        personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
        email = sharedPref.getString(PreferenceConstants.EMAIL,"")!!
        phone = sharedPref.getString(PreferenceConstants.PHONE,"")!!
        firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME,"")!!
        gender = sharedPref.getString(PreferenceConstants.GENDER,"")!!
    }

    fun isSelfUser(): Boolean {
        return sharedPref.getString(PreferenceConstants.PERSONID,"") == sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,"")
    }

    fun getDOBOfPerson() = viewModelScope.launch(dispatchers.main){
        withContext(dispatchers.io) {
           val user = homeManagementUseCase.invokeGetUserRelativeDetailsByRelativeId(sharedPref.getString(PreferenceConstants.PERSONID,"")!!)
            currentSelectedPerson.postValue(user)
        }

    }

    fun goToHRA() {
        try {
//            val dob = user.dateOfBirth
//            if (dob.isNullOrEmpty()){
//                toastMessage("HRA is allowed for 18+ members only")
//            }else {
//                if(DateHelper.isDateAbove18Years(dob)) {
                    if (hraDetails.value != null) {
                        val hraSummary = hraDetails.value
                        val currentHRAHistoryID = hraSummary?.currentHRAHistoryID.toString()
                        val wellnessScore = hraSummary?.scorePercentile.toString()
                        val hraCutOff = hraSummary?.hraCutOff
                        if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && currentHRAHistoryID != "0") {
                            if (hraCutOff.equals("0")) {
                                navigateToHraStart()
                            } else if (!Utilities.isNullOrEmpty(wellnessScore)) {
                                navigateToHraSummary()
                            } else {
                                navigateToHraStart()
                            }
                        } else {
                            navigateToHraStart()
                        }
                    } else {
                        navigateToHraStart()
                    }
//                }else{
//                    toastMessage("HRA is allowed for 18+ members only")
//                }
//            }
        }catch (e:Exception){e.printStackTrace()}
    }

    private fun navigateToHraStart( ) {
        navigate(HlmtDashboardFragmentDirections.actionDashboardFragmentToHraActivity())
    }

    private fun navigateToHraSummary( ) {
        navigate(HlmtDashboardFragmentDirections.actionDashboardFragmentToHraSummaryActivity2())
    }

    fun callSaveFeedbackApi( context: Context,feedback : String) = viewModelScope.launch(dispatchers.main) {

        val deviceName = Build.BRAND + "~" + Build.MODEL
        val requestData = SaveFeedbackModel(Gson().toJson(
                SaveFeedbackModel.JSONDataRequest(
                    SaveFeedbackModel.AppFeedback(
                        app = Configuration.strAppIdentifier,
                        appVersion = Utilities.getAppVersion(context).toString(),
                        deviceType = "ANDROID",
                        deviceName = deviceName,
                        personID = personId.toInt(),
                        accountID = accountID.toInt(),
                        emailID = email,
                        phoneNumber = phone,
                        type = "Feedback",
                        feedback = feedback)),
                SaveFeedbackModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Saving Feedback...")
        _saveFeedback.removeSource(saveFeedbackSource)
        withContext(dispatchers.io) {
            saveFeedbackSource = homeManagementUseCase.invokeSaveFeedback(isForceRefresh = true, data = requestData)
        }
        _saveFeedback.addSource(saveFeedbackSource) {
            _saveFeedback.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val appFeedback = it.data!!.appFeedback
                    if ( !Utilities.isNullOrEmptyOrZero( appFeedback.id.toString() ) ) {
                        Utilities.toastMessageShort(context, "Feedback Submitted Successfully")
                        val intentToPass = Intent()
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intentToPass)
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

    fun callPasswordChangeApi( oldPassword:String,newPassword:String,activity: PasswordChangeActivity) = viewModelScope.launch(dispatchers.main) {

        val requestData = PasswordChangeModel(Gson().toJson(
            PasswordChangeModel.JSONDataRequest(
                loginName = email,
                emailAddress = email,
                primaryPhone = phone,
                oldPassword = oldPassword,
                newPassword = newPassword),
            PasswordChangeModel.JSONDataRequest::class.java)  )

        _progressBar.value = Event("Updating Password...")
        _passwordChange.removeSource(passwordChangeSource)
        withContext(dispatchers.io) {
            passwordChangeSource = homeManagementUseCase.invokePasswordChange(isForceRefresh = true, data = requestData)
        }
        _passwordChange.addSource(passwordChangeSource) {
            _passwordChange.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    if ( newPassword.equals(it.data!!.newPassword,ignoreCase = true) ) {
                        activity.showPasswordUpdatedDialog()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                when {
                    it.errorNumber.equals("1100014",true) -> {
                        _sessionError.value = Event(true)
                    }
                    it.errorNumber.equals("1100005",true) -> {
                        toastMessage( activity.resources.getString(R.string.ERROR_INVALID_OLD_PASSWORD) )
                    }
                    else -> {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    }

    fun callContactUsApi(context:Context,fromEmail:String,fromMobile:String,message:String) = viewModelScope.launch(dispatchers.main) {

        val requestData = ContactUsModel(Gson().toJson(ContactUsModel.JSONDataRequest(
            emailAddress = email,
            fromEmail = fromEmail,
            fromMobile = fromMobile,
            message = message),
            ContactUsModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Posting Your Message...")
        _contactUs.removeSource(contactUsSource)
        withContext(dispatchers.io) {
            contactUsSource = homeManagementUseCase.invokeContactUs(isForceRefresh = true, data = requestData)
        }
        _contactUs.addSource(contactUsSource) {
            _contactUs.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    if ( it.errorNumber.equals("0",true) ) {
                        toastMessage( context.resources.getString(R.string.MSG_CONTACT_US_SUCCESS) )
                        val intentToPass = Intent()
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intentToPass)
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

    fun navigateToMyProfileActivityWithStoragePermission(activity:HomeMainActivity,listener: PermissionUtil.AppPermissionListener) {
        val isGranted = PermissionUtil().getInstance()!!.checkStoragePermissionFromActivity(
            listener,activity,activity)
        if (isGranted) {
            navigateToMyProfileActivity()
        }
    }

    fun navigateToLinkAccountActivity() {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LINK_ACCOUNT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigateToMyProfileActivity() {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MY_PROFILE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigateToFamilyProfileActivity() {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigateToFamilyDoctorsActivity() {
        val intent = Intent()
        intent.putExtra(Constants.FROM, "Home")
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_DOCTOR)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigateToContactUsActivity() {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.CONTACT_US)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun navigateToSettingsActivity() {
        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun spreadTheWord() {
        try {
            var himOrHer = ""
            val userName = firstName
            val appName = "HLMT"

            when(gender) {
                "1" -> himOrHer = " him "
                "2" -> himOrHer = " her "
            }

            val title: String = userName + " has invited you to join "+appName+" !"
            /*val text = "Hello," + "\n\n" +  title + "\n\n" + "Join" + himORher
                    + "on Vivant, the Health & Wellness App that helps you to Store, Track and Manage your Health Data." +
                    "\n\n" + "Track your steps, get your wellness score, receive personalised recommendations and health goal, chat with a Doctor, all for free !!" +
                    "\n\n" + "What`s more! Earn and redeem rewards while managing your health from our wellness partners." +
                    Html.fromHtml("<br><br>" + "Application Link : " +
                            "<a href=\"https://wk2w7.app.goo.gl/reffral\">https://wk2w7.app.goo.gl/reffral</a> " + "</br></br>")
                    + Html.fromHtml("<br><br>" + "Team Vivant</br><br>" +
                    "<a href=\"https://vivant.me\">www.vivant.vivant.me</a></br></br>");*/
            val text = "Hello," + "\n\n" +  title + "\n\n" + "Join" + himOrHer + "on "+appName+", the Health & Wellness App that helps you to Store, Track and Manage your Health Data." +
                    "\n\n" + "Track your steps, get your wellness score, receive personalised recommendations and health goal, chat with a Doctor, all for free !!" +
                    "\n\n" + "What`s more! Earn and redeem rewards while managing your health from our wellness partners." /*+
                    Html.fromHtml("<br><br>" + "Play Store : " +
                            "<a href=\"https://bit.ly/33d2un5\">https://bit.ly/33d2un5</a> " + "</br></br>") +
                    Html.fromHtml("<br><br>" + " App Store : " +
                            "<a href=\"https://apple.co/3jZI72s\">https://apple.co/3jZI72s</a> " + "</br></br>")+
                    Html.fromHtml("<br><br>" + "Team Vivant</br><br>" + "<a href=\"https://vivant.me\">www.vivant.vivant.me</a></br></br>")*/

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPreference(type: String): String {
        if (type.equals("HEIGHT",true)){
            return sharedPref.getString(PreferenceConstants.HEIGHT_PREFERENCE,"cm")!!
        }else if(type.equals("WEIGHT",true)){
            return sharedPref.getString(PreferenceConstants.WEIGHT_PREFERENCE,"kg")!!
        }else if(type.equals("ISHLMT",true)){
            return sharedPref.getString(PreferenceConstants.IS_HLMT_USER,"false")!!
        }
        return ""
    }

    fun fetchHLMT360LoginResponse(username: String, passwordStr: String = "") = viewModelScope.launch(dispatchers.main){

        if(validateLoginData(username,passwordStr)) {
            val requestData = HLMTLoginModel(
                Gson().toJson(
                    HLMTLoginModel.JSONDataRequest(
                        username = username,
                        password = passwordStr,
                        accountID = accountID,
                        mode = "LINKACCOUNT"
                    ), HLMTLoginModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Validating Username..")
            _hlmt360LoginResponse.removeSource(hlmt360LoginUserSource)
            withContext(dispatchers.io) {
                hlmt360LoginUserSource =
                    homeManagementUseCase.invokeHLMT360LoginResponse(true, requestData)
            }
            _hlmt360LoginResponse.addSource(hlmt360LoginUserSource) {
                _hlmt360LoginResponse.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    sharedPref.edit()
                        .putString(PreferenceConstants.IS_HLMT_USER, it.data!!.isHLMTUser)
                    sharedPref.edit()
                        .putString(PreferenceConstants.HLMT_USERNAME, it.data!!.hlmtUserName)
                    sharedPref.edit()
                        .putString(PreferenceConstants.HLMT_USER_ID, it.data!!.HLMTUserID)
                    Timber.i("Data=> " + it)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }
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


}
