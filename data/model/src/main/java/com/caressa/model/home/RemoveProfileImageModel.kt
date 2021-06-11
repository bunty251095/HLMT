package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class RemoveProfileImageModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(Header(authTicket = authToken))
{

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: Int = 0
    )

    data class RemoveProfileImageResponse(
        @SerializedName("IsProcessed")
        val isProcessed : String = ""
    )

}