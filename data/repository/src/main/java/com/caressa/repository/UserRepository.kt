package com.caressa.repository

import androidx.lifecycle.LiveData
import com.caressa.local.dao.VivantUserDao
import com.caressa.model.*
import com.caressa.model.entity.Users
import com.caressa.model.security.*
import com.caressa.remote.SecurityDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import timber.log.Timber

interface UserRepository {
    suspend fun getLoginResponse(forceRefresh: Boolean = false,encryptedData: String) : LiveData<Resource<Users>>
    suspend fun fetchRegistrationResponse(encryptedData: String) : LiveData<Resource<Users>>
    suspend fun isEmailExist(forceRefresh: Boolean = false, data: EmailExistsModel) :LiveData<Resource<EmailExistsModel.IsExistResponse>>
    suspend fun isPhoneExist(forceRefresh: Boolean = false, data: PhoneExistsModel) :LiveData<Resource<PhoneExistsModel.IsExistResponse>>
//    suspend fun fetchFacebookLogin(forceRefresh: Boolean = false, data: EmailExistsModel) :LiveData<Resource<EmailOrPhoneExistResponse>>
//    suspend fun fetchGoogleLogin(forceRefresh: Boolean = false, data: EmailExistsModel) :LiveData<Resource<EmailOrPhoneExistResponse>>
    suspend fun getTermsConditionsResponse(forceRefresh: Boolean = false, encryptedData: TermsConditionsModel): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>>
    suspend fun getGenerateOTPResponse(forceRefresh: Boolean = false, data: GenerateOtpModel) :LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>
    suspend fun getValidateOTPResponse(forceRefresh: Boolean = false, data: GenerateOtpModel) :LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>
    suspend fun updatePassword(forceRefresh: Boolean = false, data: ChangePasswordModel) : LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>>

    suspend fun isLoginNameExist(forceRefresh: Boolean = false, data: LoginNameExistsModel) :LiveData<Resource<LoginNameExistsModel.IsExistResponse>>
    suspend fun hlmtLoginResponse(forceRefresh: Boolean = false, data: LoginModel) :LiveData<Resource<LoginModel.Response>>
    suspend fun hlmt360LoginResponse(forceRefresh: Boolean = false, data: HLMTLoginModel) :LiveData<Resource<HLMTLoginModel.LoginResponse>>
    suspend fun saveUserInfo(data: Users)

}

 class UserRepositoryImpl(private val datasource: SecurityDatasource,
                         private val vudao: VivantUserDao): UserRepository {

    override suspend fun fetchRegistrationResponse(encryptedData: String): LiveData<Resource<Users>> {
        Timber.i("Inside => fetchRegistrationResponse UserRepository")
        return object : NetworkBoundResource<Users,ApiResponse<Users>>(){
            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): Users {
                val db =  vudao.getUser()
//                Timber.i("processResponse=> $db")
                return db
            }

            override fun createCallAsync(): Deferred<ApiResponse<Users>> {
                return datasource.fetchRegistrationResponse(encryptedData)
            }

            override fun processResponse(response: ApiResponse<Users>): Users {
                if(response.statusCode.equals("400")) {
                    return response.data
                }else{
                    return response.data
                }
            }

            override suspend fun saveCallResults(items: Users) {
                vudao.deleteAllRecords()
                vudao.insert(items)
            }

            override fun shouldFetch(data: Users?): Boolean
                    = true

        }.build().asLiveData()
    }

    override suspend fun isPhoneExist(forceRefresh: Boolean, data: PhoneExistsModel): LiveData<Resource<PhoneExistsModel.IsExistResponse>> {

        return object : NetworkBoundResource<PhoneExistsModel.IsExistResponse,BaseResponse<PhoneExistsModel.IsExistResponse>>(){

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<PhoneExistsModel.IsExistResponse>): PhoneExistsModel.IsExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: PhoneExistsModel.IsExistResponse) {

            }

            override fun shouldFetch(data: PhoneExistsModel.IsExistResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): PhoneExistsModel.IsExistResponse {
                return PhoneExistsModel.IsExistResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<PhoneExistsModel.IsExistResponse>> {
                return datasource.fetchPhoneExistResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun isEmailExist(forceRefresh: Boolean, data: EmailExistsModel): LiveData<Resource<EmailExistsModel.IsExistResponse>> {

        return object : NetworkBoundResource<EmailExistsModel.IsExistResponse,BaseResponse<EmailExistsModel.IsExistResponse>>(){

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<EmailExistsModel.IsExistResponse>): EmailExistsModel.IsExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: EmailExistsModel.IsExistResponse) {

            }

            override fun shouldFetch(data: EmailExistsModel.IsExistResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): EmailExistsModel.IsExistResponse {
                return EmailExistsModel.IsExistResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<EmailExistsModel.IsExistResponse>> {
                return datasource.fetchEmailExistResponse(data)
            }


        }.build().asLiveData()
    }

     override suspend fun isLoginNameExist(forceRefresh: Boolean, data: LoginNameExistsModel): LiveData<Resource<LoginNameExistsModel.IsExistResponse>> {

         return object : NetworkBoundResource<LoginNameExistsModel.IsExistResponse,BaseResponse<LoginNameExistsModel.IsExistResponse>>(){

             override fun shouldStoreInDb(): Boolean = false

             override fun processResponse(response: BaseResponse<LoginNameExistsModel.IsExistResponse>): LoginNameExistsModel.IsExistResponse {
                 return response.jSONData
             }

             override suspend fun saveCallResults(items: LoginNameExistsModel.IsExistResponse) {

             }

             override fun shouldFetch(data: LoginNameExistsModel.IsExistResponse?): Boolean {
                 return true
             }

             override suspend fun loadFromDb(): LoginNameExistsModel.IsExistResponse {
                 return LoginNameExistsModel.IsExistResponse()
             }

             override fun createCallAsync(): Deferred<BaseResponse<LoginNameExistsModel.IsExistResponse>> {
                 return datasource.fetchLoginNameExistResponse(data)
             }


         }.build().asLiveData()
     }

     override suspend fun hlmtLoginResponse(
         forceRefresh: Boolean,
         data: LoginModel
     ): LiveData<Resource<LoginModel.Response>> {
         return object : NetworkBoundResource<LoginModel.Response,BaseResponse<LoginModel.Response>>(){
             override fun processResponse(response: BaseResponse<LoginModel.Response>): LoginModel.Response {
                 Timber.i("processResponse=> "+response.jSONData)
                 return response.jSONData
             }

             override suspend fun saveCallResults(items: LoginModel.Response) {

             }

             override fun shouldFetch(data: LoginModel.Response?): Boolean {
                 return true
             }

             override fun shouldStoreInDb(): Boolean {
                 return false
             }

             override suspend fun loadFromDb(): LoginModel.Response {
                 return LoginModel.Response()
             }

             override fun createCallAsync(): Deferred<BaseResponse<LoginModel.Response>> {
                 return datasource.fetchHlmtLoginResponse(data)
             }

         }.build().asLiveData()
     }

     override suspend fun hlmt360LoginResponse(
         forceRefresh: Boolean,
         data: HLMTLoginModel
     ): LiveData<Resource<HLMTLoginModel.LoginResponse>> {
         return object : NetworkBoundResource<HLMTLoginModel.LoginResponse,BaseResponse<HLMTLoginModel.LoginResponse>>(){
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

     override suspend fun saveUserInfo(data: Users) {
         vudao.deleteAllRecords()
         vudao.insert(data)
     }

     override suspend fun updatePassword(forceRefresh: Boolean, data: ChangePasswordModel)
             : LiveData<Resource<ChangePasswordModel.ChangePasswordResponse>> {

         return object : NetworkBoundResource<ChangePasswordModel.ChangePasswordResponse,BaseResponse<ChangePasswordModel.ChangePasswordResponse>>() {

             override fun shouldStoreInDb(): Boolean = false

             override fun processResponse(response: BaseResponse<ChangePasswordModel.ChangePasswordResponse>): ChangePasswordModel.ChangePasswordResponse {
                 return response.jSONData
             }

             override suspend fun saveCallResults(items: ChangePasswordModel.ChangePasswordResponse) {}

             override fun shouldFetch(data: ChangePasswordModel.ChangePasswordResponse?): Boolean {
                 return true
             }

             override suspend fun loadFromDb(): ChangePasswordModel.ChangePasswordResponse {
                 return ChangePasswordModel.ChangePasswordResponse()
             }

             override fun createCallAsync(): Deferred<BaseResponse<ChangePasswordModel.ChangePasswordResponse>> {
                 return datasource.updatePasswordResponse(data)
             }

         }.build().asLiveData()
     }

    override suspend fun getGenerateOTPResponse(forceRefresh: Boolean, data: GenerateOtpModel): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {

        return object : NetworkBoundResource<GenerateOtpModel.GenerateOTPResponse,BaseResponse<GenerateOtpModel.GenerateOTPResponse>>(){

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<GenerateOtpModel.GenerateOTPResponse>): GenerateOtpModel.GenerateOTPResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GenerateOtpModel.GenerateOTPResponse) {
            }

            override fun shouldFetch(data: GenerateOtpModel.GenerateOTPResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): GenerateOtpModel.GenerateOTPResponse {
                return GenerateOtpModel.GenerateOTPResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<GenerateOtpModel.GenerateOTPResponse>> {
                return datasource.getGenerateOTPResponse(data)
            }

        }.build().asLiveData()
    }

    override suspend fun getValidateOTPResponse(forceRefresh: Boolean, data: GenerateOtpModel): LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> {

        return object : NetworkBoundResource<GenerateOtpModel.GenerateOTPResponse,BaseResponse<GenerateOtpModel.GenerateOTPResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<GenerateOtpModel.GenerateOTPResponse>): GenerateOtpModel.GenerateOTPResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GenerateOtpModel.GenerateOTPResponse) {
            }

            override fun shouldFetch(data: GenerateOtpModel.GenerateOTPResponse?): Boolean {
                return true
            }

            override suspend fun loadFromDb(): GenerateOtpModel.GenerateOTPResponse {
                return GenerateOtpModel.GenerateOTPResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<GenerateOtpModel.GenerateOTPResponse>> {
                return datasource.getValidateOTPResponse(data)
            }

        }.build().asLiveData()
    }

     override suspend fun getTermsConditionsResponse(forceRefresh: Boolean, encryptedData: TermsConditionsModel): LiveData<Resource<TermsConditionsModel.TermsConditionsResponse>> {

        return  object : NetworkBoundResource<TermsConditionsModel.TermsConditionsResponse,BaseResponse<TermsConditionsModel.TermsConditionsResponse>>() {

            override fun processResponse(response: BaseResponse<TermsConditionsModel.TermsConditionsResponse>): TermsConditionsModel.TermsConditionsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: TermsConditionsModel.TermsConditionsResponse) {

            }

            override fun shouldFetch(data: TermsConditionsModel.TermsConditionsResponse?): Boolean {
                return true
            }

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): TermsConditionsModel.TermsConditionsResponse {
                return TermsConditionsModel.TermsConditionsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<TermsConditionsModel.TermsConditionsResponse>> {
                return datasource.getTermsConditionsResponce(encryptedData)
            }


        }.build().asLiveData()
    }

    override suspend fun getLoginResponse(forceRefresh: Boolean, encryptedData: String): LiveData<Resource<Users>> {

        return object : NetworkBoundResource<Users,ApiResponse<Users>>() {
            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): Users {
                val db =  vudao.getUser()
//                Timber.i("processResponse=> $db")
                return db
            }

            override fun createCallAsync(): Deferred<ApiResponse<Users>> {
                return datasource.getLoginResponse(encryptedData)
            }

            override fun processResponse(response: ApiResponse<Users>): Users {
                if(response.statusCode.equals("400")) {
                    return response.data
                }else{
                    return response.data
                }
            }

            override suspend fun saveCallResults(items: Users) {
                vudao.deleteAllRecords()
                items.name = ""
                vudao.insert(items)
            }

            override fun shouldFetch(data: Users?): Boolean
                    = true

        }.build().asLiveData()

    }
}