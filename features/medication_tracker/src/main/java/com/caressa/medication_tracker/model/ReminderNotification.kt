package com.caressa.medication_tracker.model

import com.google.gson.annotations.SerializedName

data class ReminderNotification(
    @SerializedName("Action")
    var action: String = "",
    @SerializedName("PersonID")
    var personID: String = "",
    @SerializedName("MedicationID")
    var medicationID: String = "",
    @SerializedName("ScheduleID")
    var scheduleID: String = "",
    @SerializedName("Name")
    var medicineName: String = "",
    @SerializedName("Dosage")
    var dosage: String = "",
    @SerializedName("Instruction")
    var instruction: String = "",
    @SerializedName("ScheduleTime")
    var scheduleTime: String = "",
    @SerializedName("NotificationDate")
    var notificationDate: String = ""
)