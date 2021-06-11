package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ContactUsModel(@SerializedName("JSONData")
                     @Expose
                     private val jsonData: String,
                     private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ApplicationCode")
        val applicationCode: String = Configuration.ApplicationCode,
        @SerializedName("PartnerCode")
        val partnerCode: String = Configuration.PartnerCode,
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("FromEmail")
        val fromEmail: String = "",
        @SerializedName("FromMobile")
        val fromMobile: String = "",
        @SerializedName("Message")
        val message: String = "",
        @SerializedName("Source ")
        val source: String = "MobileApp",
        @SerializedName("RequestType")
        val requestType: String = "POST"
    )

    data class ContactUsResponse(
        // random parameter , since nothing in Response JSON Data
        @SerializedName("IsProcessed")
        var isProcessed: Boolean = false
    )

}