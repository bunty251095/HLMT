package com.caressa.model.parameter


import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BloodPressureHistoryModel(@SerializedName("JSONData")
                                     @Expose
                                     private val jsonData: String,
                                     private val authToken: String): BaseRequest(Header(authTicket = authToken)) {

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
    data class Response( @SerializedName("BloodPressureHistory")
                         val bloodPressureHistory: List<BloodPressureHistory> = listOf()
    )
    data class BloodPressureHistory(
        @SerializedName("Systolic")
        val systolic: Int = 0,
        @SerializedName("Diastolic")
        val diastolic: Int = 0,
        @SerializedName("Pulse")
        val pulse: Int = 0,
        @SerializedName("RecordDate")
        var recordDate: String = "",
        var recordDateMillisec: String? = "",
        @SerializedName("Observation")
        val observation: Any = Any(),
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("MaxWellnessScore")
        val maxWellnessScore: Any = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("OwnerCode")
        val ownerCode: Any = Any(),
        @SerializedName("PersonID")
        val personID: Int = 0
    )
}