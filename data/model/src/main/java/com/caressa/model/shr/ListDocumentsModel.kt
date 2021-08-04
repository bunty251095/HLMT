package com.caressa.model.shr

import com.caressa.model.BaseRequest
import com.caressa.model.entity.HealthDocument
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListDocumentsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Message")
        val message: String = "Synchronizing your profile",
        @SerializedName("SearchCriteria")
        val searchCriteria: SearchCriteria = SearchCriteria()
    )

    data class SearchCriteria(
        @SerializedName("NumberOfReadings")
        val numberOfReadings: Int = 5,
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("Mode")
        val mode: String = "FM"
    )

    data class ListDocumentsResponse(
        @SerializedName("Documents")
        var documents: List<HealthDocument> = listOf()
    )

    data class Document(
        @SerializedName("Comments")
        var comments: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        var createdDate: String = "",
        @SerializedName("DocumentType")
        val documentType: DocumentType = DocumentType(),
        @SerializedName("DocumentTypeCode")
        var documentTypeCode: String = "",
        @SerializedName("FileBytes")
        val fileBytes: Any? = Any(),
        @SerializedName("FileName")
        var fileName: String = "",
        @SerializedName("FilePath")
        var filePath: Any? = Any(),
        @SerializedName("ID")
        var id: Int = 0,
        @SerializedName("Keywords")
        val keywords: List<String> = listOf(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("OwnerCode")
        val ownerCode: Any? = Any(),
        @SerializedName("PersonID")
        var personID: Int = 0,
        @SerializedName("PersonName")
        val personName: String = "",
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("Title")
        var title: String = ""
    )

    data class DocumentType(
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any()
    )

}