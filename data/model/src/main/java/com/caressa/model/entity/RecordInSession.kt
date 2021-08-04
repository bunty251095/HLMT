package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "RecordsInSessionTable")
data class RecordInSession(
    @SerializedName("Id")
    val Id: String = "",
    @PrimaryKey
    @SerializedName("Name")
    val Name: String = "",
    @SerializedName("OriginalFileName")
    val OriginalFileName: String = "",
    @SerializedName("Path")
    val Path: String = "",
    @SerializedName("Type")
    val Type: String = "",
    @SerializedName("Sync")
    val Sync: String = "",
    @SerializedName("LastUpdatedTime")
    val LastUpdatedTime: String = ""
)