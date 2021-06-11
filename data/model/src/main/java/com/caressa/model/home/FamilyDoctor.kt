package com.caressa.model.home


import com.google.gson.annotations.SerializedName

data class FamilyDoctor(
    @SerializedName("ID")
    var id: Int = 0,
    @SerializedName("SpecalityID")
    var specalityID: Int = 0,
    @SerializedName("RegistrationNumber")
    var registrationNumber: Any? = Any(),
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("Specialty")
    var specialty: String = "",
    @SerializedName("EmailAddress")
    var emailAddress: String = "",
    @SerializedName("PrimaryContactNo")
    var primaryContactNo: String = "",
    @SerializedName("AffiliatedTo")
    var affiliatedTo: String = "",
    @SerializedName("AccountID")
    var accountID: Int = 0,
    @SerializedName("CreatedBy")
    var createdBy: Int = 0,
    @SerializedName("CreatedDate")
    var createdDate: Any? = Any(),
    @SerializedName("ModifiedBy")
    var modifiedBy: Int = 0,
    @SerializedName("ModifiedDate")
    var modifiedDate: Any? = Any()
)