package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PasswordChangeModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("LoginName")
        @Expose
        private val loginName: String = "",
        @SerializedName("EmailAddress")
        @Expose
        private val emailAddress: String = "",
        @SerializedName("PrimaryPhone")
        @Expose
        private val primaryPhone: String = "",
        @SerializedName("OldPassword")
        @Expose
        private val oldPassword: String = "",
        @SerializedName("NewPassword")
        @Expose
        private val newPassword: String = ""
    )

    data class ChangePasswordResponse(
        @SerializedName("NewPassword")
        @Expose
        val newPassword: String = ""
    )
}