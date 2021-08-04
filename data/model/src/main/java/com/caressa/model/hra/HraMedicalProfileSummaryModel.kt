package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.caressa.model.entity.HRASummary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraMedicalProfileSummaryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = "",
        @SerializedName("LastSyncDate")
        @Expose
        private val LastSyncDate: String = "01-JAN-1901"
    )

    data class MedicalProfileSummaryResponse(
        @SerializedName("MedicalProfileSummary")
        @Expose
        var MedicalProfileSummary: HRASummary? = null
    )

}