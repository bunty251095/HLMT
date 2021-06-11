package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LabRecordsModel(
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
         val PersonID: String = ""
    )

    data class LabRecordsExistResponse(
    @SerializedName("LabRecords")
    @Expose
     val LabRecords: List<LabRecordDetails>? = listOf())

/*    data class LabRecordsExistResponse(
        @SerializedName("LabRecords")
        @Expose
         val LabRecords: LabRecord = LabRecord()
    )

    data class LabRecord(
        @SerializedName("LabRecords")
        @Expose
         val LabRecords: List<LabRecordDetails> = listOf()
    )*/

    data class LabRecordDetails(
        @SerializedName("PersonID")
        @Expose
        var PersonID: String? = "",
        @SerializedName("ProfileCode")
        @Expose
        var ProfileCode: String? = "",
        @SerializedName("ProfileName")
        @Expose
        var ProfileName: String? = "",
        @SerializedName("ParameterCode")
        @Expose
        var ParameterCode: String? = "",
        @SerializedName("Description")
        @Expose
        var Description: String? = "",
        @SerializedName("Value")
        @Expose
        var Value: String? = "",
        @SerializedName("TextValue")
        @Expose
        var TextValue: String? = "",
        @SerializedName("Comments")
        @Expose
        var Comments: String? = "",
        @SerializedName("Unit")
        @Expose
        var Unit: String? = "",
        @SerializedName("Range")
        @Expose
        var Range: String? = "",
        @SerializedName("NormalRange")
        @Expose
        var NormalRange: String? = "",
        @SerializedName("DisplayRange")
        @Expose
        var DisplayRange: String? = "",
        @SerializedName("ParameterID")
        @Expose
        var ParameterID: String? = "",
        @SerializedName("Observation")
        @Expose
        var Observation: String? = "",
        @SerializedName("RecordDate")
        @Expose
        var RecordDate: String? = "",
        @SerializedName("WellnessScore")
        @Expose
        var WellnessScore: WellnessScore = WellnessScore(),
        @SerializedName("OwnerCode")
        @Expose
        var OwnerCode: String? = "",
        @SerializedName("ID")
        @Expose
        var ID: String? = "",
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

    data class WellnessScore(
        @SerializedName("ProfileAttributeCode")
        @Expose
        var ProfileAttributeCode: String? = "" ,
        @SerializedName("ValueCode")
        @Expose
        var ValueCode: String? = "" ,
        @SerializedName("MinValue")
        @Expose
        var MinValue: String? = "" ,
        @SerializedName("MaxValue")
        @Expose
        var MaxValue: String? = "" ,
        @SerializedName("BaseScore")
        @Expose
        var BaseScore: String? = "" ,
        @SerializedName("BaseMaxScore")
        @Expose
        var BaseMaxScore: String? = "" ,
        @SerializedName("Observation")
        @Expose
        var Observation: String? = "" ,
        @SerializedName("Unit")
        @Expose
        var Unit: String? = ""
    )

}