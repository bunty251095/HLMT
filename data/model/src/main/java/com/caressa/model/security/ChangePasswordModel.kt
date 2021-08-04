package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChangePasswordModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("AccountID")
        @Expose
        private val accountID: String = "0",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = "",
        @SerializedName("NewPassword")
        @Expose
        private val newPassword: String = ""
    )

    data class ChangePasswordResponse(
        @SerializedName("IsPasswordUpdate")
        @Expose
        var isPasswordUpdate: String? = ""
    )
}