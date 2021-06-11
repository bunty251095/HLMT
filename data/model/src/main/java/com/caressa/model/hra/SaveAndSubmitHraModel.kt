package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveAndSubmitHraModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String , private val authToken: String ) : BaseRequest(Header(authTicket = authToken))   {

    data class JSONDataRequest(
        @SerializedName("HraResponse")
        @Expose
        private val HraResponse: HraResponse = HraResponse(),
        @SerializedName("LabRecords")
        @Expose
        private val LabRecords: List<LabRecord> = listOf(),
        @SerializedName("PersonID")
        @Expose
        private val personID: String = ""
    )

    data class HraResponse(
        @SerializedName("HRARESPONSE")
        @Expose
        private val response: HraResp = HraResp()
    )

    data class HraResp(
        @SerializedName("PERSONID")
        @Expose
        val PERSONID: String = "",
        @SerializedName("SETFAMILYHIST")
        @Expose
        val SETFAMILYHIST: String = "",
        @SerializedName("SETGENHEALTH")
        @Expose
        val SETGENHEALTH: String = "",
        @SerializedName("TEMPLATEID")
        @Expose
        val TEMPLATEID: String = "",
        @SerializedName("VITALS")
        @Expose
        val VITALS: VITALS = VITALS()
    )

    data class LabRecord(
        @SerializedName("ParameterCode")
        @Expose
        val ParameterCode: String = "",
        @SerializedName("PersonID")
        @Expose
        val PersonID: String = "",
        @SerializedName("RecordDate")
        @Expose
        val RecordDate: String = "",
        @SerializedName("Unit")
        @Expose
        val Unit: String = "",
        @SerializedName("Value")
        @Expose
        val Value: String = ""
    )

    data class VITALS(
        @SerializedName("R")
        @Expose
        private val R: String = "SETVITALS",
        @SerializedName("Height")
        @Expose
        private val Height: String = "" ,
        @SerializedName("Weight")
        @Expose
        private val Weight: String = "" ,
        @SerializedName("SaveBMI")
        @Expose
        private val SaveBMI: String = "" ,
        @SerializedName("BMI")
        @Expose
        private val BMI: String = ""  ,
        @SerializedName("Pulse")
        @Expose
        private val Pulse: String = ""  ,
        @SerializedName("SaveBP")
        @Expose
        private val SaveBP: String  = "",
        @SerializedName("SystolicBP")
        @Expose
        private val SystolicBP: String = "" ,
        @SerializedName("Mode")
        @Expose
        private val Mode: String = "" ,
        @SerializedName("DiastolicBP")
        @Expose
        private val DiastolicBP: String = ""
    )

    data class LabDetails(
        @SerializedName("ParameterCode")
        @Expose
        private val ParameterCode : String = ""  ,
        @SerializedName("RecordDate")
        @Expose
        private val RecordDate : String = "" ,
        @SerializedName("Value")
        @Expose
        private val Value : String = "" ,
        @SerializedName("PersonID")
        @Expose
        private val PersonID : String = "" ,
        @SerializedName("Unit")
        @Expose
        private val Unit : String = ""
    )




    data class  SaveAndSubmitHraResponse(
        @SerializedName("WellnessScoreSummary")
        @Expose
        var WellnessScoreSummary: WellnessScoreSummary = WellnessScoreSummary(),
        @SerializedName("HRASummaryReport")
        @Expose
        var HRASummaryReport: HRASummaryReport = HRASummaryReport(),
        @SerializedName("LabTests")
        @Expose
        val LabTests: List<LabTest> = listOf()
    )

    data class WellnessScoreSummary(
        @SerializedName("HRAResponses")
        @Expose
        val HRAResponses: Any? = Any(),
        @SerializedName("HRAStatus")
        @Expose
        val HRAStatus: String = "",
        @SerializedName("Observation")
        @Expose
        val Observation: String = "",
        @SerializedName("StatusCode")
        @Expose
        val StatusCode: String = "",
        @SerializedName("SubmittedDate")
        @Expose
        val SubmittedDate: String = "",
        @SerializedName("WellnessScore")
        @Expose
        val WellnessScore: String = ""
    )

    data class  HRASummaryReport(
        @SerializedName("AssessmentsInProgress")
        @Expose
        val AssessmentsInProgress: List<Any> = listOf(),
        @SerializedName("AssessmentsTaken")
        @Expose
        val AssessmentsTaken: List<Any> = listOf(),
        @SerializedName("OtherAssessments")
        @Expose
        val OtherAssessments: List<Any> = listOf(),
        @SerializedName("SuggestedAssessments")
        @Expose
        val SuggestedAssessments: List<SuggestedAssessment> = listOf()
    )

    data class SuggestedAssessment(
        @SerializedName("AssessmentCode")
        @Expose
        val AssessmentCode: String,
        @SerializedName("AssessmentDescription")
        @Expose
        val AssessmentDescription: String,
        @SerializedName("AssessmentID")
        @Expose
        val AssessmentID: Any,
        @SerializedName("AssessmentStatus")
        @Expose
        val AssessmentStatus: Any,
        @SerializedName("AssessmentStatusCode")
        @Expose
        val AssessmentStatusCode: String,
        @SerializedName("AssessmentType")
        @Expose
        val AssessmentType: String,
        @SerializedName("AssessmentTypeCode")
        @Expose
        val AssessmentTypeCode: String,
        @SerializedName("ClusterID")
        @Expose
        val ClusterID: Any,
        @SerializedName("DateOfBirth")
        @Expose
        val DateOfBirth: String,
        @SerializedName("FirstName")
        @Expose
        val FirstName: String,
        @SerializedName("Gender")
        @Expose
        val Gender: Int,
        @SerializedName("LastName")
        @Expose
        val LastName: String,
        @SerializedName("PartnerID")
        @Expose
        val PartnerID: Any,
        @SerializedName("PersonID")
        @Expose
        val PersonID: Int,
        @SerializedName("RiskCategory")
        @Expose
        val RiskCategory: String,
        @SerializedName("RiskCategoryCode")
        @Expose
        val RiskCategoryCode: String,
        @SerializedName("RiskLevel")
        @Expose
        val RiskLevel: String,
        @SerializedName("RiskScore")
        @Expose
        val RiskScore: String,
        @SerializedName("RiskScorePercent")
        @Expose
        val RiskScorePercent: String
    )

    data class LabTest(
        @SerializedName("Key")
        @Expose
        val Key: String,
        @SerializedName("Tests")
        @Expose
        val Tests: List<Test>
    )

    data class Test(
        @SerializedName("Frequency")
        @Expose
        val Frequency: String,
        @SerializedName("FrequencyCode")
        @Expose
        val FrequencyCode: Any,
        @SerializedName("FrequencyID")
        @Expose
        val FrequencyID: Int,
        @SerializedName("FrequencyLevel")
        @Expose
        val FrequencyLevel: Int,
        @SerializedName("LabTestCode")
        @Expose
        val LabTestCode: String,
        @SerializedName("LabTestID")
        @Expose
        val LabTestID: Int,
        @SerializedName("LabTestName")
        @Expose
        val LabTestName: String,
        @SerializedName("ReasonCodes")
        @Expose
        val ReasonCodes: List<String>,
        @SerializedName("Reasons")
        @Expose
        val Reasons: List<String>
    )

}