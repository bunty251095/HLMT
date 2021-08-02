package com.caressa.home.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.*
import com.caressa.model.home.*
import com.caressa.model.security.HLMTLoginModel
import com.caressa.model.security.PhoneExistsModel
import com.caressa.model.shr.ListRelativesModel
import com.caressa.repository.*
import com.caressa.repository.utils.Resource
import okhttp3.RequestBody

class HomeManagementUseCase( private val homeRepository: HomeRepository , private val hraRepository : HraRepository ,
                             private val userRepository : UserRepository ) {

    suspend fun invokeSaveFeedback(isForceRefresh : Boolean, data: SaveFeedbackModel): LiveData<Resource<SaveFeedbackModel.SaveFeedbackResponse>> {
        return Transformations.map(
            homeRepository.saveFeedback(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokePasswordChange(isForceRefresh : Boolean, data: PasswordChangeModel): LiveData<Resource<PasswordChangeModel.ChangePasswordResponse>> {
        return Transformations.map(
            homeRepository.passwordChange(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeContactUs(isForceRefresh : Boolean, data: ContactUsModel): LiveData<Resource<ContactUsModel.ContactUsResponse>> {
        return Transformations.map(
            homeRepository.contactUs(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeGetUserDetails(isForceRefresh : Boolean, data: UserDetailsModel): LiveData<Resource<UserDetailsModel.UserDetailsResponse>> {
        return Transformations.map(
            homeRepository.getUserDetails(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeUpdateUserDetails(isForceRefresh : Boolean, data: UpdateUserDetailsModel): LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> {
        return Transformations.map(
            homeRepository.updateUserDetails(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeGetProfileImage(isForceRefresh : Boolean, data: ProfileImageModel): LiveData<Resource<ProfileImageModel.ProfileImageResponse>> {
        return Transformations.map(
            homeRepository.getProfileImage(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeUploadProfileImage(personID: RequestBody, fileName: RequestBody, documentTypeCode: RequestBody,
                                          byteArray: RequestBody,authTicket: RequestBody): LiveData<Resource<UploadProfileImageResponce>> {
        return Transformations.map(
            homeRepository.uploadProfileImage(personID,fileName,documentTypeCode,byteArray,authTicket)) {
            it
        }
    }


    suspend fun invokeRemoveProfileImage(isForceRefresh : Boolean, data: RemoveProfileImageModel): LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> {
        return Transformations.map(
            homeRepository.removeProfileImage(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeRelativesList(isForceRefresh : Boolean, data: ListRelativesModel): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {
        return Transformations.map(
            homeRepository.fetchRelativesList(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeaddNewRelative(isForceRefresh : Boolean, data: AddRelativeModel): LiveData<Resource<AddRelativeModel.AddRelativeResponse>> {
        return Transformations.map(
            homeRepository.addNewRelative(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeupdateRelative(isForceRefresh : Boolean, data: UpdateRelativeModel): LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> {
        return Transformations.map(
            homeRepository.updateRelative(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeRemoveRelative(isForceRefresh : Boolean, data: RemoveRelativeModel): LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> {
        return Transformations.map(
            homeRepository.removeRelative(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeDoctorsList(isForceRefresh : Boolean, data: FamilyDoctorsListModel): LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> {
        return Transformations.map(
            homeRepository.fetchDoctorsList(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeSpecialitiesList(isForceRefresh : Boolean, data: ListDoctorSpecialityModel): LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> {
        return Transformations.map(
            homeRepository.fetchSpecialityList(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeAddDoctor(isForceRefresh : Boolean, data: FamilyDoctorAddModel): LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> {
        return Transformations.map(
            homeRepository.addDoctor(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeUpdateDoctor(isForceRefresh : Boolean, data: FamilyDoctorUpdateModel): LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> {
        return Transformations.map(
            homeRepository.updateDoctor(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeRemoveDoctor(isForceRefresh : Boolean, data: RemoveDoctorModel): LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> {
        return Transformations.map(
            homeRepository.removeDoctor(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokePhoneExist(isForceRefresh : Boolean, data: PhoneExistsModel): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {
        return Transformations.map(
            userRepository.isPhoneExist(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeGetLoggedInPersonDetails( ) : Users {
        return homeRepository.getLoggedInPersonDetails()
    }

    suspend fun invokeUpdateUserDetails( name : String ,personId : Int  ) {
        return homeRepository.updateUserDetails( name , personId )
    }

    suspend fun invokeUpdateUserProfileImgPath( name : String ,path : String  ) {
        return homeRepository.updateUserProfileImgPath( name , path )
    }

    suspend fun invokeGetUserRelatives() : List<UserRelatives> {
        return homeRepository.getUserRelatives()
    }

    suspend fun invokeGetUserRelativesExceptSelf() : List<UserRelatives> {
        return homeRepository.getUserRelativesExceptSelf()
    }

    suspend fun invokeGetUserRelativeSpecific( relationShipCode : String ) : List<UserRelatives> {
        return homeRepository.getUserRelativeSpecific( relationShipCode )
    }

    suspend fun invokeGetUserRelativeForRelativeId( relativeId : String ) : List<UserRelatives> {
        return homeRepository.getUserRelativeForRelativeId( relativeId )
    }

    suspend fun invokeGetHraSummaryDetails( ) : HRASummary {
        return hraRepository.getHraSummaryDetails()
    }

    suspend fun invokeClearTablesForSwitchProfile() {
        homeRepository.clearTablesForSwitchProfile()
    }

    suspend fun invokeGetUserRelativeDetailsByRelativeId( relativeId : String ) : UserRelatives {
        return homeRepository.getUserRelativeDetailsByRelativeId( relativeId )
    }

    suspend fun invokeHLMT360LoginResponse(isForceRefresh : Boolean, data: HLMTLoginModel): LiveData<Resource<HLMTLoginModel.LoginResponse>> {
        return Transformations.map(
            homeRepository.hlmt360LoginResponse(isForceRefresh,data)) {
            it // Place here your specific logic actions
        }
    }
}