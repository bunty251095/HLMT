package com.caressa.remote

import com.caressa.model.home.*
import com.caressa.model.security.HLMTLoginModel
import com.caressa.model.shr.ListRelativesModel
import okhttp3.RequestBody

class HomeDatasource(
    private val defaultService: ApiService,
    private val encryptedUserService: ApiService
) {

    fun getUserDetailsResponse(data: UserDetailsModel) =
        encryptedUserService.getUserDetailsApi(data)

    fun updateUserDetailsResponse(data: UpdateUserDetailsModel) =
        encryptedUserService.updateUserDetailsApi(data)

    fun getProfileImageResponse(data: ProfileImageModel) =
        encryptedUserService.getProfileImageApi(data)

    fun uploadProfileImageResponce(
        personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
        byteArray: RequestBody, authTicket: RequestBody
    ) = defaultService.uploadProfileImage(
        personID,
        fileName,
        documentTypeCode,
        byteArray,
        authTicket
    )

    fun removeProfileImageResponse(data: RemoveProfileImageModel) =
        encryptedUserService.removeProfileImageApi(data)


    fun getRelativesListResponse(data: ListRelativesModel) =
        encryptedUserService.fetchRelativesListApi(data)

    fun addRelativeResponse(data: AddRelativeModel) = encryptedUserService.addRelativeApi(data)

    fun updateRelativeResponse(data: UpdateRelativeModel) =
        encryptedUserService.updateRelativeApi(data)

    fun removeRelativeResponse(data: RemoveRelativeModel) =
        encryptedUserService.removeRelativeApi(data)

    fun getDoctorsListResponse(data: FamilyDoctorsListModel) =
        encryptedUserService.getFamilyDoctorsListApi(data)

    fun getSpecialityListResponse(data: ListDoctorSpecialityModel) =
        encryptedUserService.getSpecialityListApi(data)

    fun addDoctorResponse(data: FamilyDoctorAddModel) = encryptedUserService.addDoctorApi(data)

    fun updateDoctorResponse(data: FamilyDoctorUpdateModel) =
        encryptedUserService.updateDoctorApi(data)

    fun removeDoctorResponse(data: RemoveDoctorModel) = encryptedUserService.removeDoctorApi(data)

    fun contactUs(data: ContactUsModel) = encryptedUserService.contactUsApi(data)

    fun saveFeedbackResponse(data: SaveFeedbackModel) = encryptedUserService.saveFeedbackApi(data)

    fun passwordChangeResponse(data: PasswordChangeModel) =
        encryptedUserService.passwordChangeApi(data)

    fun checkAppUpdateResponse(data: CheckAppUpdateModel) =
        encryptedUserService.checkAppUpdateApi(data)

    fun saveCloudMessagingId(data: SaveCloudMessagingIdModel) =
        encryptedUserService.saveCloudMessagingId(data)

    fun fetchHLMT360LoginResponse(data: HLMTLoginModel) =
        encryptedUserService.hlmt360UserLogin(data)

    fun updateLanguagePreferences(data: UpdateLanguageProfileModel) = encryptedUserService.updateLanguagePreference(data)

    fun fetchRefreshToken(data: RefreshTokenModel) = encryptedUserService.getRefreshToken(data)

    fun listActiveBanner(data: ListActiveBannerModel) = encryptedUserService.listActiveBannerApi(data)

    fun saveBannerAccessLog(data: SaveBannerAccessLogModel) = encryptedUserService.saveBannerAccessLogApi(data)
}