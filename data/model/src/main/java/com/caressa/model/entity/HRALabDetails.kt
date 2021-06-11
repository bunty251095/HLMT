package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRASaveLabTable")
data class HRALabDetails(
    @PrimaryKey
    @field:SerializedName("ParameterCode")
    val ParameterCode : String = "" ,
    @field:SerializedName("RecordDate")
    val RecordDate : String? = "" ,
    @field:SerializedName("Value")
    val LabValue : String? = "" ,
    @field:SerializedName("PersonID")
    val PersonID : String? = "" ,
    @field:SerializedName("Unit")
    val Unit : String? = ""
)