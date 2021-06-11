package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity( tableName = "AppCacheMaster")
data class AppCacheMaster (
    @PrimaryKey
    @SerializedName("mapKey")
    val mapKey: String = "",
    @SerializedName("mapValue")
    val mapValue: String? = ""
)