package com.caressa.model.parameter


import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveParameterModel(@SerializedName("JSONData")
                              @Expose
                              private val jsonData: String,
                              private val authToken: String): BaseRequest(Header(authTicket = authToken)){

    data class JSONDataRequest(
        @SerializedName("Records")
        val records: List<Record> = listOf()
    )

    data class Response(
        @SerializedName("Records")
        val records: List<ResponseRecord> = listOf()
    )

    data class ResponseRecord(
        @SerializedName("Comments")
        val comments: Any = Any(),
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("Description")
        val description: Any = Any(),
        @SerializedName("DisplayRange")
        val displayRange: String = "",
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("NormalRange")
        val normalRange: String = "",
        @SerializedName("Observation")
        val observation: String = "",
        @SerializedName("OwnerCode")
        val ownerCode: Any = Any(),
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("ParameterID")
        val parameterID: Int = 0,
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("ProfileCode")
        val profileCode: String = "",
        @SerializedName("ProfileName")
        val profileName: Any = Any(),
        @SerializedName("Range")
        val range: String = "",
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("TextValue")
        val textValue: String = "",
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("Value")
        val value: Double = 0.0,
        @SerializedName("WellnessScore")
        val wellnessScore: WellnessScore = WellnessScore()
    )

    data class WellnessScore(
        @SerializedName("BaseMaxScore")
        val baseMaxScore: String = "",
        @SerializedName("BaseScore")
        val baseScore: String = "",
        @SerializedName("MaxValue")
        val maxValue: String = "",
        @SerializedName("MinValue")
        val minValue: String = "",
        @SerializedName("Observation")
        val observation: String = "",
        @SerializedName("ProfileAttributeCode")
        val profileAttributeCode: String = "",
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("ValueCode")
        val valueCode: String = ""
    )

    data class Record(
        @SerializedName("CreatedBy")
        val createdBy: String = "",
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: String = "",
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("ProfileCode")
        val profileCode: String = "",
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("Value")
        val value: String = "",
        @SerializedName("TextValue")
        var textValue: String = ""
    )
}

