package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateRelativeModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    //************************Request************************
    data class JSONDataRequest(
/*        @SerializedName("Message")
        val message: String = "" ,*/
        @SerializedName("PersonID")
        val personID: String = "",
        @SerializedName("Person")
        val person: Person = Person()
    )

    data class Person(
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("LastName")
        val lastName: String = "",
        @SerializedName("DateOfBirth")
        val dateOfBirth: String = "",
        @SerializedName("Gender")
        val gender: String = "",
        @SerializedName("isProfileImageChanges")
        val isProfileImageChanges: String = "",
        @SerializedName("Contact")
        val contact: Contact = Contact()
    )

    data class Contact(
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )

    //************************Responce************************
    data class UpdateRelativeResponse(
        @SerializedName("Person")
        val person: PersonResp = PersonResp()
    )

    data class PersonResp(
        @SerializedName("AccountID")
        val accountID: Any? = Any(),
        @SerializedName("Address")
        val address: Any? = Any(),
        @SerializedName("Age")
        val age: Int = 0,
        @SerializedName("AgeGroup")
        val ageGroup: Any? = Any(),
        @SerializedName("ClusterAssociation")
        val clusterAssociation: Any? = Any(),
        @SerializedName("Contact")
        val contact: ContactResp = ContactResp(),
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
        val id: Int = 0,
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
        val relationships: Any? = Any(),
        @SerializedName("Source")
        val source: Any? = Any(),
        @SerializedName("UploadFileID")
        val uploadFileID: Any? = Any()
    )

    data class ContactResp(
        @SerializedName("AlternateContactNo")
        val alternateContactNo: Any? = Any(),
        @SerializedName("AlternateEmailAddress")
        val alternateEmailAddress: Any? = Any(),
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )
}