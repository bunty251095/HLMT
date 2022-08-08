package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListActiveBannerModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("CampaignFilters")
        @Expose
        private val campaignFilters: String? = null
    )

    data class ListActiveBannerResponse(
        @SerializedName("CampaignDetailsList")
        val campaignDetailsList: MutableList<CampaignDetails> = mutableListOf()
    )

    data class CampaignDetails(
        @SerializedName("CampaignID")
        val campaignID: String? = "",
        @SerializedName("DisplayOrder")
        val displayOrder: String? = "",
        @SerializedName("Image")
        val image: String? = "",
        @SerializedName("RedirectionUrl")
        val redirectionUrl: String? = ""
    )

}