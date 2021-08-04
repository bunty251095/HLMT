package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HLMTLoginModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("Username")
        @Expose
        private val username: String = "",
        @SerializedName("Password")
        @Expose
        private val password: String = "",
        @SerializedName("Source")
        @Expose
        private val source: String = "ANDROID_MOBILE",
        @SerializedName("AccountID")
        @Expose
        private val accountID: String = "",
        @SerializedName("Mode")
        @Expose
        private val mode: String = "",
    )

    data class LoginResponse(

        @SerializedName("HLMTUserID")
        @Expose
        var HLMTUserID: String? = "",
        @SerializedName("LoginStatus")
        @Expose
        var loginStatus: String? = "",
        @SerializedName("IsHLMTUser")
        @Expose
        var isHLMTUser: String? = "",
        @SerializedName("HLMTUserName")
        @Expose
        var hlmtUserName: String? = "",
        @SerializedName("AccountLinkStatus")
        @Expose
        var accountLinkStatus: String? = ""

    )

}