package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FamilyDoctorUpdateModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String ,  private val authToken: String) : BaseRequest(Header(authTicket = authToken))
{

    data class JSONDataRequest(
        @SerializedName("FamilyDoctor")
        val familyDoctor: FamilyDoctorUpdateRequest = FamilyDoctorUpdateRequest()
    )

    data class FamilyDoctorUpdateRequest(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("AccountID")
        val accountID: Int = 0,
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("LastName")
        val lastName: String = "",
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = "",
        @SerializedName("Specialty")
        val specialty: String = "",
        @SerializedName("AffiliatedTo")
        val affiliatedTo: String = ""
    )

    data class FamilyDoctorUpdateResponse(
        @SerializedName("FamilyDoctor")
        val familyDoctor: FamilyDoctor = FamilyDoctor()
    )

}