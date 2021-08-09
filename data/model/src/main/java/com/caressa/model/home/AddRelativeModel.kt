package com.caressa.model.home

import com.caressa.model.BaseRequest
import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddRelativeModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    //************************Request************************
    data class JSONDataRequest(
        @SerializedName("PersonID")
        val personID: String = "",
/*        @SerializedName("Message")
        val message: String = "",*/
        @SerializedName("Person")
        val person: Person = Person()
    )

    data class Person(
        @SerializedName("RequestType")
        val requestType: String = "POST",
        @SerializedName("FirstName")
        val firstName: String = "",
        @SerializedName("LastName")
        val lastName: String = "",
        @SerializedName("RelativeID")
        val relativeID: String = "",
        @SerializedName("DateOfBirth")
        val dateOfBirth: String = "",
        @SerializedName("Gender")
        val gender: String = "",
        @SerializedName("isProfileImageChanges")
        val isProfileImageChanges: String = "",
        @SerializedName("Contact")
        val contact: Contact = Contact(),
        @SerializedName("ClusterAssociation")
        val clusterAssociation: List<ClusterAssociation> = listOf(ClusterAssociation()),
        @SerializedName("Relationships")
        val relationships: List<Relationship> = listOf()
    )

    data class ClusterAssociation(
        @SerializedName("PartnerCode")
        val partnerCode: String = Configuration.PartnerCode,
        @SerializedName("ClusterID")
        val clusterID: Int = -1
    )

    data class Contact(
        @SerializedName("EmailAddress")
        val emailAddress: String = "",
        @SerializedName("PrimaryContactNo")
        val primaryContactNo: String = ""
    )

    data class Relationship(
        @SerializedName("RelativeID")
        val relativeID: String = "",
        @SerializedName("RelationshipCode")
        val relationshipCode: String = ""
    )

    //************************Responce************************

    data class AddRelativeResponse(
        @SerializedName("Person")
        var person: PersonResp = PersonResp()
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
        val clusterAssociation: List<ClusterAssociationResp> = listOf(),
        @SerializedName("Contact")
        val contact: ContactResp = ContactResp(),
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("DateOfBirth")
        val dateOfBirth: String = "",
        @SerializedName("EnrollmentType")
        val enrollmentType: String = "",
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
        val relationships: List<RelationshipResp> = listOf(),
        @SerializedName("Source")
        val source: Any? = Any(),
        @SerializedName("UploadFileID")
        val uploadFileID: Any? = Any()
    )

    data class ClusterAssociationResp(
        @SerializedName("ClusterAssociationNo")
        val clusterAssociationNo: Any? = Any(),
        @SerializedName("ClusterCode")
        val clusterCode: Any? = Any(),
        @SerializedName("ClusterID")
        val clusterID: Int = 0,
        @SerializedName("ClusterName")
        val clusterName: Any? = Any(),
        @SerializedName("CoordinatorGroup")
        val coordinatorGroup: Any? = Any(),
        @SerializedName("CreatedBy")
        val createdBy: Any? = Any(),
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("Department")
        val department: Any? = Any(),
        @SerializedName("DepartmentID")
        val departmentID: Int = 0,
        @SerializedName("EnrollmentType")
        val enrollmentType: String = "",
        @SerializedName("ExpiryDate")
        val expiryDate: String = "",
        @SerializedName("FirstName")
        val firstName: Any? = Any(),
        @SerializedName("ID")
        val iD: Int = 0,
        @SerializedName("IsActive")
        val isActive: Boolean = false,
        @SerializedName("Location")
        val location: Any? = Any(),
        @SerializedName("LocationID")
        val locationID: Int = 0,
        @SerializedName("MiscellaneousInformation")
        val miscellaneousInformation: Any? = Any(),
        @SerializedName("MiscellaneousInformation1")
        val miscellaneousInformation1: Any? = Any(),
        @SerializedName("MiscellaneousInformation2")
        val miscellaneousInformation2: Any? = Any(),
        @SerializedName("MiscellaneousInformation3")
        val miscellaneousInformation3: Any? = Any(),
        @SerializedName("MiscellaneousInformation4")
        val miscellaneousInformation4: Any? = Any(),
        @SerializedName("MiscellaneousInformation5")
        val miscellaneousInformation5: Any? = Any(),
        @SerializedName("ModifiedBy")
        val modifiedBy: Any? = Any(),
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("OfferLetterNumber")
        val offerLetterNumber: Any? = Any(),
        @SerializedName("OrganizationName")
        val organizationName: Any? = Any(),
        @SerializedName("Packages")
        val packages: Any? = Any(),
        @SerializedName("PartnerCode")
        val partnerCode: String = "",
        @SerializedName("PartnerID")
        val partnerID: Any? = Any(),
        @SerializedName("PartnerLevel1Code")
        val partnerLevel1Code: Any? = Any(),
        @SerializedName("PartnerLevel2Code")
        val partnerLevel2Code: Any? = Any(),
        @SerializedName("PartnerRegistrationNo")
        val partnerRegistrationNo: Any? = Any(),
        @SerializedName("PersonID")
        val personID: Int = 0,
        @SerializedName("PolicyNumber")
        val policyNumber: Any? = Any(),
        @SerializedName("RegistrationDate")
        val registrationDate: String = "",
        @SerializedName("RelativeClusterAssociationNo")
        val relativeClusterAssociationNo: Any? = Any()
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

    data class RelationshipResp(
        @SerializedName("CreatedBy")
        val createdBy: Int = 0,
        @SerializedName("CreatedDate")
        val createdDate: Any? = Any(),
        @SerializedName("ID")
        val id: Int = 0,
        @SerializedName("ModifiedBy")
        val modifiedBy: Int = 0,
        @SerializedName("ModifiedDate")
        val modifiedDate: Any? = Any(),
        @SerializedName("Relationship")
        val relationship: Any? = Any(),
        @SerializedName("RelationshipCode")
        val relationshipCode: String = "",
        @SerializedName("RelativeID")
        val relativeID: Int = 0,
        @SerializedName("Source")
        val source: Any? = Any()
    )

}