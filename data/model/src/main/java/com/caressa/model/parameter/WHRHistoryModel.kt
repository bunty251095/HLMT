package com.caressa.model.parameter


import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WHRHistoryModel(
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
        @SerializedName("WHRHistory")
        val wHRHistory: List<WHRHistoryModel.WHRHistory> = listOf()
    )

    data class WHRHistory(
        @SerializedName("Waist")
        val waist: Double = 0.0,
        @SerializedName("Hip")
        val hip: Double = 0.0,
        @SerializedName("Value")
        val value: String = "",
        @SerializedName("Observation")
        val observation: String = "",
        @SerializedName("RecordDate")
        var recordDate: String = "",
        var recordDateMillisec: String? = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("OwnerCode")
        val ownerCode: Any = Any(),
        @SerializedName("Comments")
        val comments: Any = Any(),
        @SerializedName("MaxWellnessScore")
        val maxWellnessScore: Any = Any(),
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = ""
    )
}