package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "TrackParameters")
data class TrackParameters(
    @PrimaryKey
    @SerializedName("ProfileCode")
    var ProfileCode: String = "",
    @SerializedName("ProfileName")
    var ProfileName: String? = "",
    @SerializedName("IsSelected")
    var IsSelected: Boolean = false
)