package com.caressa.remote

import com.caressa.model.security.*

/**
 * Implementation of [UserService] interface
 */
class SecurityDatasource(
    private val defaultUserService: ApiService,
    private val encryptedUserService: ApiService
) {

    fun getLoginResponse(data: String) = defaultUserService.loginDocumentProxyAPI(data)

    fun fetchRegistrationResponse(data: String) = defaultUserService.registerDocumentProxyAPI(data)

    fun fetchEmailExistResponse(data: EmailExistsModel) =
        encryptedUserService.checkEmailExistsAPI(data)

    fun fetchPhoneExistResponse(data: PhoneExistsModel) =
        encryptedUserService.checkPhoneExistsAPI(data)

    fun getTermsConditionsResponce(data: TermsConditionsModel) =
        encryptedUserService.getTermsAndConditionsForPartnerAPI(data)

    fun getGenerateOTPResponse(data: GenerateOtpModel) =
        encryptedUserService.generateOTPforUserAPI(data)

    fun getValidateOTPResponse(data: GenerateOtpModel) =
        encryptedUserService.validateOTPforUserAPI(data)

    fun updatePasswordResponse(data: ChangePasswordModel) =
        encryptedUserService.updatePasswordAPI(data)

    fun fetchLoginNameExistResponse(data: LoginNameExistsModel) =
        encryptedUserService.checkLoginNameExistsAPI(data)

    fun fetchHlmtLoginResponse(data: LoginModel) = encryptedUserService.hlmtLoginAPI(data)

    fun fetchHLMT360LoginResponse(data: HLMTLoginModel) =
        encryptedUserService.hlmt360UserLogin(data)

}