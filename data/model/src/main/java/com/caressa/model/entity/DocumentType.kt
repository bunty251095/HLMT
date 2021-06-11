package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DocumentTypeTable")
data class DocumentType(
    @PrimaryKey
    @SerializedName("Code")
    val code: String = "",
    @SerializedName("Description")
    val description: String = "",
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("CreatedBy")
    val createdBy: String? = "",
    @SerializedName("CreatedDate")
    val createdDate: String? = "",
    @SerializedName("ModifiedBy")
    val modifiedBy: String? = "",
    @SerializedName("ModifiedDate")
    val modifiedDate: String? = ""
)