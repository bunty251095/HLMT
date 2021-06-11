package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRASaveVitalTable" , primaryKeys = ["VitalsKey"] )
data class HRAVitalDetails(
    @field:SerializedName("VitalsKey")
    val VitalsKey: String = "" ,
    @field:SerializedName("VitalsValue")
    val VitalsValue: String = ""
)