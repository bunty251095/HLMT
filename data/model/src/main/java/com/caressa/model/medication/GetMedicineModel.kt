package com.caressa.model.medication

import com.caressa.model.BaseRequest
import com.caressa.model.entity.MedicationEntity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetMedicineModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("Message")
        val message: String = ""
    )

    data class GetMedicineResponse(
        @SerializedName("Medication")
        val medication: MedicationEntity.Medication = MedicationEntity.Medication()
        //val medication: Medication = Medication()

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
        @SerializedName("DurationInDays")
        val durationInDays: Int = 0,
        @SerializedName("ID")
        val iD: Int = 0,
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
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("Dosage")
        val dosage: Double = 0.0,
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("MedicationID")
        val medicationID: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("ScheduleTime")
        val scheduleTime: String = ""
    )
}