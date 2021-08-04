package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveCloudMessagingIdModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {


    data class JSONDataRequest(
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("Key")
        val key: Key = Key()
    )

    data class Key(
        @SerializedName("Mode")
        val mode: String = "ADD",
        @SerializedName("AppIdentifier")
        val appIdentifier: String = Configuration.AppIdentifier,
        @SerializedName("DeviceType")
        val deviceType: String = "ANDROID",
        @SerializedName("AccountID")
        val accountID: String = "",
        @SerializedName("RegistrationID")
        val registrationID: String = ""
    )

    data class SaveCloudMessagingIdResponse(
        @SerializedName("RegistrationID")
        val registrationID: String = ""
    )
}