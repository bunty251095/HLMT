package com.caressa.model.parameter

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LabRecordsListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        val personID: String = "0",
        @SerializedName("LastSyncDate")
        @Expose
        var lastSyncDate: String = "01-JAN-1901",
        @SerializedName("NumberOfReadings")
        @Expose
        var numberOfReadings: String = "5"
    )

    data class Response(
        @SerializedName("History")
        val history: List<History> = listOf()
    )

    data class History(
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("Description")
        val description: String = "",
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
        val ownerCode: String = "",
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("ParameterID")
        val parameterID: Int = 0,
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("ProfileCode")
        val profileCode: String = "",
        @SerializedName("ProfileName")
        val profileName: String = "",
        @SerializedName("Range")
        val range: String = "",
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("TextValue")
        val textValue: String = "",
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("Value")
        val value: Int = 0,
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

}