package com.caressa.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.local.dao.*
import com.caressa.model.entity.AppVersion
import com.caressa.model.entity.DataSyncMaster
import com.caressa.model.entity.UserRelatives
import com.caressa.model.entity.Users
import com.caressa.model.home.*
import com.caressa.model.security.HLMTLoginModel
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

interface HomeRepository {

    suspend fun saveCloudMessagingId(
        forceRefresh: Boolean = false,
        data: SaveCloudMessagingIdModel
    ): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>

    suspend fun contactUs(
        forceRefresh: Boolean = false,
        data: ContactUsModel
    ): LiveData<Resource<ContactUsModel.ContactUsResponse>>

    suspend fun saveFeedback(
        forceRefresh: Boolean = false,
        data: SaveFeedbackModel
    ): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>>

    suspend fun passwordChange(
        forceRefresh: Boolean = false,
        data: PasswordChangeModel
    ): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>>

    suspend fun checkAppUpdate(
        forceRefresh: Boolean = false,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>>

    suspend fun getUserDetails(
        forceRefresh: Boolean = false,
        data: UserDetailsModel
    ): LiveData<Resource<UserDetailsModel.UserDetailsResponse>>

    suspend fun updateUserDetails(
        forceRefresh: Boolean = false,
        data: UpdateUserDetailsModel
    ): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>

    suspend fun getProfileImage(
        forceRefresh: Boolean = false,
        data: ProfileImageModel
    ): LiveData<Resource<ProfileImageModel.ProfileImageResponse>>

    suspend fun uploadProfileImage(
        personID: RequestBody,
        fileName: RequestBody,
        documentTypeCode: RequestBody,
        byteArray: RequestBody,
        authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>>

    suspend fun removeProfileImage(
        forceRefresh: Boolean = false,
        data: RemoveProfileImageModel
    ): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>

    suspend fun fetchRelativesList(
        forceRefresh: Boolean = false,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>>

    suspend fun addNewRelative(
        forceRefresh: Boolean = false,
        data: AddRelativeModel
    ): LiveData<Resource<AddRelativeModel.AddRelativeResponse>>

    suspend fun updateRelative(
        forceRefresh: Boolean = false,
        data: UpdateRelativeModel
    ): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>

    suspend fun removeRelative(
        forceRefresh: Boolean = false,
        data: RemoveRelativeModel
    ): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>

    suspend fun fetchDoctorsList(
        forceRefresh: Boolean = false,
        data: FamilyDoctorsListModel
    ): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>>

    suspend fun fetchSpecialityList(
        forceRefresh: Boolean = false,
        data: ListDoctorSpecialityModel
    ): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>

    suspend fun addDoctor(
        forceRefresh: Boolean = false,
        data: FamilyDoctorAddModel
    ): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>>

    suspend fun updateDoctor(
        forceRefresh: Boolean = false,
        data: FamilyDoctorUpdateModel
    ): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>

    suspend fun removeDoctor(
        forceRefresh: Boolean = false,
        data: RemoveDoctorModel
    ): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>>

    suspend fun getLoggedInPersonDetails(): Users
    suspend fun getAppVersionDetails(): AppVersion
    suspend fun updateUserDetails(name: String, personId: Int)
    suspend fun updateUserProfileImgPath(name: String, path: String)
    suspend fun getUserRelatives(): List<UserRelatives>
    suspend fun getUserRelativesExceptSelf(): List<UserRelatives>
    suspend fun getUserRelativeSpecific(relationShipCode: String): List<UserRelatives>
    suspend fun getUserRelativeForRelativeId(relativeId: String): List<UserRelatives>
    suspend fun getUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives
    suspend fun getSyncMasterData(personId: String): List<DataSyncMaster>
    suspend fun saveSyncDetails(data: DataSyncMaster)
    suspend fun clearTablesForSwitchProfile()
    suspend fun logoutUser()
    suspend fun hlmt360LoginResponse(
        forceRefresh: Boolean = false,
        data: HLMTLoginModel
    ): LiveData<Resource<HLMTLoginModel.LoginResponse>>

    suspend fun fetchUpdateProfileResponse(data: UpdateLanguageProfileModel): LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>>
    suspend fun fetchRefreshTokenResponse(data: RefreshTokenModel): LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>>
    suspend fun listActiveBanner(data: ListActiveBannerModel): LiveData<Resource<ListActiveBannerModel.ListActiveBannerResponse>>
}

class HomeRepositoryImpl(
    private val datasource: HomeDatasource, private val dataSyncDao: DataSyncMasterDao,
    private val homeDao: VivantUserDao, private val medicationDao: MedicationDao,
    private val shrDao: StoreRecordsDao, private val hraDao: HRADao,
    private val trackParamDao: TrackParameterDao, private val context: Context
) : HomeRepository {

    override suspend fun saveCloudMessagingId(
        forceRefresh: Boolean,
        data: SaveCloudMessagingIdModel
    ): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {

        return object :
            NetworkBoundResource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse, BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse {
                return SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {
                return datasource.saveCloudMessagingId(data)
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

    override suspend fun contactUs(
        forceRefresh: Boolean,
        data: ContactUsModel
    ): LiveData<Resource<ContactUsModel.ContactUsResponse>> {

        return object :
            NetworkBoundResource<ContactUsModel.ContactUsResponse, BaseResponse<ContactUsModel.ContactUsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ContactUsModel.ContactUsResponse {
                return ContactUsModel.ContactUsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<ContactUsModel.ContactUsResponse>> {
                return datasource.contactUs(data)
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


    override suspend fun saveFeedback(
        forceRefresh: Boolean,
        data: SaveFeedbackModel
    ): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> {

        return object :
            NetworkBoundResource<SaveFeedbackModel.SaveFeedbackResponse, BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveFeedbackModel.SaveFeedbackResponse {
                return SaveFeedbackModel.SaveFeedbackResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveFeedbackModel.SaveFeedbackResponse>> {
                return datasource.saveFeedbackResponse(data)
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

    override suspend fun passwordChange(
        forceRefresh: Boolean,
        data: PasswordChangeModel
    ): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> {

        return object :
            NetworkBoundResource<PasswordChangeModel.ChangePasswordResponse, BaseResponse<PasswordChangeModel.ChangePasswordResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): PasswordChangeModel.ChangePasswordResponse {
                return PasswordChangeModel.ChangePasswordResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<PasswordChangeModel.ChangePasswordResponse>> {
                return datasource.passwordChangeResponse(data)
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

        return object :
            NetworkBoundResource<CheckAppUpdateModel.CheckAppUpdateResponse, BaseResponse<CheckAppUpdateModel.CheckAppUpdateResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): CheckAppUpdateModel.CheckAppUpdateResponse {
                val appVersion: ArrayList<AppVersion> = arrayListOf()
                val versionDetails = homeDao.getAppVersionDetails()
                if (versionDetails != null) {
                    appVersion.add(homeDao.getAppVersionDetails())
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
                if (!items.result.appVersion.isNullOrEmpty()) {
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

        return object :
            NetworkBoundResource<UserDetailsModel.UserDetailsResponse, BaseResponse<UserDetailsModel.UserDetailsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UserDetailsModel.UserDetailsResponse {
                return UserDetailsModel.UserDetailsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UserDetailsModel.UserDetailsResponse>> {
                return datasource.getUserDetailsResponse(data)
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

    override suspend fun updateUserDetails(
        forceRefresh: Boolean,
        data: UpdateUserDetailsModel
    ): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {

        return object :
            NetworkBoundResource<UpdateUserDetailsModel.UpdateUserDetailsResponse, BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UpdateUserDetailsModel.UpdateUserDetailsResponse {
                return UpdateUserDetailsModel.UpdateUserDetailsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {
                return datasource.updateUserDetailsResponse(data)
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

    override suspend fun getProfileImage(
        forceRefresh: Boolean,
        data: ProfileImageModel
    ): LiveData<Resource<ProfileImageModel.ProfileImageResponse>> {

        return object :
            NetworkBoundResource<ProfileImageModel.ProfileImageResponse, BaseResponse<ProfileImageModel.ProfileImageResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

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
        authTicket: RequestBody
    ): LiveData<Resource<UploadProfileImageResponce>> {

        return object :
            NetworkDataBoundResource<UploadProfileImageResponce, BaseResponse<UploadProfileImageResponce>>(
                context
            ) {

            override fun processResponse(response: BaseResponse<UploadProfileImageResponce>): UploadProfileImageResponce {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<UploadProfileImageResponce>> {
                return datasource.uploadProfileImageResponce(
                    personID,
                    fileName,
                    documentTypeCode,
                    byteArray,
                    authTicket
                )
            }

        }.build().asLiveData()
    }

    override suspend fun removeProfileImage(
        forceRefresh: Boolean,
        data: RemoveProfileImageModel
    ): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> {

        return object :
            NetworkBoundResource<RemoveProfileImageModel.RemoveProfileImageResponse, BaseResponse<RemoveProfileImageModel.RemoveProfileImageResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

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

    override suspend fun addNewRelative(
        forceRefresh: Boolean,
        data: AddRelativeModel
    ): LiveData<Resource<AddRelativeModel.AddRelativeResponse>> {

        return object :
            NetworkBoundResource<AddRelativeModel.AddRelativeResponse, BaseResponse<AddRelativeModel.AddRelativeResponse>>(
                context
            ) {

            var person = AddRelativeModel.PersonResp()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): AddRelativeModel.AddRelativeResponse {
                val resp = AddRelativeModel.AddRelativeResponse()
                resp.person = person
                return resp
            }

            override fun createCallAsync(): Deferred<BaseResponse<AddRelativeModel.AddRelativeResponse>> {
                return datasource.addRelativeResponse(data)
            }

            override fun processResponse(response: BaseResponse<AddRelativeModel.AddRelativeResponse>): AddRelativeModel.AddRelativeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddRelativeModel.AddRelativeResponse) {
                person = items.person
                var relationshipId = ""
                var relationshipCode = ""
                if (items.person.relationships.isNotEmpty()) {
                    relationshipCode = items.person.relationships[0].relationshipCode
                    relationshipId = items.person.relationships[0].id.toString()
                }
                val userRelative = UserRelatives(
                    relativeID = items.person.id.toString(),
                    firstName = items.person.firstName,
                    lastName = items.person.lastName,
                    dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(items.person.dateOfBirth),
                    age = items.person.age.toString(),
                    gender = items.person.gender.toString(),
                    contactNo = items.person.contact.primaryContactNo,
                    emailAddress = items.person.contact.emailAddress,
                    relationshipCode = relationshipCode,
                    relationship = Utilities.getRelationshipByRelationshipCode(
                        relationshipCode,
                        context
                    ),
                    relationShipID = relationshipId
                )
                homeDao.insertUserRelative(userRelative)
            }

            override fun shouldFetch(data: AddRelativeModel.AddRelativeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRelativesList(
        forceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {

        return object :
            NetworkBoundResource<ListRelativesModel.ListRelativesResponse, BaseResponse<ListRelativesModel.ListRelativesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

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
                val user = UserRelatives(
                    relativeID = userDetails.personId.toDouble().toInt().toString(),
                    firstName = userDetails.firstName,
                    lastName = userDetails.lastName,
                    dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(userDetails.dateOfBirth),
                    age = userDetails.age.toDouble().toInt().toString(),
                    gender = userDetails.gender,
                    contactNo = userDetails.phoneNumber,
                    emailAddress = userDetails.emailAddress,
                    relationshipCode = "SELF",
                    relationship = "SELF"
                )
                relativeInfoList.add(user)
                for (item in relativeList) {
                    if (item.relationships.isNotEmpty()) {
                        relationshipId = item.relationships[0].id.toString()
                    }
                    val userRelative = UserRelatives(
                        relativeID = item.id.toDouble().toInt().toString(),
                        firstName = item.firstName,
                        lastName = item.lastName,
                        dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(item.dateOfBirth),
                        age = item.age.toString(),
                        gender = item.gender.toString(),
                        contactNo = item.contact.primaryContactNo,
                        emailAddress = item.contact.emailAddress,
                        relationshipCode = item.relationships[0].relationshipCode,
                        relationship = item.relationships[0].relationship,
                        relationShipID = relationshipId
                    )
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

    override suspend fun removeRelative(
        forceRefresh: Boolean,
        data: RemoveRelativeModel
    ): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> {

        return object :
            NetworkBoundResource<RemoveRelativeModel.RemoveRelativeResponse, BaseResponse<RemoveRelativeModel.RemoveRelativeResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

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

    override suspend fun updateRelative(
        forceRefresh: Boolean,
        data: UpdateRelativeModel
    ): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> {

        return object :
            NetworkBoundResource<UpdateRelativeModel.UpdateRelativeResponse, BaseResponse<UpdateRelativeModel.UpdateRelativeResponse>>(
                context
            ) {

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

    override suspend fun fetchDoctorsList(
        forceRefresh: Boolean,
        data: FamilyDoctorsListModel
    ): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorsListModel.FamilyDoctorsResponse, BaseResponse<FamilyDoctorsListModel.FamilyDoctorsResponse>>(
                context
            ) {

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

    override suspend fun fetchSpecialityList(
        forceRefresh: Boolean,
        data: ListDoctorSpecialityModel
    ): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {

        return object :
            NetworkBoundResource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse, BaseResponse<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>>(
                context
            ) {

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

    override suspend fun addDoctor(
        forceRefresh: Boolean,
        data: FamilyDoctorAddModel
    ): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorAddModel.FamilyDoctorAddResponse, BaseResponse<FamilyDoctorAddModel.FamilyDoctorAddResponse>>(
                context
            ) {

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

    override suspend fun updateDoctor(
        forceRefresh: Boolean,
        data: FamilyDoctorUpdateModel
    ): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {

        return object :
            NetworkBoundResource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse, BaseResponse<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>>(
                context
            ) {

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

    override suspend fun removeDoctor(
        forceRefresh: Boolean,
        data: RemoveDoctorModel
    ): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> {

        return object :
            NetworkBoundResource<RemoveDoctorModel.RemoveDoctorResponse, BaseResponse<RemoveDoctorModel.RemoveDoctorResponse>>(
                context
            ) {

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

    override suspend fun getLoggedInPersonDetails(): Users {
        return homeDao.getUser()
    }

    override suspend fun getAppVersionDetails(): AppVersion {
        return homeDao.getAppVersionDetails()
    }

    override suspend fun updateUserDetails(name: String, personId: Int) {
        homeDao.updateName(name, personId)
        homeDao.updateUserInUserRelativesDetails(name, personId.toString())
    }

    override suspend fun updateUserProfileImgPath(name: String, path: String) {
        Timber.e("ProfileImagePath_Updated")
        return homeDao.updateUserProfileImgPath(name, path)
    }

    override suspend fun getUserRelatives(): List<UserRelatives> {
        return homeDao.geAllRelativeList()
    }

    override suspend fun getUserRelativesExceptSelf(): List<UserRelatives> {
        return homeDao.getUserRelatives()
    }

    override suspend fun getUserRelativeSpecific(relationShipCode: String): List<UserRelatives> {
        return homeDao.getUserRelativeSpecific(relationShipCode)
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
        hraDao.deleteHraVitalDetailsTable()
        hraDao.deleteHraLabDetailsTable()
        // Track parameters
        //trackParamDao.deleteHistory()
    }

    override suspend fun logoutUser() {
        homeDao.deleteAllRecords()
        homeDao.deleteUserRelativesTable()
    }

    override suspend fun hlmt360LoginResponse(
        forceRefresh: Boolean,
        data: HLMTLoginModel
    ): LiveData<Resource<HLMTLoginModel.LoginResponse>> {
        return object :
            NetworkBoundResource<HLMTLoginModel.LoginResponse, BaseResponse<HLMTLoginModel.LoginResponse>>(
                context
            ) {
            override fun processResponse(response: BaseResponse<HLMTLoginModel.LoginResponse>): HLMTLoginModel.LoginResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: HLMTLoginModel.LoginResponse) {

            }

            override fun shouldFetch(data: HLMTLoginModel.LoginResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean {
                return false
            }

            override suspend fun loadFromDb(): HLMTLoginModel.LoginResponse {
                return HLMTLoginModel.LoginResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<HLMTLoginModel.LoginResponse>> {
                return datasource.fetchHLMT360LoginResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun fetchUpdateProfileResponse(data: UpdateLanguageProfileModel):LiveData<Resource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> {

        return object : NetworkBoundResource<UpdateLanguageProfileModel.UpdateLanguageProfileResponse, BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>>(context) {

            override fun processResponse(response: BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>): UpdateLanguageProfileModel.UpdateLanguageProfileResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UpdateLanguageProfileModel.UpdateLanguageProfileResponse) {

            }

            override fun shouldFetch(data: UpdateLanguageProfileModel.UpdateLanguageProfileResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean {
                return false
            }

            override suspend fun loadFromDb(): UpdateLanguageProfileModel.UpdateLanguageProfileResponse {
                return UpdateLanguageProfileModel.UpdateLanguageProfileResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse>> {
                return datasource.updateLanguagePreferences(data)
            }

/*            override suspend fun createCallAsync(): BaseResponse<UpdateLanguageProfileModel.UpdateLanguageProfileResponse> {
                return datasource.updateLanguagePreferences(data)
            }*/

        }.build().asLiveData()
    }

    override suspend fun fetchRefreshTokenResponse(data: RefreshTokenModel): LiveData<Resource<RefreshTokenModel.RefreshTokenResponse>> {

        return object : NetworkBoundResource<RefreshTokenModel.RefreshTokenResponse, BaseResponse<RefreshTokenModel.RefreshTokenResponse>>(context) {

            override fun processResponse(response: BaseResponse<RefreshTokenModel.RefreshTokenResponse>): RefreshTokenModel.RefreshTokenResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: RefreshTokenModel.RefreshTokenResponse) {

            }

            override fun shouldFetch(data: RefreshTokenModel.RefreshTokenResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean {
                return false
            }

            override suspend fun loadFromDb(): RefreshTokenModel.RefreshTokenResponse {
                return RefreshTokenModel.RefreshTokenResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<RefreshTokenModel.RefreshTokenResponse>> {
                return datasource.fetchRefreshToken(data)
            }

/*            override suspend fun createCallAsync(): BaseResponse<RefreshTokenModel.RefreshTokenResponse> {
                return datasource.fetchRefreshToken(data)
            }*/

        }.build().asLiveData()
    }

    override suspend fun listActiveBanner(data: ListActiveBannerModel): LiveData<Resource<ListActiveBannerModel.ListActiveBannerResponse>> {

        return object : NetworkBoundResource<ListActiveBannerModel.ListActiveBannerResponse, BaseResponse<ListActiveBannerModel.ListActiveBannerResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ListActiveBannerModel.ListActiveBannerResponse {
                return ListActiveBannerModel.ListActiveBannerResponse()
            }

            override fun processResponse(response: BaseResponse<ListActiveBannerModel.ListActiveBannerResponse>): ListActiveBannerModel.ListActiveBannerResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<ListActiveBannerModel.ListActiveBannerResponse>> {
                return datasource.listActiveBanner(data)
            }

            override suspend fun saveCallResults(items: ListActiveBannerModel.ListActiveBannerResponse) {

            }

            override fun shouldFetch(data: ListActiveBannerModel.ListActiveBannerResponse?): Boolean = true


        }.build().asLiveData()

    }


}
