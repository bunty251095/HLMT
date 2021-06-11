package com.caressa.model.hra

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HraStartModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String , private val authToken: String) : BaseRequest(
    Header(
        authTicket = authToken
    )
) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
         val personID: String
    )
    data class HraStartResponse(
        @SerializedName("Template")
        @Expose
        var template: Template = Template()
    )

    data class Template(
        @SerializedName("ID")
        @Expose
         val ID: String? = "",
        @SerializedName("JobProfileCode")
        @Expose
         val JobProfileCode: String? = "",
        @SerializedName("Gender")
        @Expose
         val gender: String? = "",
        @SerializedName("MinAge")
        @Expose
         val minAge: String? = "",
        @SerializedName("MaxAge")
        @Expose
         val maxAge: String? = "",
        @SerializedName("MaxScore")
        @Expose
         val maxScore: String? = "",
        @SerializedName("Sections")
        @Expose
        var sections : List<Section>? = listOf(),
        @SerializedName("HRAHistoryID")
        @Expose
         val hraHistoryID: String? = ""
    )

    data class Section(
        @SerializedName("tabID")
        @Expose
         val tabID: String? = "",
        @SerializedName("Section")
        @Expose
         val section: String? = ""
    )

    /*    data class Sections(
        @Expose
        var sections : List<Section> = listOf()
    )*/

}