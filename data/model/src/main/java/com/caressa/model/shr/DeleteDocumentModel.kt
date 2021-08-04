package com.caressa.model.shr

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteDocumentModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("DocumentIDS")
        val documentIDS: List<String> = listOf(),
        @SerializedName("RequestType")
        val requestType: String = "POST"
    )

    data class DeleteDocumentResponse(
        @SerializedName("IsProcessed")
        var isProcessed: String = ""
    )

}
