package com.caressa.local.converter

import androidx.room.TypeConverter
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.MedicationEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    private var gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromMedicationToJson(mSchedule: List<MedicationEntity.MedicationSchedule>): String {
        return gson.toJson(mSchedule)
    }

    @TypeConverter
    fun fromJsonToMedication(json: String): List<MedicationEntity.MedicationSchedule> {
        val type = object : TypeToken<List<MedicationEntity.MedicationSchedule>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromDrugToJson(mSchedule: MedicationEntity.Drug): String {
        return gson.toJson(mSchedule)
    }

    @TypeConverter
    fun fromJsonToDrug(json: String): MedicationEntity.Drug {
        val type = object : TypeToken<MedicationEntity.Drug>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromNotificationToJson(notification: MedicationEntity.Notification?): String? {
        return if (notification == null) {
            ""
        } else {
            gson.toJson(notification)
        }
    }

    @TypeConverter
    fun fromJsonToNotification(json: String?): MedicationEntity.Notification? {
        return if (Utilities.isNullOrEmpty(json)) {
            MedicationEntity.Notification()
        } else {
            val type = object : TypeToken<MedicationEntity.Notification>() {}.type
            gson.fromJson<MedicationEntity.Notification>(json, type)
        }
    }

/*    @TypeConverter
    fun fromNotificationToJson(notification: MedicationEntity.Notification?): String? {
        return if (notification == null) {
            null
        } else {
            gson.toJson(notification)
        }
    }

    @TypeConverter
    fun fromJsonToNotification(json: String?) : MedicationEntity.Notification? {
        return if (json == null) {
            null
        }
        else {
            val type = object : TypeToken<MedicationEntity.Notification>() {}.type
            gson.fromJson<MedicationEntity.Notification>(json,type)
        }
    }*/

/*    @TypeConverter
    fun fromNotificationToJson(notification: MedicationEntity.Notification): String{
        return gson.toJson(notification)
    }

    @TypeConverter
    fun fromJsonToNotification(json: String) : MedicationEntity.Notification {
        val type = object : TypeToken<MedicationEntity.Notification>() {}.type
        return gson.fromJson<MedicationEntity.Notification>(json,type)
    }*/

}