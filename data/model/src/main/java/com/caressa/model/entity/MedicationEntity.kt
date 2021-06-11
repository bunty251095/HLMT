package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

class MedicationEntity {

    @Entity(tableName = "Medication")
    data class Medication(
        @PrimaryKey
        @SerializedName("ID")
        var medicationId: Int = 0,
        @SerializedName("DrugID")
        var drugID: Int = 0,
        @SerializedName("PrescribedDate")
        var PrescribedDate: String? = "",
        @SerializedName("EndDate")
        var EndDate: String? = "",
        @SerializedName("DrugTypeCode")
        var DrugTypeCode: String? = "",
        @SerializedName("MedicationPeriod")
        var medicationPeriod: String? = "",
        @SerializedName("Drug")
        var drug: Drug = Drug(),
        @SerializedName("Notification")
        var notification: Notification? = Notification(),
        @SerializedName("medicationScheduleList")
        var scheduleList: List<MedicationSchedule> = listOf(),
        @SerializedName("DurationInDays")
        var durationInDays: String? = "",
        @SerializedName("Comments")
        var comments: String? = "",
        @SerializedName("Notes")
        val notes: String? = "",
        @SerializedName("PersonID")
        var personID: String? = "",
        @SerializedName("DosageConsume")
        var dosageConsume: String? = "",
        @SerializedName("DosagePerPeriod")
        var dosagePerPeriod: String? = "",
        @SerializedName("DosageRemaining")
        var dosageRemaining: Double = 0.0,
        @SerializedName("Quantity")
        var quantity: Double = 0.0,
        @SerializedName("Reason")
        var reason: String? = "",
        @SerializedName("Unit")
        var unit: String? = "",
        @SerializedName("InTakeWay")
        var inTakeWay: String? = "",
        @SerializedName("PrescribedBy")
        var prescribedBy: String? = "",
        @SerializedName("CreatedBy")
        var createdBy: String? = "",
        @SerializedName("CreatedDate")
        var createdDate: String? = "",
        @SerializedName("ModifiedBy")
        var modifiedBy: String? = "",
        @SerializedName("ModifiedDate")
        var modifiedDate: String? = ""
    )

    data class MedicationSchedule(
        @SerializedName("ID")
        val scheduleID: Int = 0,
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("ScheduleTime")
        var scheduleTime: String? = "",
        @SerializedName("Dosage")
        val dosage: String = ""
    )

    data class Drug(
        @PrimaryKey
        @SerializedName("ID")
        var id: Int = 0,
        @SerializedName("Name")
        var name: String? = "",
        @SerializedName("Strength")
        var strength: String = "",
        @SerializedName("Company")
        var company: String? = "",
        @SerializedName("Content")
        var content: String? = "",
        @SerializedName("DrugTypeCode")
        var drugTypeCode: String? = ""
    )

    data class Notification(
        @SerializedName("SetAlert")
        var setAlert: Boolean? = false
    )

}