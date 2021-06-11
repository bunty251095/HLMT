package com.caressa.local.converter

import androidx.room.TypeConverter
import com.caressa.model.entity.MedicationEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MedicationConverter {
    var gson = Gson()

    @TypeConverter
    fun fromMedicationToJson(mSchedule: List<MedicationEntity.MedicationSchedule>): String{
        return gson.toJson(mSchedule)
    }

    @TypeConverter
    fun fromJsonToMedication(json: String) : List<MedicationEntity.MedicationSchedule>{
        val type = object : TypeToken<List<MedicationEntity.MedicationSchedule>>() {}.type
        return gson.fromJson<List<MedicationEntity.MedicationSchedule>>(json,type)
    }

}