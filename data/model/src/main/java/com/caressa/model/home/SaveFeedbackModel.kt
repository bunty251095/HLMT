package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveFeedbackModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("AppFeedback")
        val appFeedback: AppFeedback = AppFeedback()
    )

    data class AppFeedback(
        @SerializedName("App")
        var app: String = "",
        @SerializedName("AppVersion")
        var appVersion: String = "",
        @SerializedName("DeviceType")
        var deviceType: String = "",
        @SerializedName("DeviceName")
        var deviceName: String = "",
        @SerializedName("PersonID")
        var personID: Int = 0,
        @SerializedName("AccountID")
        var accountID: Int = 0,
        @SerializedName("EmailID")
        val emailID: String = "",
        @SerializedName("PhoneNumber")
        var phoneNumber: String = "",
        @SerializedName("Type")
        var type: String = "",
        @SerializedName("Feedback")
        var feedback: String = ""
    )

    data class SaveFeedbackResponse(
        @SerializedName("AppFeedback")
        val appFeedback: AppFeedbackResp = AppFeedbackResp()
    )

    data class AppFeedbackResp(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("App")
        val app: String = "",
        @SerializedName("AppVersion")
        val appVersion: String = "",
        @SerializedName("DeviceType")
        val deviceType: String = "",
        @SerializedName("DeviceName")
        val deviceName: String = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("AccountID")
        val accountID: Int = 0,
        @SerializedName("EmailID")
        val emailID: String = "",
        @SerializedName("PhoneNumber")
        val phoneNumber: String = "",
        @SerializedName("Type")
        val type: String = "",
        @SerializedName("Feedback")
        val feedback: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any()
    )

}