package com.caressa.model.shr

import com.caressa.model.BaseRequest
import com.caressa.model.entity.HealthDocument
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveDocumentModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(  Header(authTicket = authToken) ) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("from")
        val from: Int = 0,
        @SerializedName("Message")
        val message: String = "",
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("HealthDocuments")
        val healthDocuments: List<HealthDoc> = listOf()
/*        @SerializedName("HealthDocuments")
        val healthDocuments: Array<HealthDoc> = arrayOf()*/
    )

    data class HealthDoc(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("DocumentTypeCode")
        val documentTypeCode: String = "",
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("FileName")
        val fileName: String = "",
        @SerializedName("Title")
        val title: String = "" ,
        @SerializedName("relation")
        val relation: String = "" ,
        @SerializedName("FileBytes")
        val fileBytes: String = "",
        @SerializedName("PersonName")
        val personName: String = "",
        @SerializedName("Type")
        val type: String = "",
        @SerializedName("FilePath")
        var Path: String? = ""

    )

    data class SaveDocumentsResponse(
        @SerializedName("HealthDocuments")
        val healthDocuments: List<HealthDocument> = listOf()
    )

    data class HealthDocResp(
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("DocumentType")
        val documentType: Any? = Any(),
        @SerializedName("DocumentTypeCode")
        val documentTypeCode: String = "",
        @SerializedName("FileBytes")
        val fileBytes: Any? = Any(),
        @SerializedName("FileName")
        val fileName: String = "",
        @SerializedName("FilePath")
        val filePath: Any? = Any(),
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("Keywords")
        val keywords: Any? = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("OwnerCode")
        val ownerCode: Any? = Any(),
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("PersonName")
        val personName: Any? = Any(),
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("Title")
        val title: String = ""
    )
}