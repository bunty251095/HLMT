package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgetPasswordModel(
@SerializedName("JSONData")
@Expose
private val jsonData: String
) : BaseRequest(Header(authTicket = "")) {

    data class JSONDataRequest(
        @SerializedName("LoginName")
        @Expose
        private val emailAddress: String = ""
    )

    data class ForgetPasswordResponse(
        @SerializedName("SUCCESS")
        @Expose
        var success: String? = "",
        @SerializedName("New Password")
        @Expose
        var newPassword: String? = "",
        @SerializedName("Message")
        @Expose
        var message: String? = ""
    )
}