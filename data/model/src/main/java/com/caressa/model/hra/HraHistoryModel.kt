package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraHistoryModel (
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
        private val PersonID: String = ""
    )

    data class HRAHistory(
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: String = "",
        @SerializedName("CurrentRiskScore")
        val currentRiskScore: Any = Any(),
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("LastParameterCode")
        val lastParameterCode: Any = Any(),
        @SerializedName("LastSection")
        val lastSection: Any = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: String = "",
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("PreviousRiskScore")
        val previousRiskScore: String = "",
        @SerializedName("PreviousWellnessScore")
        val previousWellnessScore: String = "",
        @SerializedName("StartDate")
        val startDate: String = "",
        @SerializedName("StatusCode")
        val statusCode: String = "",
        @SerializedName("SubmittedDate")
        val submittedDate: String = "",
        @SerializedName("SubmittedWellnessScore")
        val submittedWellnessScore: String = "",
        @SerializedName("TemplateID")
        val templateID: Int = 0
    )

    data class HRAHistoryResponse(@SerializedName("HRAHistory")
                                  val hRAHistory: HRAHistory? = HRAHistory()
    )
}