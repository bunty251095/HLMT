package com.caressa.repository

import androidx.lifecycle.LiveData
import com.caressa.common.utils.DateHelper
import com.caressa.local.dao.*
import com.caressa.model.entity.AppVersion
import com.caressa.model.entity.DataSyncMaster
import com.caressa.model.entity.UserRelatives
import com.caressa.model.entity.Users
import com.caressa.model.home.*
import com.caressa.model.shr.ListRelativesModel
import com.caressa.model.singleton.ToolsTrackerSingleton
import com.caressa.remote.HomeDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.NetworkDataBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import timber.log.Timber

interface HomeRepository{

    suspend fun saveCloudMessagingId(forceRefresh: Boolean = false , data : SaveCloudMessagingIdModel) : LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>
    suspend fun contactUs(forceRefresh: Boolean = false , data : ContactUsModel) : LiveData<Resource<ContactUsModel.ContactUsResponse>>
    suspend fun saveFeedback(forceRefresh: Boolean = false , data : SaveFeedbackModel) : LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>>
    suspend fun passwordChange(forceRefresh: Boolean = false , data : PasswordChangeModel) : LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>>
    suspend fun checkAppUpdate(forceRefresh: Boolean = false , data : CheckAppUpdateModel) : LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>>

    suspend fun getUserDetails(forceRefresh: Boolean = false , data : UserDetailsModel) : LiveData<Resource<UserDetailsModel.UserDetailsResponse>>
    suspend fun updateUserDetails(forceRefresh: Boolean = false , data : UpdateUserDetailsModel) : LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>
    suspend fun getProfileImage(forceRefresh: Boolean = false , data : ProfileImageModel) : LiveData<Resource<ProfileImageModel.ProfileImageResponse>>
    suspend fun uploadProfileImage(personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
                                   byteArray: RequestBody,authTicket: RequestBody) : LiveData<Resource<UploadProfileImageResponce>>
    suspend fun removeProfileImage(forceRefresh: Boolean = false , data : RemoveProfileImageModel) : LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>

    suspend fun fetchRelativesList(forceRefresh: Boolean = false , data : ListRelativesModel) : LiveData<Resource<ListRelativesModel.ListRelativesResponse>>
    suspend fun addNewRelative(forceRefresh: Boolean = false , data : AddRelativeModel) : LiveData<Resource<AddRelativeModel.AddRelativeResponse>>
    suspend fun updateRelative(forceRefresh: Boolean = false , data : UpdateRelativeModel) : LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>
    suspend fun removeRelative(forceRefresh: Boolean = false , data : RemoveRelativeModel) : LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>

    suspend fun fetchDoctorsList(forceRefresh: Boolean = false , data : FamilyDoctorsListModel) : LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>>
    suspend fun fetchSpecialityList(forceRefresh: Boolean = false , data : ListDoctorSpecialityModel) : LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>
    suspend fun addDoctor(forceRefresh: Boolean = false , data : FamilyDoctorAddModel) : LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>>
    suspend fun updateDoctor(forceRefresh: Boolean = false , data : FamilyDoctorUpdateModel) : LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>
    suspend fun removeDoctor(forceRefresh: Boolean = false , data : RemoveDoctorModel) : LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>>

    suspend fun getLoggedInPersonDetails() : Users
    suspend fun getAppVersionDetails() : AppVersion
    suspend fun updateUserDetails( name : String ,phone : String )
    suspend fun updateUserProfileImgPath( name : String ,path : String )
    suspend fun getUserRelatives() : List<UserRelatives>
    suspend fun getUserRelativesExceptSelf() : List<UserRelatives>
    suspend fun getUserRelativeSpecific( relationShipCode : String ) : List<UserRelatives>
    suspend fun getUserRelativeForRelativeId( relativeId : String ) : List<UserRelatives>
    suspend fun getUserRelativeDetailsByRelativeId( relativeId : String ) : UserRelatives
    suspend fun getSyncMasterData(personId: String): List<DataSyncMaster>
    suspend fun saveSyncDetails(data: DataSyncMaster)
    suspend fun clearTablesForSwitchProfile()
    suspend fun logoutUser()

}

class HomeRepositoryImpl(private val datasource : HomeDatasource, private val dataSyncDao: DataSyncMasterDao,
                         private val homeDao : VivantUserDao, private val medicationDao: MedicationDao,
                         private val shrDao : StoreRecordsDao, private val hraDao : HRADao,
                         private val trackParamDao: TrackParameterDao) : HomeRepository {

    override suspend fun saveCloudMessagingId(forceRefresh: Boolean, data: SaveCloudMessagingIdModel): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {

        return object : NetworkBoundResource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse,BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse {
                return SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {
                return  datasource.saveCloudMessagingId(data)
            }

            override fun processResponse(response: BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>): SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse) {

            }

            override fun shouldFetch(data: SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun contactUs(forceRefresh: Boolean, data: ContactUsModel): LiveData<Resource<ContactUsModel.ContactUsResponse>> {

        return object : NetworkBoundResource<ContactUsModel.ContactUsResponse,BaseResponse<ContactUsModel.ContactUsResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): ContactUsModel.ContactUsResponse {
                return ContactUsModel.ContactUsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<ContactUsModel.ContactUsResponse>> {
                return  datasource.contactUs(data)
            }

            override fun processResponse(response: BaseResponse<ContactUsModel.ContactUsResponse>): ContactUsModel.ContactUsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ContactUsModel.ContactUsResponse) {}

            override fun shouldFetch(data: ContactUsModel.ContactUsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }


    override suspend fun saveFeedback(forceRefresh: Boolean, data: SaveFeedbackModel):
            LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> {

        return object : NetworkBoundResource<SaveFeedbackModel.SaveFeedbackResponse,BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): SaveFeedbackModel.SaveFeedbackResponse {
                return SaveFeedbackModel.SaveFeedbackResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>> {
                return  datasource.saveFeedbackResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>): SaveFeedbackModel.SaveFeedbackResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveFeedbackModel.SaveFeedbackResponse) {}

            override fun shouldFetch(data: SaveFeedbackModel.SaveFeedbackResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun passwordChange(forceRefresh: Boolean, data: PasswordChangeModel): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> {

        return object : NetworkBoundResource<PasswordChangeModel.ChangePasswordResponse,BaseResponse<PasswordChangeModel.ChangePasswordResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): PasswordChangeModel.ChangePasswordResponse {
                return PasswordChangeModel.ChangePasswordResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<PasswordChangeModel.ChangePasswordResponse>> {
                return  datasource.passwordChangeResponse(data)
            }

            override fun processResponse(response: BaseResponse<PasswordChangeModel.ChangePasswordResponse>): PasswordChangeModel.ChangePasswordResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PasswordChangeModel.ChangePasswordResponse) {}

            override fun shouldFetch(data: PasswordChangeModel.ChangePasswordResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun checkAppUpdate(forceRefresh: Boolean, data: CheckAppUpdateModel):
            LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {

        return object : NetworkBoundResource<CheckAppUpdateModel.CheckAppUpdateResponse,BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>>() {

            override fun shouldStoreInDb() : Boolean = true

            override suspend fun loadFromDb(): CheckAppUpdateModel.CheckAppUpdateResponse {
                val appVersion: ArrayList<AppVersion> = arrayListOf()
                val versionDetails = homeDao.getAppVersionDetails()
                if ( versionDetails != null ) {
                    appVersion.add( homeDao.getAppVersionDetails() )
                }
                val appUpdateResp = CheckAppUpdateModel.CheckAppUpdateResponse()
                appUpdateResp.result = CheckAppUpdateModel.Result(appVersion = appVersion)
                return appUpdateResp
            }

            override fun createCallAsync(): Deferred<BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>> {
                return datasource.checkAppUpdateResponse(data)
            }

            override fun processResponse(response: BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>): CheckAppUpdateModel.CheckAppUpdateResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: CheckAppUpdateModel.CheckAppUpdateResponse) {
                if ( !items.result.appVersion.isNullOrEmpty() ) {
                    homeDao.insertAppVersionDetails(items.result.appVersion[0])
                }
            }

            override fun shouldFetch(data: CheckAppUpdateModel.CheckAppUpdateResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getUserDetails(forceRefresh: Boolean, data: UserDetailsModel):
            LiveData<Resource<UserDetailsModel.UserDetailsResponse>> {

        return object : NetworkBoundResource<UserDetailsModel.UserDetailsResponse,BaseResponse<UserDetailsModel.UserDetailsResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): UserDetailsModel.UserDetailsResponse {
                return UserDetailsModel.UserDetailsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UserDetailsModel.UserDetailsResponse>> {
                return  datasource.getUserDetailsResponse(data)
            }

            override fun processResponse(response: BaseResponse<UserDetailsModel.UserDetailsResponse>): UserDetailsModel.UserDetailsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UserDetailsModel.UserDetailsResponse) {

            }

            override fun shouldFetch(data: UserDetailsModel.UserDetailsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateUserDetails(forceRefresh: Boolean, data: UpdateUserDetailsModel):
            LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {

        return object : NetworkBoundResource<UpdateUserDetailsModel.UpdateUserDetailsResponse,BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): UpdateUserDetailsModel.UpdateUserDetailsResponse {
                return UpdateUserDetailsModel.UpdateUserDetailsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {
                return  datasource.updateUserDetailsResponse(data)
            }

            override fun processResponse(response: BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>): UpdateUserDetailsModel.UpdateUserDetailsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UpdateUserDetailsModel.UpdateUserDetailsResponse) {

            }

            override fun shouldFetch(data: UpdateUserDetailsModel.UpdateUserDetailsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getProfileImage(forceRefresh: Boolean, data: ProfileImageModel):
            LiveData<Resource<ProfileImageModel.ProfileImageResponse>> {

        return object : NetworkBoundResource<ProfileImageModel.ProfileImageResponse,BaseResponse<ProfileImageModel.ProfileImageResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): ProfileImageModel.ProfileImageResponse {
                return ProfileImageModel.ProfileImageResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<ProfileImageModel.ProfileImageResponse>> {
                return datasource.getProfileImageResponse(data)
            }

            override fun processResponse(response: BaseResponse<ProfileImageModel.ProfileImageResponse>): ProfileImageModel.ProfileImageResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ProfileImageModel.ProfileImageResponse) {

            }

            override fun shouldFetch(data: ProfileImageModel.ProfileImageResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun uploadProfileImage(
        personID: RequestBody,
        fileName: RequestBody,
        documentTypeCode: RequestBody,
        byteArray: RequestBody,
        authTicket: RequestBody): LiveData<Resource<UploadProfileImageResponce>> {

        return object : NetworkDataBoundResource<UploadProfileImageResponce, BaseResponse<UploadProfileImageResponce>>(){

            override fun processResponse(response: BaseResponse<UploadProfileImageResponce>): UploadProfileImageResponce {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<UploadProfileImageResponce>> {
                return datasource.uploadProfileImageResponce(personID,fileName,documentTypeCode,byteArray,authTicket)
            }

        }.build().asLiveData()
    }

    override suspend fun removeProfileImage(forceRefresh: Boolean, data: RemoveProfileImageModel):
            LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> {

        return object : NetworkBoundResource<RemoveProfileImageModel.RemoveProfileImageResponse,BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): RemoveProfileImageModel.RemoveProfileImageResponse {
                return RemoveProfileImageModel.RemoveProfileImageResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>> {
                return datasource.removeProfileImageResponse(data)
            }

            override fun processResponse(response: BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>): RemoveProfileImageModel.RemoveProfileImageResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveProfileImageModel.RemoveProfileImageResponse) {}

            override fun shouldFetch(data: RemoveProfileImageModel.RemoveProfileImageResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun addNewRelative(forceRefresh: Boolean, data: AddRelativeModel): LiveData<Resource<AddRelativeModel.AddRelativeResponse>> {

        return object : NetworkBoundResource<AddRelativeModel.AddRelativeResponse,BaseResponse<AddRelativeModel.AddRelativeResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): AddRelativeModel.AddRelativeResponse {
                return AddRelativeModel.AddRelativeResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<AddRelativeModel.AddRelativeResponse>> {
                return datasource.addRelativeResponse(data)
            }

            override fun processResponse(response: BaseResponse<AddRelativeModel.AddRelativeResponse>): AddRelativeModel.AddRelativeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddRelativeModel.AddRelativeResponse) {
            }

            override fun shouldFetch(data: AddRelativeModel.AddRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRelativesList(forceRefresh: Boolean, data: ListRelativesModel): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {

        return object : NetworkBoundResource<ListRelativesModel.ListRelativesResponse, BaseResponse<ListRelativesModel.ListRelativesResponse>>() {

            override fun shouldStoreInDb() : Boolean = true

            override suspend fun loadFromDb(): ListRelativesModel.ListRelativesResponse {
                return ListRelativesModel.ListRelativesResponse(relativeList = homeDao.geAllRelativeList())
            }

            override suspend fun saveCallResults(items: ListRelativesModel.ListRelativesResponse) {
                homeDao.deleteUserRelativesTable()
                val relativeList = items.persons
                val relativeInfoList: ArrayList<UserRelatives> = ArrayList()
                val userDetails = homeDao.getUser()
                ToolsTrackerSingleton.instance.userDetails = userDetails
                var relationshipId = ""
                val user = UserRelatives(relativeID = userDetails.personId.toDouble().toInt().toString(),
                    firstName = userDetails.firstName, lastName = userDetails.lastName,
                    dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(userDetails.dateOfBirth) ,
                    age = userDetails.age.toDouble().toInt().toString(), gender = userDetails.gender,
                    contactNo = userDetails.phoneNumber, emailAddress = userDetails.emailAddress,
                    relationshipCode = "SELF", relationship =  "SELF")
                relativeInfoList.add(user)
                for(item in relativeList){
                    if ( item.relationships.size > 0 ) {
                        relationshipId = item.relationships[0].id.toString()
                    }
                    val userRelative = UserRelatives(relativeID = item.id.toDouble().toInt().toString(),
                        firstName = item.firstName, lastName = item.lastName,
                        dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(item.dateOfBirth), age = item.age.toString(),
                        gender = item.gender.toString(), contactNo = item.contact.primaryContactNo,
                        emailAddress = item.contact.emailAddress, relationshipCode = item.relationships[0].relationshipCode,
                        relationship = item.relationships[0].relationship , relationShipID = relationshipId)
                    relativeInfoList.add(userRelative)
                }
                homeDao.insertUserRelative(relativeInfoList)

            }

            override fun createCallAsync(): Deferred<BaseResponse<ListRelativesModel.ListRelativesResponse>> {
                return datasource.getRelativesListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListRelativesModel.ListRelativesResponse>): ListRelativesModel.ListRelativesResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListRelativesModel.ListRelativesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun removeRelative(forceRefresh: Boolean, data: RemoveRelativeModel):
            LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> {

        return object : NetworkBoundResource<RemoveRelativeModel.RemoveRelativeResponse,BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>>() {

            override fun shouldStoreInDb() : Boolean = false

            override suspend fun loadFromDb(): RemoveRelativeModel.RemoveRelativeResponse {
                return RemoveRelativeModel.RemoveRelativeResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>> {
                return datasource.removeRelativeResponse(data)
            }

            override fun processResponse(response: BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>): RemoveRelativeModel.RemoveRelativeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveRelativeModel.RemoveRelativeResponse) {
            }

            override fun shouldFetch(data: RemoveRelativeModel.RemoveRelativeResponse?): Boolean {
               return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateRelative(forceRefresh: Boolean, data: UpdateRelativeModel): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> {

        return object  : NetworkBoundResource<UpdateRelativeModel.UpdateRelativeResponse,BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UpdateRelativeModel.UpdateRelativeResponse {
                return UpdateRelativeModel.UpdateRelativeResponse()
            }

            override fun processResponse(response: BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>): UpdateRelativeModel.UpdateRelativeResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>> {
                return datasource.updateRelativeResponse(data)
            }

            override suspend fun saveCallResults(items: UpdateRelativeModel.UpdateRelativeResponse) {}

            override fun shouldFetch(data: UpdateRelativeModel.UpdateRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchDoctorsList(forceRefresh: Boolean, data: FamilyDoctorsListModel):
            LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> {

        return object  : NetworkBoundResource<FamilyDoctorsListModel.FamilyDoctorsResponse,BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorsListModel.FamilyDoctorsResponse {
                return FamilyDoctorsListModel.FamilyDoctorsResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>): FamilyDoctorsListModel.FamilyDoctorsResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>> {
                return datasource.getDoctorsListResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorsListModel.FamilyDoctorsResponse) {}

            override fun shouldFetch(data: FamilyDoctorsListModel.FamilyDoctorsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun fetchSpecialityList(forceRefresh: Boolean, data: ListDoctorSpecialityModel):
            LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {

        return object  : NetworkBoundResource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse,BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ListDoctorSpecialityModel.ListDoctorSpecialityResponse {
                return ListDoctorSpecialityModel.ListDoctorSpecialityResponse()
            }

            override fun processResponse(response: BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>): ListDoctorSpecialityModel.ListDoctorSpecialityResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {
                return datasource.getSpecialityListResponse(data)
            }

            override suspend fun saveCallResults(items: ListDoctorSpecialityModel.ListDoctorSpecialityResponse) {}

            override fun shouldFetch(data: ListDoctorSpecialityModel.ListDoctorSpecialityResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun addDoctor(forceRefresh: Boolean, data: FamilyDoctorAddModel):
            LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {

        return object  : NetworkBoundResource<FamilyDoctorAddModel.FamilyDoctorAddResponse,BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorAddModel.FamilyDoctorAddResponse {
                return FamilyDoctorAddModel.FamilyDoctorAddResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>): FamilyDoctorAddModel.FamilyDoctorAddResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {
                return datasource.addDoctorResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorAddModel.FamilyDoctorAddResponse) {}

            override fun shouldFetch(data: FamilyDoctorAddModel.FamilyDoctorAddResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun updateDoctor(forceRefresh: Boolean, data: FamilyDoctorUpdateModel):
            LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {

        return object  : NetworkBoundResource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse,BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse {
                return FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse()
            }

            override fun processResponse(response: BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>): FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {
                return datasource.updateDoctorResponse(data)
            }

            override suspend fun saveCallResults(items: FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse) {}

            override fun shouldFetch(data: FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun removeDoctor(forceRefresh: Boolean, data: RemoveDoctorModel):
            LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> {

        return object  : NetworkBoundResource<RemoveDoctorModel.RemoveDoctorResponse,BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): RemoveDoctorModel.RemoveDoctorResponse {
                return RemoveDoctorModel.RemoveDoctorResponse()
            }

            override fun processResponse(response: BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>): RemoveDoctorModel.RemoveDoctorResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RemoveDoctorModel.RemoveDoctorResponse) {}

            override fun createCallAsync(): Deferred<BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>> {
                return datasource.removeDoctorResponse(data)
            }

            override fun shouldFetch(data: RemoveDoctorModel.RemoveDoctorResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getLoggedInPersonDetails() : Users {
        return homeDao.getUser()
    }

    override suspend fun getAppVersionDetails() : AppVersion {
        return homeDao.getAppVersionDetails()
    }

    override suspend fun updateUserDetails( name : String ,phone : String ) {
        return homeDao.updateUserDetails( name , phone)
    }

    override suspend fun updateUserProfileImgPath( name : String ,path : String ) {
        Timber.e("ProfileImagePath_Updated")
        return homeDao.updateUserProfileImgPath( name , path)
    }

    override suspend fun getUserRelatives(): List<UserRelatives> {
        return homeDao.geAllRelativeList()
    }

    override suspend fun getUserRelativesExceptSelf(): List<UserRelatives> {
        return  homeDao.getUserRelatives()
    }

    override suspend  fun getUserRelativeSpecific(relationShipCode : String): List<UserRelatives> {
        return  homeDao.getUserRelativeSpecific( relationShipCode )
    }

    override suspend fun getUserRelativeForRelativeId(relativeId: String): List<UserRelatives> {
        return homeDao.getUserRelativeForRelativeId(relativeId)
    }

    override suspend fun getUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives {
        return homeDao.getUserRelativeDetailsByRelativeId(relativeId)
    }

    override suspend fun getSyncMasterData(personId: String): List<DataSyncMaster> {
        return dataSyncDao.getLastSyncDataList(personId)
    }

    override suspend fun saveSyncDetails(data: DataSyncMaster) {
//        if(dataSyncDao.getLastSyncDataList().find { it.apiName == data.apiName } == null)
            dataSyncDao.insertApiSyncData(data)
//        else
//            dataSyncDao.updateRecord(data)
    }

    override suspend fun clearTablesForSwitchProfile() {
        //Medication
        medicationDao.deleteMedicationTable()
        //Health Records
        shrDao.deleteHealthDocumentTableTable()
        //Hra
        hraDao.deleteHraQuesTable()
        hraDao.deleteHraVitalDetailsTable( )
        hraDao.deleteHraLabDetailsTable()
        // Track parameters
        //trackParamDao.deleteHistory()
    }

    override suspend fun logoutUser() {
        homeDao.deleteAllRecords()
        homeDao.deleteUserRelativesTable()
    }

}
