package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HealthDocumentTable")
data class HealthDocument(
    @PrimaryKey
    @SerializedName("ID")
    var Id: Int = 0,
    @SerializedName("Title")
    var Title: String? = "",
    @SerializedName("FileName")
    var Name: String? = "",
    @SerializedName("DocumentTypeCode")
    var Code: String? = "",
    @field:SerializedName("Type")
    var Type: String? = "",
    @SerializedName("Comments")
    var Comment: String? = "",
    @SerializedName("PersonID")
    var PersonId: Int? = 0,
    @SerializedName("PersonName")
    var PersonName: String? = "",
    @SerializedName("Relation")
    var Relation: String? = "",
    @SerializedName("FilePath")
    var Path: String? = "",
    @field:SerializedName("Sync")
    var Sync: String? = "N",
    @SerializedName("RecordDate")
    var RecordDate: String? = ""
)