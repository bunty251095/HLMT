package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraAssessmentSummaryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(
    Header(
        authTicket = authToken
    )
) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = "",
        @SerializedName("LastSyncDate")
        @Expose
        private val LastSyncDate: String = "01-JAN-1901"
    )

    data class AssessmentSummaryResponce(
        @SerializedName("HRASummaryReport")
        @Expose
        val hraSummaryReport : HRASummaryReport = HRASummaryReport()
    )

    data class HRASummaryReport(
        @SerializedName("AssessmentsInProgress")
        @Expose
        val assessmentsInProgress: List<Any> = listOf(),
        @SerializedName("AssessmentsTaken")
        @Expose
        val assessmentsTaken: List<Any> = listOf(),
        @SerializedName("OtherAssessments")
        @Expose
        val otherAssessments: List<AssessmentDetails> = listOf(),
        @SerializedName("SuggestedAssessments")
        @Expose
        val suggestedAssessments: List<AssessmentDetails> = listOf()
    )

    data class AssessmentDetails(
        @SerializedName("PersonID")
        @Expose
        val personID: String = "",
        @SerializedName("FirstName")
        @Expose
        val firstName: String = "",
        @SerializedName("LastName")
        @Expose
        val lastName: String = "",
        @SerializedName("Gender")
        @Expose
        val gender: String = "",
        @SerializedName("DateOfBirth")
        @Expose
        val dateOfBirth: String = "",
        @SerializedName("ClusterID")
        @Expose
        val clusterID: Any? = Any(),
        @SerializedName("PartnerID")
        @Expose
        val partnerID: Any? = Any(),
        @SerializedName("RiskCategoryCode")
        @Expose
        val riskCategoryCode: String = "",
        @SerializedName("RiskCategory")
        @Expose
        val riskCategory: String = "",
        @SerializedName("AssessmentID")
        @Expose
        val assessmentID: Any? = Any(),
        @SerializedName("AssessmentStatusCode")
        @Expose
        val assessmentStatusCode: String = "",
        @SerializedName("AssessmentStatus")
        @Expose
        val assessmentStatus: Any? = Any(),
        @SerializedName("AssessmentCode")
        @Expose
        val assessmentCode: String = "",
        @SerializedName("AssessmentDescription")
        @Expose
        val assessmentDescription: String = "",
        @SerializedName("AssessmentTypeCode")
        @Expose
        val assessmentTypeCode: String = "",
        @SerializedName("AssessmentType")
        @Expose
        val assessmentType: String = "",
        @SerializedName("RiskScore")
        @Expose
        val riskScore: String = "",
        @SerializedName("RiskLevel")
        @Expose
        val riskLevel: String = "",
        @SerializedName("RiskScorePercent")
        @Expose
        val riskScorePercent: String = ""
    )

}