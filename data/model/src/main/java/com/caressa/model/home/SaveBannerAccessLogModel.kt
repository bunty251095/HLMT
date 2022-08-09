package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveBannerAccessLogModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("CampaignAccessDetails")
        @Expose
        private val campaignAccessDetails: CampaignAccessDetails = CampaignAccessDetails()
    )

    data class CampaignAccessDetails(
        @SerializedName("CampaignID")
        val campaignID: Int = 0,
        @SerializedName("PersonID")
        val personId: Int = 0,
        @SerializedName("AccountID")
        val accountID: Int = 0,
        @SerializedName("PartnerCode")
        val partnerCode: String = "",
        @SerializedName("Service")
        val service: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("URL")
        val url: String = "",
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("AppVersion")
        val appVersion: String = "",
        @SerializedName("Device")
        val device: String = "",
        @SerializedName("DeviceType")
        val deviceType: String = "",
        @SerializedName("Platform")
        val platform: String = ""
    )

    data class SaveBannerAccessLogResponse(
        @SerializedName("CampaignAccessDetails")
        val campaignAccessDetails: CampaignAccessDetailsResp = CampaignAccessDetailsResp()
    )

    data class CampaignAccessDetailsResp(
        @SerializedName("ID")
        val id: Int? = 0,
        @SerializedName("CampaignID")
        val campaignID: Int? = 0,
        @SerializedName("PersonID")
        val personID: Int? = 0,
        @SerializedName("AccountID")
        val accountID: Int? = 0,
        @SerializedName("PartnerCode")
        val partnerCode: String? = "",
        @SerializedName("Service")
        val service: String? = "",
        @SerializedName("Code")
        val code: String? = "",
        @SerializedName("URL")
        val url: String? = "",
        @SerializedName("Description")
        val description: String? = "",
        @SerializedName("AppVersion")
        val appVersion: String? = "",
        @SerializedName("Device")
        val device: String? = "",
        @SerializedName("DeviceType")
        val deviceType: String? = "",
        @SerializedName("Platform")
        val platform: String? = ""
    )

}
