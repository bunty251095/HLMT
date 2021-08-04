package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListRelativesModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {
    data class JSONDataRequest(
        @SerializedName("PersonID")
        @Expose
        private val PersonID: String = ""
    )

    data class ListRelativesResponse(
        @SerializedName("Persons")
        val persons: List<Person> = listOf()
    )

    data class Person(
        @SerializedName("AccountID")
        val accountID: Any? = Any(),
        @SerializedName("Address")
        val address: Address = Address(),
        @SerializedName("Age")
        val age: Int = 0,
        @SerializedName("AgeGroup")
        val ageGroup: Any? = Any(),
        @SerializedName("ClusterAssociation")
        val clusterAssociation: List<Any> = listOf(),
        @SerializedName("Contact")
        val contact: Contact = Contact(),
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("DateOfBirth")
        val dateOfBirth: String = "",
        @SerializedName("EnrollmentType")
        val enrollmentType: Any? = Any(),
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("Gender")
        val gender: Int = 0,
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("IsActive")
        val isActive: Boolean = false,
        @SerializedName("JobProfile")
        val jobProfile: Any? = Any(),
        @SerializedName("LastName")
        val lastName: String = "",
        @SerializedName("MaritalStatus")
        val maritalStatus: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("NumberOfKids")
        val numberOfKids: Int = 0,
        @SerializedName("ProfileImageID")
        val profileImageID: Int = 0,
        @SerializedName("Relationships")
        val relationships: List<Relationship> = listOf(),
        @SerializedName("Source")
        val source: Any? = Any(),
        @SerializedName("UploadFileID")
        val uploadFileID: Any? = Any()
    )

    data class Address(
        @SerializedName("AddressLine1")
        val addressLine1: String = "",
        @SerializedName("AddressLine2")
        val addressLine2: Any? = Any(),
        @SerializedName("CityID")
        val cityID: Int = 0,
        @SerializedName("CountryID")
        val countryID: Int = 0,
        @SerializedName("Location")
        val location: String = "",
        @SerializedName("StateID")
        val stateID: Int = 0,
        @SerializedName("ZipCode")
        val zipCode: Any? = Any(),
        @SerializedName("ZoneCode")
        val zoneCode: Any? = Any()
    )

    data class Contact(
        @SerializedName("AlternateContactNo")
        val alternateContactNo: Any? = Any(),
        @SerializedName("AlternateEmailAddress")
        val alternateEmailAddress: Any? = Any(),
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )

    data class Relationship(
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("Relationship")
        val relationship: String = "",
        @SerializedName("RelationshipCode")
        val relationshipCode: String = "",
        @SerializedName("RelativeID")
        val relativeID: Int = 0,
        @SerializedName("Source")
        val source: Any? = Any()
    )
}