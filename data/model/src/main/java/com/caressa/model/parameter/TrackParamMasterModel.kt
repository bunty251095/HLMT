package com.caressa.model.parameter

import com.caressa.model.entity.TrackParameterMaster
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DashboardData(
    val parameterCode: String?,
    val profileCode: String?,
    val MaxDate: String?,
    val value: String?
)

data class DashboardObservationData(
    val code: String?,
    val profileCode: String?,
    val minAge: String?,
    val maxAge: String?,
    val minValue: String?,
    val maxValue: String?,
    val observation: String?,
    val rangeType: String?,
    val unit: String?,
    val value: String?,
    val MaxDate: String?
)

data class TrackParamDashboardDataSet(
    val observationData: List<DashboardObservationData>?,
    val isStartTracking: Boolean = false,
    val profileName:String?,
    val profileCode: String?
)

data class HistorytableModel(
    var parameterCode: String?,
    var parameterName: String?,
    var parameterUnit: String?,
    var paramDateValueList : List<DateValue> = listOf()
)

data class HistorytableDataModel(
    var parameterCode: String?,
    var parameterName: String?,
    var parameterUnit: String?,
    var paramDateValueList : List<String> = listOf()
)

data class DateValue(
    var date: String?,
    var value: Double?
)

data class MonthYear(
    var month: String = "",
    var year: String = ""
)

data class ParameterProfile(
    var profileCode : String = "",
    var profileName : String = "",
    var isSelection : Boolean = false
)

class ParentProfileModel {
    var profileCode = ""
    var profileName = ""
    var isExpanded = false
    var childParameterList: MutableList<TrackParameterMaster.History> = mutableListOf()
}

data class DashboardParamGridModel(
    var imgId: Int = 0,
    var colorId: Int = 0,
    var parameterName: String = "",
    var parameterValue: String = "",
    var paramCode: String = "")

data class FitnessData(
    @SerializedName("RecordDate")
    var recordDate: String = "",
    @SerializedName("StepsCount")
    var stepsCount: String = "",
    @SerializedName("Calories")
    var calories: String = "",
    @SerializedName("Distance")
    var distance: Double = 0.0,
    @SerializedName("ActiveTime")
    var activeTime: Int = 0
)
