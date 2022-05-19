package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResetPasswordModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("OldPassword")
        @Expose
        private val oldPassword: String? = "",
        @SerializedName("NewPassword")
        @Expose
        private val newPassword: String = "",
        @SerializedName("LoginName")
        @Expose
        private val loginName: String = "",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = ""
    )

    data class ResetPasswordResponse(
        @SerializedName("SUCCESS")
        @Expose
        var success: String? = "",
        @SerializedName("NewPassword")
        @Expose
        var newPassword: String? = ""
    )
}