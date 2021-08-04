package com.caressa.model.medication

import com.caressa.model.BaseRequest
import com.caressa.model.entity.MedicationEntity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MedicationListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {
    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val personId: String,
        @SerializedName("FromDate")
        @Expose
        private val fromDate: String = "",
        @SerializedName("ToDate")
        @Expose
        private val toDate: String,
        @SerializedName("Message")
        @Expose
        var message: String = "Getting today's medicine...."

    )

    data class Response(
        @SerializedName("Medications")
        @Expose
        var medication: List<MedicationEntity.Medication> = listOf()
    )

    data class Medication(
        @SerializedName("Comments")
        var comments: String = "",
        @SerializedName("CreatedBy")
        var createdBy: String = "",
        @SerializedName("CreatedDate")
        var createdDate: String = "",
        @SerializedName("DosageConsume")
        var dosageConsume: String = "",
        @SerializedName("DosagePerPeriod")
        var dosagePerPeriod: String = "",
        @SerializedName("DosageRemaining")
        var dosageRemaining: Double = 0.0,

        @SerializedName("Drug")
        var drug: DrugsModel.DrugsResponse.Drug = DrugsModel.DrugsResponse.Drug(),
        @SerializedName("DrugID")
        var drugID: Int = 0,
        @SerializedName("DurationInDays")
        var durationInDays: String = "",
        @SerializedName("ID")
        var MedicationID: Int = 0,
        @SerializedName("InTakeWay")
        var inTakeWay: String = "",
        @SerializedName("MedicationPeriod")
        var medicationPeriod: String = "",
        @SerializedName("medicationScheduleList")
        var medicationScheduleList: List<MedicationSchedule> = listOf(),
        @SerializedName("Notification")
        var notification: Notification = Notification(),
        @SerializedName("ModifiedBy")
        var modifiedBy: String = "",
        @SerializedName("ModifiedDate")
        var modifiedDate: String = "",
        @SerializedName("PersonID")
        var personID: String = "",
        @SerializedName("PrescribedBy")
        var prescribedBy: String = "",
        @SerializedName("PrescribedByID")
        var prescribedByID: String = "",
        @SerializedName("PrescribedDate")
        var prescribedDate: String = "",
        @SerializedName("Quantity")
        var quantity: Double = 0.0,
        @SerializedName("Reason")
        var reason: String = "",
        @SerializedName("Unit")
        var unit: String = ""
    )

    data class MedicationSchedule(
        @SerializedName("Dosage")
        val dosage: String = "",
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("ScheduleTime")
        val scheduleTime: String = ""
    )

    data class Notification(
        @SerializedName("SetAlert")
        var setAlert: Boolean = false
    )
}