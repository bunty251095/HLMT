package com.caressa.model.fitness

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StepsSaveListModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken.toString())) {

    data class JSONDataRequest(
        @SerializedName("StepsDetails")
        val stepsDetails: List<StepsDetail> = listOf()
    )

    data class StepsDetail(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("RecordDate")
        val recordDate: String = "",
        @SerializedName("Count")
        val count: String = "",
        @SerializedName("Calories")
        val calories: String = "",
        @SerializedName("Distance")
        val distance: String = ""
    )

    data class StepsSaveListResponse(
        @SerializedName("StepsDetails")
        val stepsDetails: String = ""
    )
}