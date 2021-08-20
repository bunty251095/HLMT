package com.caressa.home.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.home.ui.ProfileAndFamilyMember.*
import com.caressa.model.entity.UserRelatives
import com.caressa.model.entity.Users
import com.caressa.model.home.*
import com.caressa.model.home.AddRelativeModel.*
import com.caressa.model.security.PhoneExistsModel
import com.caressa.model.shr.ListRelativesModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

@SuppressLint("StaticFieldLeak")
class ProfileFamilyMemberViewModel(private val homeManagementUseCase: HomeManagementUseCase,
                                   private val dispatchers: AppDispatchers,
                                   private val sharedPref: SharedPreferences,
                                   private val dataHandler : DataHandler,
                                    val context: Context) : BaseViewModel() {

    val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!

    var requestData : UpdateRelativeModel? = null
    var relativeToRemove : List<UserRelatives> = listOf()

    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val alreadyExistRelatives = MutableLiveData<List<UserRelatives>>()
    var familyRelationList = MutableLiveData<List<DataHandler.FamilyRelationOption>>()

    var userProfileDetailsSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> = MutableLiveData()
    val _userProfileDetails = MediatorLiveData<UserDetailsModel.UserDetailsResponse>()
    val userProfileDetails: LiveData<UserDetailsModel.UserDetailsResponse> get() = _userProfileDetails

    var updateUserDetailsSource: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> = MutableLiveData()
    val _updateUserDetails = MediatorLiveData<UpdateUserDetailsModel.UpdateUserDetailsResponse>()
    val updateUserDetails: LiveData<UpdateUserDetailsModel.UpdateUserDetailsResponse> get() = _updateUserDetails

    var profileImageSource: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> = MutableLiveData()
    val _profileImage = MediatorLiveData<ProfileImageModel.ProfileImageResponse>()
    val profileImage: LiveData<ProfileImageModel.ProfileImageResponse> get() = _profileImage

    var uploadProfileImageSource: LiveData<Resource<UploadProfileImageResponce>> = MutableLiveData()
    val _uploadProfileImage = MediatorLiveData<UploadProfileImageResponce>()
    val uploadProfileImage: LiveData<UploadProfileImageResponce> get() = _uploadProfileImage

    var removeProfileImageSource: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> = MutableLiveData()
    val _removeProfileImage = MediatorLiveData<RemoveProfileImageModel.RemoveProfileImageResponse>()
    val removeProfileImage: LiveData<RemoveProfileImageModel.RemoveProfileImageResponse> get() = _removeProfileImage

    var addRelativeSource: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> = MutableLiveData()
    val _addRelative = MediatorLiveData<AddRelativeModel.AddRelativeResponse>()
    val addRelative: LiveData<AddRelativeModel.AddRelativeResponse> get() = _addRelative

    var updateRelativeSource: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> = MutableLiveData()
    val _updateRelative = MediatorLiveData<UpdateRelativeModel.UpdateRelativeResponse>()
    val updateRelative: LiveData<UpdateRelativeModel.UpdateRelativeResponse> get() = _updateRelative

    var removeRelativeSource: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> = MutableLiveData()
    val _removeRelative = MediatorLiveData<RemoveRelativeModel.RemoveRelativeResponse>()
    val removeRelative: LiveData<RemoveRelativeModel.RemoveRelativeResponse> get() = _removeRelative

    var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> = MutableLiveData()
    val _listRelatives = MediatorLiveData<ListRelativesModel.ListRelativesResponse>()
    val listRelatives: LiveData<ListRelativesModel.ListRelativesResponse> get() = _listRelatives

    var phoneExistSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> = MutableLiveData()
    val _phoneExist = MediatorLiveData<PhoneExistsModel.IsExistResponse>()
    val phoneExist: LiveData<PhoneExistsModel.IsExistResponse> get() = _phoneExist

    fun callAddNewRelativeApi(forceRefresh: Boolean,userRelative:UserRelatives,from:String,fragment:AddFamilyMemberFragment) = viewModelScope.launch(dispatchers.main) {

        val contact = AddRelativeModel.Contact( userRelative.emailAddress , userRelative.contactNo )
        val relationships: ArrayList<Relationship> = ArrayList()
        relationships.add( Relationship( personId , userRelative.relationshipCode ) )
        var gender = ""
        if ( userRelative.gender.equals("Male",ignoreCase = true) ) {
            gender = "1"
        } else if ( userRelative.gender.equals("Female",ignoreCase = true) ) {
            gender = "2"
        }

        val requestData = AddRelativeModel(Gson().toJson(AddRelativeModel.JSONDataRequest(
            personID = personId , person =  AddRelativeModel.Person(
                firstName = userRelative.firstName , relativeID = userRelative.relativeID ,
                dateOfBirth = userRelative.dateOfBirth , gender = gender ,
                isProfileImageChanges = Constants.FALSE , contact = contact ,
                relationships = relationships )), AddRelativeModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Adding Family Member.....")
        _addRelative.removeSource(addRelativeSource)
        withContext(dispatchers.io) {
            addRelativeSource = homeManagementUseCase.invokeaddNewRelative(isForceRefresh = forceRefresh, data = requestData)
        }
        _addRelative.addSource(addRelativeSource) {
            _addRelative.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it != null ) {
                    val newRelative = it.data!!.person
                    if ( !Utilities.isNullOrEmpty( newRelative.id.toString() ) ) {
                        toastMessage(context.resources.getString(R.string.MEMBER_ADDED))
                        Timber.e("from----->$from")
                        if ( from.equals(Constants.HRA,ignoreCase = true) ) {
                            fragment.navigateToHRA()
                        } else {
                            navigate(AddFamilyMemberFragmentDirections.actionAddFamilyMemberFragmentToFamilyMembersListFragment())
                        }
                    }
                }
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD_SUCCESS)
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    navigate(AddFamilyMemberFragmentDirections.actionAddFamilyMemberFragmentToFamilyMembersListFragment())
                    toastMessage(it.errorMessage)
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD_FAILED)
                }
            }
        }

    }

    fun callListRelativesApi(forceRefresh: Boolean,fragment: FamilyMembersListFragment) = viewModelScope.launch(dispatchers.main) {

        val requestData = ListRelativesModel(Gson().toJson(ListRelativesModel.JSONDataRequest(
                personID = personId ), ListRelativesModel.JSONDataRequest::class.java) , authToken )

        _listRelatives.removeSource(listRelativesSource)
        withContext(dispatchers.io) {
            listRelativesSource = homeManagementUseCase.invokeRelativesList(isForceRefresh = forceRefresh, data = requestData )
        }
        _listRelatives.addSource(listRelativesSource) {
            _listRelatives.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val relativesList = it.data!!.relativeList
                    if ( relativesList.size > 1 ) {

                        val userRelatives: MutableList<UserRelatives> = mutableListOf()
                        for ( i in relativesList ) {
                            if ( i.relationshipCode != "SELF" )
                            userRelatives.add(i)
                        }
                        userRelativesList.postValue(userRelatives)
                    } else {
                        fragment.noDataView()
                    }
                    Timber.i("RelativesList----->"+relativesList.size)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callRemoveRelativesApi(forceRefresh: Boolean , relativeId : String , relationshipId : String) = viewModelScope.launch(dispatchers.main) {

        val relatives: ArrayList<Int> = ArrayList()
        withContext(dispatchers.io) {
             relativeToRemove = homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId)
        }

        for ( i in relativeToRemove ) {
            relatives.add(relationshipId.toInt())
        }

        val requestData = RemoveRelativeModel(
            Gson().toJson(RemoveRelativeModel.JSONDataRequest(
                id = relatives ), RemoveRelativeModel.JSONDataRequest::class.java) ,
            authToken )

        _progressBar.value = Event("Deleting Family Member.....")
        _removeRelative.removeSource(removeRelativeSource)
        withContext(dispatchers.io) {
            removeRelativeSource = homeManagementUseCase.invokeRemoveRelative(isForceRefresh = forceRefresh, data = requestData)
        }
        _removeRelative.addSource(removeRelativeSource) {
            _removeRelative.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                toastMessage(context.resources.getString(R.string.MEMBER_DELETED))
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                    navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                }
            }
        }

    }

    fun callCheckPhoneExistApi( username : String ,phone: String )
            = viewModelScope.launch(dispatchers.main) {

        val requestData = PhoneExistsModel(Gson().toJson(PhoneExistsModel.JSONDataRequest(
            primaryPhone = phone), PhoneExistsModel.JSONDataRequest::class.java))

        _progressBar.value = Event("Verifing Phone Number...")
        _phoneExist.removeSource(phoneExistSource)
        withContext(dispatchers.io) { phoneExistSource = homeManagementUseCase.invokePhoneExist(true, requestData) }
        _phoneExist.addSource(phoneExistSource) {
            _phoneExist.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data!!.isExist.equals(Constants.TRUE, true)) {
                    toastMessage(context.resources.getString(R.string.ERROR_MOBILE_ALREADY_REGISTERED))
                } else if (it.data!!.isExist.equals(Constants.FALSE, true)) {

                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callUpdateProfileApi( forceRefresh: Boolean,username : String,phoneNumber : String )
            = viewModelScope.launch(dispatchers.main) {

        withContext(dispatchers.io) {
            val userDetails = homeManagementUseCase.invokeGetLoggedInPersonDetails()
            try {
                val dob = DateHelper.formatDateValue(DateHelper.SERVER_DATE_YYYYMMDD, userDetails.dateOfBirth)!!
                requestData = UpdateRelativeModel(Gson().toJson(UpdateRelativeModel.JSONDataRequest(
                    personID = personId ,
                    person = UpdateRelativeModel.Person(
                        id = personId.toInt() ,
                        firstName = username,
                        lastName = "" ,
                        dateOfBirth = dob ,
                        gender = userDetails.gender ,
                        isProfileImageChanges = Constants.FALSE ,
                        contact = UpdateRelativeModel.Contact(
                            emailAddress = userDetails.emailAddress ,
                            primaryContactNo = phoneNumber))), UpdateRelativeModel.JSONDataRequest::class.java) , authToken )
            } catch ( e : Exception ) {
                e.printStackTrace()
            }
        }
        _progressBar.value = Event("Updating Profile.....")
        _updateRelative.removeSource(updateRelativeSource)
        withContext(dispatchers.io) {
            updateRelativeSource = homeManagementUseCase.invokeupdateRelative(isForceRefresh = forceRefresh, data = requestData!!)
        }
        _updateRelative.addSource(updateRelativeSource) {
            _updateRelative.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it != null ) {
                    val personDetails = it.data!!.person
                    Timber.i("PersonId-----> ${personDetails.id}")
                    Timber.i("UpdatedName-----> ${personDetails.firstName}")
                    if ( !Utilities.isNullOrEmpty( personDetails.id.toString() ) ) {
                        updateUserDetails(personDetails.firstName,personDetails.id)
                        toastMessage(context.resources.getString(R.string.PROFILE_UPDATED))
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

    fun callUpdateRelativesApi(forceRefresh: Boolean , relative : UserRelatives ,from:String) = viewModelScope.launch(dispatchers.main) {

        val requestData = UpdateRelativeModel(Gson().toJson(UpdateRelativeModel.JSONDataRequest(
                personID = personId ,
                person = UpdateRelativeModel.Person(
                    id = relative.relativeID.toInt() ,
                    firstName = relative.firstName ,
                    lastName = "" ,
                    dateOfBirth = relative.dateOfBirth ,
                    gender = relative.gender ,
                    isProfileImageChanges = Constants.FALSE ,
                    contact = UpdateRelativeModel.Contact(
                        emailAddress = relative.emailAddress ,
                        primaryContactNo = relative.contactNo))), UpdateRelativeModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Updating Relative Profile.....")
        _updateRelative.removeSource(updateRelativeSource)
        withContext(dispatchers.io) {
            updateRelativeSource = homeManagementUseCase.invokeupdateRelative(isForceRefresh = forceRefresh, data = requestData)
        }
        _updateRelative.addSource(updateRelativeSource) {
            _updateRelative.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it != null ) {
                    val personDetails = it.data!!.person
                    if ( from == Constants.RELATIVE ) {
                        if ( !Utilities.isNullOrEmpty( personDetails.id.toString() ) ) toastMessage(context.resources.getString(R.string.PROFILE_UPDATED))
                        navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                    } else if ( from == Constants.USER ) {
                        val newNumber = personDetails.contact.primaryContactNo
                        if ( !Utilities.isNullOrEmpty( newNumber ) ) {
                            //updateUserMobile(newNumber)
                            getLoggedInPersonDetails()
                            toastMessage(context.resources.getString(R.string.MOBILE_UPDATED))
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
                    if ( from == Constants.RELATIVE ) {
                        navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                    }
                }
            }
        }
    }

    fun callGetUserDetailsApi() = viewModelScope.launch(dispatchers.main) {

        val requestData = UserDetailsModel(Gson().toJson(
            UserDetailsModel.JSONDataRequest(
                UserDetailsModel.PersonIdentificationCriteria(
                    personId = personId.toInt())),
            UserDetailsModel.JSONDataRequest::class.java) , authToken )

        _userProfileDetails.removeSource(userProfileDetailsSource)
        withContext(dispatchers.io) {
           userProfileDetailsSource = homeManagementUseCase.invokeGetUserDetails(isForceRefresh = true, data = requestData)
        }
        _userProfileDetails.addSource(userProfileDetailsSource) {
            _userProfileDetails.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val person = it.data!!.person
                    Timber.e("GetUserDetails----->$person")
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

    fun callUpdateUserDetailsApi( person : UpdateUserDetailsModel.PersonRequest ) = viewModelScope.launch(dispatchers.main) {

        val requestData = UpdateUserDetailsModel(Gson().toJson(
            UpdateUserDetailsModel.JSONDataRequest(
                personID = personId,
                person = person),
            UpdateUserDetailsModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Updating Profile Details.....")
        _updateUserDetails.removeSource(updateUserDetailsSource)
        withContext(dispatchers.io) {
            updateUserDetailsSource = homeManagementUseCase.invokeUpdateUserDetails(isForceRefresh = true, data = requestData)
        }
        _updateUserDetails.addSource(updateUserDetailsSource) {
            _updateUserDetails.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if ( it != null ) {
                    val personDetails = it.data!!.person
                    Timber.e("UpdateUserDetails----->${it.data!!.person}")
                    Timber.i("PersonId-----> ${personDetails.id}")
                    Timber.i("UpdatedName-----> ${personDetails.firstName}")
                    if ( !Utilities.isNullOrEmpty( personDetails.id.toString() ) ) {
                        updateUserDetails(personDetails.firstName,personDetails.id)
                        Utilities.toastMessageShort(context,context.resources.getString(R.string.PROFILE_UPDATED))
                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.PROFILE_UPDATE_SUCCESS)
                    }
                }

            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.PROFILE_UPDATE_FAILED)
                }
            }
        }
    }

    fun callGetProfileImageApi( activity: MyProfileNewActivity,documentID : String ) = viewModelScope.launch(dispatchers.main) {

        val requestData = ProfileImageModel(Gson().toJson(
            ProfileImageModel.JSONDataRequest(
                documentID = documentID),
            ProfileImageModel.JSONDataRequest::class.java) , authToken )

        _profileImage.removeSource(profileImageSource)
        withContext(dispatchers.io) {
            profileImageSource = homeManagementUseCase.invokeGetProfileImage(isForceRefresh = true, data = requestData)
        }
        _profileImage.addSource(profileImageSource) {
            _profileImage.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val document = it.data!!.healthRelatedDocument
                    Timber.e("Document--->$document")
                    val fileName = document.fileName
                    val fileBytes = document.fileBytes
                    //val personId = document.personID
                    val tempFileurl: String = RealPathUtil.getProfileFolderLocation() + "/" + fileName
                    val pathUrl: Boolean = RealPathUtil.isFileExist(tempFileurl)
                    val path = RealPathUtil.getProfileFolderLocation()

                    if (!pathUrl) {
                        val decodedImage: Bitmap = RealPathUtil.convertBase64ToBitmap(fileBytes)
                        if (decodedImage != null) {
                            var save = false
                            save = RealPathUtil.saveBitampAsFileToExternalCard(decodedImage, fileName, Constants.PROFILE, "")
                            //save = utilityForFiles.saveBitampAsFileToExternalCard(decodedImage,fileName,Constants.PROFILE, "")
                            if (save) {
                                updateUserProfileImgPath(fileName,path)
                            }
                        }
                    } else {
                        updateUserProfileImgPath(fileName,path)
                    }
                    activity.completeFilePath = path + "/"  + fileName
                    activity.setProfilePic()
                    activity.stopImageShimmer()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                    activity.stopImageShimmer()
                }
            }
        }
    }

    fun callUploadProfileImageApi( activity: MyProfileNewActivity,name:String, path:String, cropImgPath:String)
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
        Utilities.deleteFileFromLocalSystem(cropImgPath)

        _progressBar.value = Event("Uploading Profile Photo.....")
        _uploadProfileImage.removeSource(uploadProfileImageSource)
        withContext(dispatchers.io) {
            uploadProfileImageSource = homeManagementUseCase.invokeUploadProfileImage(PersonID,FileName,DocumentTypeCode,ByteArray,AuthTicket)
        }
        _uploadProfileImage.addSource(uploadProfileImageSource) {
            _uploadProfileImage.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    val profileImageID = it.data!!.profileImageID
                    Timber.i("UploadProfileImage----->$profileImageID")
                    if ( !Utilities.isNullOrEmptyOrZero(profileImageID) ) {
                        activity.hasProfileImage = true
                        activity.needToSet = true
                        val sourceFile = File(path,name)
                        val destpath =  RealPathUtil.getProfileFolderLocation()
                        RealPathUtil.createDirectory(destpath)
                        val destFile = File(destpath,name)
                        val save = RealPathUtil.copy(sourceFile,destFile)
                        if ( save ) {
                            Utilities.toastMessageShort(context,context.resources.getString(R.string.PROFILE_PHOTO_UPDATED))
                            updateUserProfileImgPath(name,destpath)
                            sourceFile.delete()
                            activity.completeFilePath = destpath + "/" + name
                            activity.setProfilePic()
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

    fun callRemoveProfileImageApi( activity: MyProfileNewActivity,context: Context )
            = viewModelScope.launch(dispatchers.main) {

        val requestData = RemoveProfileImageModel(Gson().toJson(
            RemoveProfileImageModel.JSONDataRequest(
                personID = personId.toInt()),
            RemoveProfileImageModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Removing Profile Photo...")
        _removeProfileImage.removeSource(removeProfileImageSource)
        withContext(dispatchers.io) {
            removeProfileImageSource = homeManagementUseCase.invokeRemoveProfileImage(isForceRefresh = true, data = requestData)
        }
        _removeProfileImage.addSource(removeProfileImageSource) {
            _removeProfileImage.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val isProcessed = it.data!!.isProcessed
                    Timber.e("isProcessed----->$isProcessed")
                    if ( isProcessed.equals(Constants.TRUE,ignoreCase = true) ) {
                        Utilities.toastMessageShort(context,context.resources.getString(R.string.PROFILE_PHOTO_REMOVED))
                        activity.removeProfilePic()
                    } else {
                        Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_PROFILE_PHOTO))
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

    fun updateUserMobileNumber( phone: String ) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
           val userDetails = homeManagementUseCase.invokeGetLoggedInPersonDetails()
            var dob = ""
            try {
                val db = DateHelper.formatDateValue( DateHelper.SERVER_DATE_YYYYMMDD,userDetails.dateOfBirth)!!
                if ( db != null ) {
                    dob = db
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val userUpdateDetails = UserRelatives(
                relativeID = personId,
                firstName = userDetails.firstName,
                lastName = "",
                dateOfBirth = dob,
                age = userDetails.age.toString(),
                gender = userDetails.gender,
                contactNo = phone,
                emailAddress = userDetails.emailAddress)
            Timber.i("userUpdateDetails----->"+userUpdateDetails)
            callUpdateRelativesApi(true,userUpdateDetails,Constants.USER)
        }
    }

    fun getLoggedInPersonDetails() = viewModelScope.launch {
        withContext(dispatchers.io) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun updateUserDetails( name : String ,personId : Int ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            homeManagementUseCase.invokeUpdateUserDetails( name , personId )
        }
    }

    fun updateUserProfileImgPath( name : String ,path : String ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            homeManagementUseCase.invokeUpdateUserProfileImgPath( name , path )
        }
    }

    fun getRelativesList() = viewModelScope.launch {
        withContext(dispatchers.io) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelativesExceptSelf())
        }
    }

   fun getUserRelativeSpecific(relationShipCode : String ) = viewModelScope.launch  {
       withContext(dispatchers.io) {
           alreadyExistRelatives.postValue( homeManagementUseCase.invokeGetUserRelativeSpecific(relationShipCode) )
       }
   }

    fun getUserRelativeForRelativeId(relativeId : String ) = viewModelScope.launch  {
        withContext(dispatchers.io) {
            alreadyExistRelatives.postValue( homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId) )
        }
    }


    fun getFamilyRelationshipList() = viewModelScope.launch{
        withContext(dispatchers.io){
            var user = homeManagementUseCase.invokeGetUserRelativeDetailsByRelativeId(sharedPref.getString(PreferenceConstants.PERSONID,"")!!)
            val gender = user.gender
            if (gender.contains("1", ignoreCase = true)) {
                familyRelationList.postValue(dataHandler.getFamilyRelationListMale())
            } else {
                familyRelationList.postValue(dataHandler.getFamilyRelationListFemale())
            }
        }
    }

    fun getRelationImgId(relationshipCode : String ): Int {
        var relationImgTobeAdded: Int = R.drawable.icon_husband

        when (relationshipCode) {
            "FAT" -> {
                relationImgTobeAdded = R.drawable.icon_father
            }
            "MOT" -> {
                relationImgTobeAdded = R.drawable.icon_mother
            }
            "SON" -> {
                relationImgTobeAdded = R.drawable.icon_son
            }
            "DAU" -> {
                relationImgTobeAdded = R.drawable.icon_daughter
            }
            "GRF" -> {
                relationImgTobeAdded = R.drawable.icon_gf
            }
            "GRM" -> {
                relationImgTobeAdded = R.drawable.icon_gm
            }
            "HUS" -> {
                relationImgTobeAdded = R.drawable.icon_husband
            }
            "WIF" -> {
                relationImgTobeAdded = R.drawable.icon_wife
            }
            "BRO" -> {
                relationImgTobeAdded = R.drawable.icon_brother
            }
            "SIS" -> {
                relationImgTobeAdded = R.drawable.icon_sister
            }
        }
        return relationImgTobeAdded
    }

}