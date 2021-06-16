package com.caressa.security.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.Users
import com.caressa.model.security.*
import com.caressa.repository.UserRepository
import com.caressa.repository.utils.Resource

class UserManagementUseCase(private val repository: UserRepository){

    suspend operator fun invoke(isForceRefresh : Boolean, data: String): LiveData<Resource<Users>> {
        return Transformations.map(
            repository.getLoginResponse(isForceRefresh,data)) {
            it // Place here your specific logic actions
        }
    }

    suspend fun invokeRegistration(data: String): LiveData<Resource<Users>> {
        return Transformations.map(
            repository.fetchRegistrationResponse(data)){
            it
        }
    }

    suspend fun invokeEmailExist(isForceRefresh : Boolean, data: EmailExistsModel): LiveData<Resource<EmailExistsModel.IsExistResponse>> {
        return Transformations.map(
            repository.isEmailExist(isForceRefresh,data)) {
            it // Place here your specific logic actions
        }
    }

    suspend fun invokePhoneExist(isForceRefresh : Boolean, data: PhoneExistsModel): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {
        return Transformations.map(
            repository.isPhoneExist(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeGenerateOTP(isForceRefresh : Boolean, data: GenerateOtpModel): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return Transformations.map(
            repository.getGenerateOTPResponse(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeValidateOTP(isForceRefresh : Boolean, data: GenerateOtpModel): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {
        return Transformations.map(
            repository.getValidateOTPResponse(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeUpdatePassword(isForceRefresh : Boolean, data: ChangePasswordModel): LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>> {
        return Transformations.map(
            repository.updatePassword(isForceRefresh,data)) {
            it
        }
    }

    suspend  fun invokeTermsCondition(isForceRefresh : Boolean, data: TermsConditionsModel): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> {
        return Transformations.map(
            repository.getTermsConditionsResponse(isForceRefresh,data)) {
            it
        }
    }

    // New Api Integration

    suspend fun invokeLoginNameExist(isForceRefresh : Boolean, data: LoginNameExistsModel): LiveData<Resource<LoginNameExistsModel.IsExistResponse>> {
        return Transformations.map(
            repository.isLoginNameExist(isForceRefresh,data)) {
            it // Place here your specific logic actions
        }
    }

    suspend fun invokeLoginResponse(isForceRefresh : Boolean, data: LoginModel): LiveData<Resource<LoginModel.Response>> {
        return Transformations.map(
            repository.hlmtLoginResponse(isForceRefresh,data)) {
            it // Place here your specific logic actions
        }
    }

    suspend fun invokeAddUserInfo(data: Users) {
            repository.saveUserInfo(data)

    }

}