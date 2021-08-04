package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.caressa.model.entity.AppVersion
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CheckAppUpdateModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("App")
        val app: String = "",
        @SerializedName("Device")
        val device: String = "",
        @SerializedName("AppVersion")
        val appVersion: String = ""
    )

    data class CheckAppUpdateResponse(
        @SerializedName("Result")
        var result: Result = Result()
    )

    data class Result(
        @SerializedName("AppVersion")
        val appVersion: List<AppVersion> = listOf()
    )

/*    data class AppVersion(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("Application")
        val application: String = "",
        @SerializedName("CurrentVersion")
        val currentVersion: String = "",
        @SerializedName("DeviceType")
        val deviceType: String = "",
        @SerializedName("ForceUpdate")
        val forceUpdate: Boolean = false,
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("ImagePath")
        val imagePath: String = "",
        @SerializedName("APICallInterval")
        val apiCallInterval: Int = 0,
        @SerializedName("Features")
        val features: Any = Any(),
        @SerializedName("ReleasedDate")
        val releasedDate: Any = Any()
    )*/

}