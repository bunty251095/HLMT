package com.caressa.model.medication

import androidx.room.PrimaryKey
import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MedicineInTakeModel(@SerializedName("JSONData")
                          @Expose
                          private val jsonData: String,
                          private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ScheduleID")
        @Expose
        private val scheduleID: Int = 0 ,
        @SerializedName("RecordDate")
        @Expose
        var recordDate: String = ""
    )

    data class MedicineDetailsResponse(
        @SerializedName("MedicationInTakes")
        var medicationInTakes: List<MedicineInTake> = listOf()
    )

    data class MedicineInTake(
        @PrimaryKey
        @SerializedName("MedicationID")
        val medicationID: Int = 0,
        @SerializedName("ID")
        val medicineInTakeId: Int = 0,
        @SerializedName("Status")
        val status: String = "",
        @SerializedName("FeelStatus")
        val feelStatus: String = "",
        @SerializedName("ScheduleID")
        val scheduleID: Int = 0,
        @SerializedName("Dosage")
        val dosage: Double = 0.0,
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("MedDate")
        val medDate: String? = "",
        @SerializedName("MedTime")
        val medTime: String = "",
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int? = 0,
        @SerializedName("CreatedDate")
        val createdDate: String? = "",
        @SerializedName("ModifiedBy")
        val modifiedBy: Int? = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String? = ""
    )

}