package com.caressa.model.medication

import com.caressa.model.BaseRequest
import com.caressa.model.entity.MedicationEntity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateMedicineModel(@SerializedName("JSONData")
                          @Expose
                          private val jsonData: String,
                          private val authToken: String) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Medication")
        val medication: MedicationModel.Medication = MedicationModel.Medication()
    )

    data class UpdateMedicineResponse(
        @SerializedName("Medication")
        @Expose
        var medication: MedicationEntity.Medication = MedicationEntity.Medication()
    )

/*    data class Medication(
        @SerializedName("Comments")
        var comments: String = "",
        @SerializedName("DosagePerPeriod")
        val dosagePerPeriod: String = "",
        @SerializedName("Drug")
        val drug: Drug = Drug(),
        @SerializedName("DrugID")
        var drugID: Int = 0,
        @SerializedName("DurationInDays")
        var durationInDays: String = "",
        @SerializedName("EndDate")
        val endDate: String = "",
        @SerializedName("ID")
        var iD: Int = 0,
        @SerializedName("MedicationPeriod")
        var medicationPeriod: String = "",
        @SerializedName("medicationScheduleList")
        var medicationScheduleList: List<MedicationSchedule> = listOf(),
        @SerializedName("Notes")
        val notes: String = "",
        @SerializedName("Notification")
        val notification: Notification = Notification(),
        @SerializedName("PersonID")
        var personID: String = "",
        @SerializedName("PrescribedDate")
        var prescribedDate: String = ""
    )

    data class Drug(
        @SerializedName("ID")
        var iD: Int = 0,
        @SerializedName("Name")
        var name: String = "",
        @SerializedName("Company")
        val company: String = "",
        @SerializedName("Content")
        val content: String = "",
        @SerializedName("DrugTypeCode")
        val drugTypeCode: String = ""
    )

    data class MedicationSchedule(
        @SerializedName("MedicationID")
        val medicationID: String = "",
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("ScheduleTime")
        val scheduleTime: String = "",
        @SerializedName("Dosage")
        val dosage: String = ""
    )

    data class Notification(
        @SerializedName("SetAlert")
        val setAlert: String = ""
    )*/

}