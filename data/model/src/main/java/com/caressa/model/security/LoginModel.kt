package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginModel(@SerializedName("JSONData")
                      @Expose
                      private val jsonData: String) : BaseRequest(Header(authTicket = "")) {

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
        private val name: String = ""
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
        @SerializedName("AccountID")
        var accountID: Int =0,
        @SerializedName("AccountStatus")
        var accountStatus: String = "",
        @SerializedName("AccountType")
        var accountType: String = "",
        @SerializedName("Context")
        var context: String = "",
        @SerializedName("CountryName")
        var countryName: String = "",
        @SerializedName("DialingCode")
        var dialingCode: String = "",
        @SerializedName("EmailAddress")
        var emailAddress: String = "",
        @SerializedName("IsActive")
        var isActive: Boolean =false,
        @SerializedName("IsAuthenticated")
        var isAuthenticated: Boolean =false,
        @SerializedName("Name")
        var name: String = "",
        @SerializedName("PATH")
        var pATH: String = "",
        @SerializedName("PROFILE_IMG_PATH")
        var pROFILEIMGPATH: String = "",
        @SerializedName("PartnerCode")
        var partnerCode: String = "",
        @SerializedName("PartnerID")
        var partnerID: String = "",
        @SerializedName("PhoneNumber")
        var phoneNumber: String = "",
//        @SerializedName("ROLES")
//        var rOLES: String = "",
        @SerializedName("TNCIsAccepted")
        var tNCIsAccepted: Boolean = true
    )

}