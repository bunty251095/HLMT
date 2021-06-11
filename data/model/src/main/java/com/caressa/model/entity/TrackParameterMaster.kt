package com.caressa.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.caressa.model.parameter.LabRecordsListModel
import com.caressa.model.parameter.ParameterListModel
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

class TrackParameterMaster {

    @Entity(tableName = "TrackParameterMaster")
    data class Parameter(
        @PrimaryKey
        @SerializedName("ID")
        var iD: Int?=0,
        @SerializedName("Code")
        var code: String? = "",
        @SerializedName("Description")
        var description: String? ="",
        @SerializedName("Unit")
        var unit: String? = "",
        @SerializedName("ProfileCode")
        var profileCode: String? = "",
        @SerializedName("ProfileName")
        var profileName: String? = "",
        @SerializedName("MaxPermissibleValue")
        var maxPermissibleValue: String? = "",
        @SerializedName("MinPermissibleValue")
        var minPermissibleValue: String? = "",
        @SerializedName("ParameterType")
        var parameterType: String? = "",
        @SerializedName("HasUnit")
        var hasUnit: Boolean? = false,
        @SerializedName("Mode")
        var mode: String? = "",
        var paramValue: String? = "",
        @SerializedName("Comments")
        var comments: String? ="",
        @SerializedName("IsRecordExist")
        var isRecordExist: String? = "",
        @SerializedName("IsMandatory")
        var isMandatory: Boolean? = false
    )

    @Entity(tableName = "TrackParameterRanges")
    data class TrackParameterRanges(
        @PrimaryKey(autoGenerate = true)
        val iD: Long? = null,
        val paramId: Int,
        @SerializedName("Code")
        val code: String? = "",
        @SerializedName("ProfileCode")
        val profileCode: String?,
        @SerializedName("DisplayRange")
        val displayRange: String?,
        @SerializedName("Gender")
        val gender: Int?,
        @SerializedName("IsRecordExist")
        val isRecordExist: String?,
        @SerializedName("MaxAge")
        val maxAge: String?,
        @SerializedName("MaxValue")
        val maxValue: String?,
        @SerializedName("MinAge")
        val minAge: String?,
        @SerializedName("MinValue")
        val minValue: String?,
        @SerializedName("Observation")
        val observation: String?,
        @SerializedName("RangeType")
        val rangeType: String?,
        @SerializedName("Unit")
        val unit: String?
    )

    @Entity(tableName = "TrackParameterHistory", primaryKeys = ["parameterCode","recordDate"])
    data class History(
        @SerializedName("ParameterID")
        var parameterID: Int? = 0,
        @NotNull
        @SerializedName("ParameterCode")
        var parameterCode: String,
        @SerializedName("Description")
        var description: String? = "",
        @NotNull
        @SerializedName("RecordDate")
        var recordDate: String,
        var recordDateMillisec: String? = "",
        @SerializedName("Value")
        var value: Double? = 0.0,
        @SerializedName("Unit")
        var unit: String? = "",
        @SerializedName("Observation")
        var observation: String? = "",
        @SerializedName("ProfileCode")
        var profileCode: String? = "",
        @SerializedName("ProfileName")
        var profileName: String? = "",
        @SerializedName("PersonID")
        var personID: Int = 0,
        @SerializedName("Range")
        var range: String? = "",
        @SerializedName("NormalRange")
        var normalRange: String? = "",
        @SerializedName("DisplayRange")
        var displayRange: String? = "",
        @SerializedName("Comments")
        var comments: String? = "",
        @SerializedName("TextValue")
        var textValue: String? = "",
        @SerializedName("OwnerCode")
        var ownerCode: String? = "",
        @SerializedName("CreatedBy")
        var createdBy: Int? = 0,
        @SerializedName("CreatedDate")
        var createdDate: String? = "",
        @SerializedName("ModifiedBy")
        var modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        var modifiedDate: String? = "",

        var sync: Boolean? = false
    )

    data class HistoryResponse(@SerializedName("History")
                        val history: List<History> = listOf()
    )


}