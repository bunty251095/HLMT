package com.caressa.model.medication

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddInTakeModel(@SerializedName("JSONData")
                     @Expose
                     private val jsonData: String,
                     private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("MedicationInTake")
        val medicationInTake: List<MedicationInTake> = listOf()
    )

    data class MedicationInTake(
        @SerializedName("MedicationID")
        var medicationID: Int = 0,
        @SerializedName("ID")
        var medicationInTakeID: Int = 0,
        @SerializedName("ScheduleID")
        var scheduleID: Int = 0,
        @SerializedName("Status")
        var status: String = "",
        @SerializedName("FeelStatus")
        var feelStatus: String = "",
        @SerializedName("MedDate")
        var medDate: String = "",
        @SerializedName("MedTime")
        var medTime: String = "",
        @SerializedName("Dosage")
        var dosage: String = "",
        @SerializedName("Description")
        var description: String = "",
        @SerializedName("CreatedDate")
        var createdDate: String = ""
    )

    data class AddInTakeResponse(
        @SerializedName("MedicationInTake")
        val medicationInTake: List<MedicationInTakeResp> = listOf()
    )

    data class MedicationInTakeResp(
        @SerializedName("MedicationID")
        val medicationID: Int = 0,
        @SerializedName("ID")
        val medicationInTakeID: Int = 0,
        @SerializedName("ScheduleID")
        val scheduleID: Int = 0,
        @SerializedName("Status")
        val status: String = "",
        @SerializedName("FeelStatus")
        val feelStatus: String = "",
        @SerializedName("MedDate")
        val medDate: String = "",
        @SerializedName("MedTime")
        val medTime: String = "",
        @SerializedName("Dosage")
        val dosage: Double = 0.0,
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any()
    )

}