package com.caressa.model.medication

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MedicineListByDayModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("MedicationDate")
        val medicationDate: String = ""
    )

    data class MedicineListByDayResponse(
        @SerializedName("Medications")
        var medications: List<Medication> = listOf()
    )

    data class Medication(
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("DosageConsume")
        val dosageConsume: Any? = Any(),
        @SerializedName("DosagePerPeriod")
        val dosagePerPeriod: String = "",
        @SerializedName("DosageRemaining")
        val dosageRemaining: Double = 0.0,
        @SerializedName("Drug")
        val drug: Drug = Drug(),
        @SerializedName("DrugID")
        val drugID: Int = 0,
        @SerializedName("DrugTypeCode")
        var drugTypeCode: String = "",
        @SerializedName("DurationInDays")
        val durationInDays: Any? = Any(),
        @SerializedName("EndDate")
        val endDate: Any? = Any(),
        @SerializedName("ID")
        val medicationId: Int = 0,
        @SerializedName("InTakeWay")
        val inTakeWay: String = "",
        @SerializedName("MedicationPeriod")
        val medicationPeriod: String = "",
        @SerializedName("medicationScheduleList")
        val medicationScheduleList: List<MedicationSchedule> = listOf(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("Notes")
        val notes: String = "",
        @SerializedName("Notification")
        var notification: Notification? = Notification(),
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("PrescribedBy")
        val prescribedBy: String = "",
        @SerializedName("PrescribedByID")
        val prescribedByID: Any? = Any(),
        @SerializedName("PrescribedDate")
        val prescribedDate: String = "",
        @SerializedName("Quantity")
        val quantity: Double = 0.0,
        @SerializedName("Reason")
        val reason: String = "",
        @SerializedName("Unit")
        val unit: String = ""
    )

    data class Drug(
        @SerializedName("Company")
        val company: String = "",
        @SerializedName("Content")
        val content: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("DrugType")
        val drugType: DrugType = DrugType(),
        @SerializedName("DrugTypeCode")
        val drugTypeCode: String = "",
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("Name")
        val name: String = "",
        @SerializedName("Status")
        val status: Any? = Any(),
        @SerializedName("Strength")
        val strength: String = ""
    )

    data class DrugType(
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Description")
        val description: Any? = Any(),
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any()
    )

    data class MedicationSchedule(
        @SerializedName("MedicationID")
        var medicationID: Int = 0,
        @SerializedName("ID")
        var scheduleId: Int = 0,
        @SerializedName("MedicationInTakeID")
        var medicationInTakeID: Int = 0,
        @SerializedName("ScheduleTime")
        var scheduleTime: String = "",
        @SerializedName("Status")
        var status: String = "",
        @SerializedName("Dosage")
        var dosage: Double = 0.0,
        @SerializedName("CreatedDate")
        var createdDate: String = "",
        @SerializedName("IsFuture")
        var isFuture: Boolean = false,
        @SerializedName("CreatedBy")
        var createdBy: Int = 0,
        @SerializedName("ModifiedBy")
        var modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        var modifiedDate: String = ""
    )

    data class Notification(
        @SerializedName("SetAlert")
        var setAlert: Boolean? = false
    )

}