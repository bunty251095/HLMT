package com.caressa.model.shr

import com.caressa.model.BaseRequest
import com.caressa.model.entity.DocumentType
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListDocumentTypesModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("from")
        val from: String = "",
        @SerializedName("Message")
        val message: String = ""
    )

    data class ListDocumentTypesResponse(
        @SerializedName("DocumentTypes")
        var documentTypes: List<DocumentType> = listOf()
    )

    data class DocType(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Description")
        val description: String = "",
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