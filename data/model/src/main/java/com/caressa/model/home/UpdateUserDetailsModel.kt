package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateUserDetailsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("Person")
        val person: PersonRequest = PersonRequest()
    )

    data class PersonRequest(
        @SerializedName("ID")
        var id: Int = 0,
        @SerializedName("FirstName")
        var firstName: String = "",
        @SerializedName("LastName")
        var lastName: String = "",
        @SerializedName("DateOfBirth")
        var dateOfBirth: String = "",
        @SerializedName("Gender")
        var gender: String = "",
        @SerializedName("Contact")
        var contact: Contact = Contact(),
        @SerializedName("Address")
        var address: Address = Address()
    )

    data class Contact(
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = "",
        @SerializedName("AlternateEmailAddress")
        val alternateEmailAddress: String = "",
        @SerializedName("AlternateContactNo")
        val alternateContactNo: String = ""
    )

    data class Address(
        @SerializedName("AddressLine1")
        val addressLine1: String = ""
    )

    data class UpdateUserDetailsResponse(
        @SerializedName("Person")
        val person: Person = Person()
    )

/*    data class PersonResp(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("AccountID")
        val accountID: Any = Any(),
        @SerializedName("Address")
        val address: AddressResp = AddressResp(),
        @SerializedName("Age")
        val age: Int = 0,
        @SerializedName("AgeGroup")
        val ageGroup: Any = Any(),
        @SerializedName("ClusterAssociation")
        val clusterAssociation: Any = Any(),
        @SerializedName("Contact")
        val contact: ContactResp = ContactResp(),
        @SerializedName("CreatedBy")
        val createdBy: Any = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any = Any(),
        @SerializedName("DateOfBirth")
        val dateOfBirth: String = "",
        @SerializedName("EnrollmentType")
        val enrollmentType: Any = Any(),
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("Gender")
        val gender: Int = 0,
        @SerializedName("IsActive")
        val isActive: Boolean = false,
        @SerializedName("JobProfile")
        val jobProfile: Any = Any(),
        @SerializedName("LastName")
        val lastName: String = "",
        @SerializedName("MaritalStatus")
        val maritalStatus: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Any = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any = Any(),
        @SerializedName("NumberOfKids")
        val numberOfKids: Int = 0,
        @SerializedName("ProfileImageID")
        val profileImageID: Int = 0,
        @SerializedName("Relationships")
        val relationships: Any = Any(),
        @SerializedName("Source")
        val source: Any = Any(),
        @SerializedName("UploadFileID")
        val uploadFileID: Any = Any()
    )

    data class ContactResp(
        @SerializedName("AlternateContactNo")
        val alternateContactNo: String = "",
        @SerializedName("AlternateEmailAddress")
        val alternateEmailAddress: String = "",
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )

    data class AddressResp(
        @SerializedName("AddressLine1")
        val addressLine1: String = "",
        @SerializedName("AddressLine2")
        val addressLine2: Any = Any(),
        @SerializedName("CityID")
        val cityID: Int = 0,
        @SerializedName("CountryID")
        val countryID: Int = 0,
        @SerializedName("Location")
        val location: Any = Any(),
        @SerializedName("StateID")
        val stateID: Int = 0,
        @SerializedName("ZipCode")
        val zipCode: Any = Any(),
        @SerializedName("ZoneCode")
        val zoneCode: Any = Any()
    )*/

}