package com.caressa.model.medication

import com.google.gson.annotations.SerializedName

class MedicationModel {

    data class Medication(
        @SerializedName("ID")
        var medicationID: Int = 0,
        @SerializedName("PersonID")
        var personID: String = "",
        @SerializedName("DrugID")
        var drugID: Int = 0,
        @SerializedName("DrugTypeCode")
        var drugTypeCode: String = "",
        @SerializedName("Drug")
        var drug: Drug = Drug(),
        @SerializedName("PrescribedDate")
        var prescribedDate: String = "",
        @SerializedName("EndDate")
        var endDate: String = "",
        @SerializedName("MedicationPeriod")
        var medicationPeriod: String = "",
        @SerializedName("DurationInDays")
        var durationInDays: String = "",
        @SerializedName("Notification")
        var notification: Notification = Notification(),
        @SerializedName("Comments")
        var comments: String = "",
        @SerializedName("Notes")
        var notes: String = "",
        @SerializedName("medicationScheduleList")
        var medicationScheduleList: List<MedicationSchedule> = listOf(),
        @SerializedName("PrescribedBy")
        val prescribedBy: String = "",
        @SerializedName("DosagePerPeriod")
        val dosagePerPeriod: String = "",
        @SerializedName("Reason")
        val reason: String = "",
        @SerializedName("DosageRemaining")
        val dosageRemaining: Double = 0.0,
        @SerializedName("Quantity")
        val quantity: Double = 0.0,
        @SerializedName("Unit")
        val unit: String = "",
        @SerializedName("InTakeWay")
        val inTakeWay: String = ""
    )

    data class Drug(
        @SerializedName("ID")
        var drugId: Int = 0,
        @SerializedName("Name")
        var name: String = "",
        @SerializedName("DrugTypeCode")
        var drugTypeCode: String = "",
        @SerializedName("Company")
        val company: String = "",
        @SerializedName("Content")
        val content: String = ""
    )

    data class MedicationSchedule(
        @SerializedName("MedicationID")
        var medicationID: String = "",
        @SerializedName("ID")
        var scheduleId: Int = 0,
        @SerializedName("ScheduleTime")
        var scheduleTime: String = "",
        @SerializedName("Dosage")
        var dosage: String = ""
    )

    data class Notification(
        @SerializedName("SetAlert")
        var setAlert: String = ""
    )

}