package com.caressa.model.shr

import com.google.gson.annotations.SerializedName


data class OCR(
    @SerializedName("jsonData")
    val jsonData: String = ""
)

data class OcrResponce(
    @SerializedName("statusCode")
    val statusCode: String = "",
    @SerializedName("headers")
    val headers: Headers = Headers(),
    @SerializedName("body")
    val body: Body = Body()
)

data class HealthDataParametersList(
    val healthDataParameters: String
)

data class Body(
    @SerializedName("HealthDataParameters")
    val healthDataParameters: List<HealthDataParameter> = listOf(),
    @SerializedName("PatientInfo")
    val patientInfo: List<PatientInfo> = listOf()
)

data class HealthDataParameter(
    @SerializedName("Comments")
    val comments: String = "",
    @SerializedName("MaxPermissibleValue")
    val maxPermissibleValue: String = "",
    @SerializedName("MinPermissibleValue")
    val minPermissibleValue: String = "",
    @SerializedName("Name")
    val name: String = "",
    @SerializedName("NormalRange")
    val normalRange: String = "",
    @SerializedName("Observation")
    var observation: String = "",
    @SerializedName("ParamCode")
    val paramCode: String = "",
    @SerializedName("ProfileCode")
    val profileCode: String = "",
    @SerializedName("Unit")
    var unit: String = "",
    @SerializedName("UnitStatus")
    var unitStatus: Boolean = true
)

data class PatientInfo(
    @SerializedName("Name")
    val name: String = ""
)

data class Headers(
    @SerializedName("Access-Control-Allow-Headers")
    val accessControlAllowHeaders: String = "",
    @SerializedName("Access-Control-Allow-Methods")
    val accessControlAllowMethods: String = "",
    @SerializedName("Access-Control-Allow-Origin")
    val accessControlAllowOrigin: String = "",
    @SerializedName("Content-Type")
    val contentType: String = ""
)
