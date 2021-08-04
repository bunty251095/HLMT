package com.caressa.model.parameter

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ParameterListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("from")
        @Expose
        val from: String = "60",
        @SerializedName("Message")
        @Expose
        var message: String = ""
    )

    data class Response(
        @SerializedName("Parameters")
        val parameters: List<Parameter> = listOf()
    )

    data class Parameter(
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Comments")
        val comments: String = "",
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("HasUnit")
        val hasUnit: Boolean = true,
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("IsMandatory")
        val isMandatory: Boolean = false,
        @SerializedName("IsRecordExist")
        val isRecordExist: String = "",
        @SerializedName("LabParameterRanges")
        val labParameterRanges: List<LabParameterRange> = listOf(),
        @SerializedName("MaxPermissibleValue")
        val maxPermissibleValue: String = "",
        @SerializedName("MinPermissibleValue")
        val minPermissibleValue: String = "",
        @SerializedName("Mode")
        val mode: String = "",
        @SerializedName("ParameterType")
        val parameterType: String = "",
        @SerializedName("ProfileCode")
        val profileCode: String = "",
        @SerializedName("ProfileName")
        var profileName: String = "",
        @SerializedName("Unit")
        val unit: String = "",

        val iconPosition: Int = 0,
        val status: Boolean = false
    )

    data class LabParameterRange(
        @SerializedName("DisplayRange")
        val displayRange: String,
        @SerializedName("Gender")
        val gender: Int,
        @SerializedName("ID")
        val iD: Int,
        @SerializedName("IsRecordExist")
        val isRecordExist: String,
        @SerializedName("MaxAge")
        val maxAge: String? = "0",
        @SerializedName("MaxValue")
        val maxValue: String? = "0",
        @SerializedName("MinAge")
        val minAge: String? = "0",
        @SerializedName("MinValue")
        val minValue: String? = "0",
        @SerializedName("Observation")
        val observation: String,
        @SerializedName("RangeType")
        val rangeType: String,
        @SerializedName("Unit")
        val unit: String
    )


    data class SelectedParameter(
        val profileCode: String = "",
        val profileName: String = "",
        val iconPosition: Int = 0,
        var selectionStatus: Boolean = false
    )

    data class InputParameterModel(
        var parameterCode: String? = "",
        var parameterType: String? = "",
        var description: String? = "",
        var profileCode: String? = "",
        var profileName: String? = "",
        var parameterUnit: String? = "",
        var minPermissibleValue: String? = "",
        var maxPermissibleValue: String? = "",
        var parameterTextVal: String? = "",
        var parameterVal: String? = ""
    )

//    data class LatestProfileHistory(
//        var
//    )

}