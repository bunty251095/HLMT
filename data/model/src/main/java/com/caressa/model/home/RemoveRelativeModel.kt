package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class RemoveRelativeModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("from")
        val from: Int = 1051,
/*    @SerializedName("Message")
        val message: String = "",*/
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("ID")
        val id: ArrayList<Int> = ArrayList()
    )

    data class Relative(
        @SerializedName("RelativeID")
        val relativeID: String = ""
    )

    data class RemoveRelativeResponse(
        val isSuccess: String = ""
    )

}