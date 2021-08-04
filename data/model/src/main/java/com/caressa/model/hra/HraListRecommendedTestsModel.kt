package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraListRecommendedTestsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(
    Header(
        authTicket = authToken
    )
) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        val PersonID: String = ""
    )

    data class ListRecommendedTestsResponce(
        @SerializedName("LabTests")
        val labTests: List<LabTest> = listOf()
    )

    data class LabTest(
        @SerializedName("Key")
        val key: String = "",
        @SerializedName("Tests")
        val tests: List<Test> = listOf()
    )

    data class Test(
        @SerializedName("Frequency")
        @Expose
        val frequency: String = "",
        @SerializedName("FrequencyCode")
        @Expose
        val frequencyCode: Any? = Any(),
        @SerializedName("FrequencyID")
        @Expose
        val frequencyID: Int = 0,
        @SerializedName("FrequencyLevel")
        @Expose
        val frequencyLevel: Int = 0,
        @SerializedName("LabTestCode")
        @Expose
        val labTestCode: String = "",
        @SerializedName("LabTestID")
        @Expose
        val labTestID: Int = 0,
        @SerializedName("LabTestName")
        @Expose
        val labTestName: String = "",
        @SerializedName("ReasonCodes")
        @Expose
        val reasonCodes: List<String> = listOf(),
        @SerializedName("Reasons")
        @Expose
        val reasons: List<String> = listOf()
    )

}