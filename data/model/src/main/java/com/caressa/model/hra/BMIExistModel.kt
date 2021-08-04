package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BMIExistModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(
    Header(authTicket = authToken)
) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = ""
    )

    data class BMIExistResponse(
        @SerializedName("BMI")
        @Expose
        var BMI: BMI = BMI()
    )

    data class BMI(
        @SerializedName("Height")
        @Expose
        var Height: String? = "",
        @SerializedName("Weight")
        @Expose
        var Weight: String? = "",
        @SerializedName("Value")
        @Expose
        var Value: String? = "",
        @SerializedName("Observation")
        @Expose
        var Observation: String? = "",
        @SerializedName("Percentile")
        @Expose
        var Percentile: String? = "",
        @SerializedName("RecordDate")
        @Expose
        var RecordDate: String? = "",
        @SerializedName("Comments")
        @Expose
        var Comments: String? = "",
        @SerializedName("OwnerCode")
        @Expose
        var OwnerCode: String? = "",
        @SerializedName("PersonID")
        @Expose
        var PersonID: String? = "",
        @SerializedName("WellnessScore")
        @Expose
        var WellnessScore: String? = "",
        @SerializedName("CreatedBy")
        @Expose
        var CreatedBy: String? = "",
        @SerializedName("ModifiedBy")
        @Expose
        var ModifiedBy: String? = "",
        @SerializedName("ModifiedDate")
        @Expose
        var ModifiedDate: String? = ""
    )
}
