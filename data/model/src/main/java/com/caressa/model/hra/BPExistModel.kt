package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BPExistModel(
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

    data class  BPExistResponse(
        @SerializedName("BloodPressure")
        @Expose
        var bloodPressure: BloodPressure = BloodPressure()
    )

    data class BloodPressure(
        @SerializedName("Systolic")
        @Expose
        var Systolic: String? = "",
        @SerializedName("Diastolic")
        @Expose
        var Diastolic: String? = "",
        @SerializedName("Pulse")
        @Expose
        var Pulse: String? = "",
        @SerializedName("Observation")
        @Expose
        var Observation: String? = "",
        @SerializedName("Comments")
        @Expose
        var Comments: String? = "",
        @SerializedName("RecordDate")
        @Expose
        var RecordDate: String? = "",
        @SerializedName("OwnerCode")
        @Expose
        var OwnerCode: String? = "",
        @SerializedName("PersonID")
        @Expose
        var PersonID: String? = "",
        @SerializedName("WellnessScore")
        @Expose
        var WellnessScore: String? = "",
        @SerializedName("MaxWellnessScore")
        @Expose
        var MaxWellnessScore: String? = "",
        @SerializedName("CreatedBy")
        @Expose
        var CreatedBy: String? = "",
        @SerializedName("CreatedDate")
        @Expose
        var CreatedDate: String? = "",
        @SerializedName("ModifiedBy")
        @Expose
        var ModifiedBy: String? = "",
        @SerializedName("ModifiedDate")
        @Expose
        var ModifiedDate: String? = ""
    )

}