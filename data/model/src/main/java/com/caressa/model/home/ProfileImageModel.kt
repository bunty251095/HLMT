package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileImageModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("DocumentID")
        val documentID: String = ""
    )

    data class ProfileImageResponse(
        @SerializedName("HealthRelatedDocument")
        val healthRelatedDocument: HealthRelatedDocument = HealthRelatedDocument()
    )

    data class HealthRelatedDocument(
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("FileName")
        val fileName: String = "",
        @SerializedName("FileBytes")
        val fileBytes: String = "",
        @SerializedName("DocumentTypeCode")
        val documentTypeCode: String = "",
        @SerializedName("Title")
        val title: Any = Any(),
        @SerializedName("PersonName")
        val personName: Any = Any(),
        @SerializedName("Comments")
        val comments: Any = Any(),
        @SerializedName("Keywords")
        val keywords: List<Any> = listOf()
    )

}