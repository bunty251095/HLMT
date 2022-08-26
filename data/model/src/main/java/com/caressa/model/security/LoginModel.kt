package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("Mode")
        @Expose
        private val mode: String = "",
        @SerializedName("PartnerCode")
        @Expose
        private val partnerCode: String = Configuration.PartnerCode,
        @SerializedName("PhoneNumber")
        @Expose
        private val phoneNumber: String = "",
        @SerializedName("Password")
        @Expose
        private val password: String = "",
        @SerializedName("Name")
        @Expose
        private val name: String = "",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = "",
        @SerializedName("Handshake")
        @Expose
        private val handshake: String = Configuration.Handshake,
        @SerializedName("UTMSource")
        @Expose
        private val utmSource: String = Configuration.UTMSource,
        @SerializedName("UTMMedium")
        @Expose
        private val utmMedium: String = Configuration.UTMMedium,
        @SerializedName("DateOfBirth")
        @Expose
        private val dateOfBirth: String = "",
        @SerializedName("Gender")
        @Expose
        private val gender: String = ""
    )


    data class Response(
        @SerializedName("Response")
        var response: LoginResponse = LoginResponse(),
    )

    data class LoginResponse(
        @SerializedName("Data")
        var loginData: Data = Data(),
        @SerializedName("StatusCode")
        var statusCode: Int = 0
    )

    data class Data(
        @SerializedName("Context")
        var context: String = "",
        @SerializedName("LanguageCode")
        var languageCode: String = "",
        @SerializedName("PartnerCode")
        var partnerCode: String = "",
        @SerializedName("Name")
        var name: String = "",
        @SerializedName("EmailAddress")
        var emailAddress: String = "",
        @SerializedName("DateOfBirth")
        var dateOfBirth: String = "",
        @SerializedName("Gender")
        var gender: String = "",
        @SerializedName("PersonID")
        var personID: String = "",
        @SerializedName("FirstName")
        var firstName: String = "",
        @SerializedName("LastName")
        var lastName: String = "",
        @SerializedName("IsActive")
        var isActive: Boolean = false,
        @SerializedName("Age")
        var age: Int = 0,
        @SerializedName("PartnerID")
        var partnerID: String = "",
        @SerializedName("MaritalStatus")
        var maritalStatus: String = "",
        @SerializedName("PhoneNumber")
        var phoneNumber: String = "",
        @SerializedName("TNCIsAccepted")
        var tNCIsAccepted: Boolean = true,
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @SerializedName("AccountStatus")
        var accountStatus: String = "",
        @SerializedName("AccountType")
        var accountType: String = "",
        @SerializedName("CountryName")
        var countryName: String = "",
        @SerializedName("DialingCode")
        var dialingCode: String = "",
        @SerializedName("IsAuthenticated")
        var isAuthenticated: Boolean = false,
        @SerializedName("PATH")
        var pATH: String = "",
        @SerializedName("PROFILE_IMG_PATH")
        var pROFILEIMGPATH: String = "",
        @SerializedName("IsHLMTUser")
        var IsHLMTUser: String = "",
        @SerializedName("AccountLinkStatus")
        var accountLinkStatus: String = "",
        @SerializedName("HLMTUserID")
        var HLMTUserID: String = "",
        @SerializedName("HLMTUserName")
        var HLMTUserName: String = "",

//        @SerializedName("ROLES")
//        var rOLES: String = "",

    )

}