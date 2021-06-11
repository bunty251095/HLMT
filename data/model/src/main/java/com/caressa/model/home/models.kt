package com.caressa.model.home

import com.google.gson.annotations.SerializedName

data class UploadProfileImageResponce(
    @SerializedName("ProfileImageID")
    val profileImageID: String = ""
)

data class Person(
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("AccountID")
    val accountID: Int = 0,
    @SerializedName("Address")
    val address: Address = Address(),
    @SerializedName("Age")
    val age: Int = 0,
    @SerializedName("AgeGroup")
    val ageGroup: String = "",
    @SerializedName("ClusterAssociation")
    val clusterAssociation: List<ClusterAssociation> = listOf(),
    @SerializedName("Contact")
    val contact: Contact = Contact(),
    @SerializedName("CreatedBy")
    val createdBy: Int = 0,
    @SerializedName("CreatedDate")
    val createdDate: String = "",
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
    val modifiedBy: Int = 0,
    @SerializedName("ModifiedDate")
    val modifiedDate: String = "",
    @SerializedName("NumberOfKids")
    val numberOfKids: Int = 0,
    @SerializedName("ProfileImageID")
    val profileImageID: Int = 0,
    @SerializedName("Relationships")
    val relationships: List<Any> = listOf(),
    @SerializedName("Source")
    val source: Any = Any(),
    @SerializedName("UploadFileID")
    val uploadFileID: Any = Any()
)

data class Contact(
    @SerializedName("AlternateContactNo")
    val alternateContactNo: String = "",
    @SerializedName("AlternateEmailAddress")
    val alternateEmailAddress: String = "",
    @SerializedName("EmailAddress")
    val emailAddress: String = "",
    @SerializedName("PrimaryContactNo")
    val primaryContactNo: String = ""
)

data class Address(
    @SerializedName("AddressLine1")
    val addressLine1: String = "",
    @SerializedName("AddressLine2")
    val addressLine2: String = "",
    @SerializedName("CityID")
    val cityID: Int = 0,
    @SerializedName("CountryID")
    val countryID: Int = 0,
    @SerializedName("Location")
    val location: Any = Any(),
    @SerializedName("StateID")
    val stateID: Int = 0,
    @SerializedName("ZipCode")
    val zipCode: String = "",
    @SerializedName("ZoneCode")
    val zoneCode: Any = Any()
)

data class ClusterAssociation(
    @SerializedName("ClusterAssociationNo")
    val clusterAssociationNo: Any = Any(),
    @SerializedName("ClusterCode")
    val clusterCode: String = "",
    @SerializedName("ClusterID")
    val clusterID: Int = 0,
    @SerializedName("ClusterName")
    val clusterName: Any = Any(),
    @SerializedName("CoordinatorGroup")
    val coordinatorGroup: Any = Any(),
    @SerializedName("CreatedBy")
    val createdBy: Int = 0,
    @SerializedName("CreatedDate")
    val createdDate: String = "",
    @SerializedName("Department")
    val department: Any = Any(),
    @SerializedName("DepartmentID")
    val departmentID: Int = 0,
    @SerializedName("EnrollmentType")
    val enrollmentType: Any = Any(),
    @SerializedName("ExpiryDate")
    val expiryDate: String = "",
    @SerializedName("FirstName")
    val firstName: Any = Any(),
    @SerializedName("ID")
    val iD: Int = 0,
    @SerializedName("IsActive")
    val isActive: Boolean = false,
    @SerializedName("Location")
    val location: String = "",
    @SerializedName("LocationID")
    val locationID: Int = 0,
    @SerializedName("MiscellaneousInformation")
    val miscellaneousInformation: Any = Any(),
    @SerializedName("MiscellaneousInformation1")
    val miscellaneousInformation1: Any = Any(),
    @SerializedName("MiscellaneousInformation2")
    val miscellaneousInformation2: Any = Any(),
    @SerializedName("MiscellaneousInformation3")
    val miscellaneousInformation3: Any = Any(),
    @SerializedName("MiscellaneousInformation4")
    val miscellaneousInformation4: Any = Any(),
    @SerializedName("MiscellaneousInformation5")
    val miscellaneousInformation5: Any? = Any(),
    @SerializedName("ModifiedBy")
    val modifiedBy: Int = 0,
    @SerializedName("ModifiedDate")
    val modifiedDate: String = "",
    @SerializedName("OfferLetterNumber")
    val offerLetterNumber: Any = Any(),
    @SerializedName("OrganizationName")
    val organizationName: String = "",
    @SerializedName("Packages")
    val packages: Any = Any(),
    @SerializedName("PartnerCode")
    val partnerCode: String = "",
    @SerializedName("PartnerID")
    val partnerID: Int = 0,
    @SerializedName("PartnerLevel1Code")
    val partnerLevel1Code: String = "",
    @SerializedName("PartnerLevel2Code")
    val partnerLevel2Code: String = "",
    @SerializedName("PartnerRegistrationNo")
    val partnerRegistrationNo: String = "",
    @SerializedName("PersonID")
    val personID: Int = 0,
    @SerializedName("PolicyNumber")
    val policyNumber: Any? = Any(),
    @SerializedName("RegistrationDate")
    val registrationDate: String = "",
    @SerializedName("RelativeClusterAssociationNo")
    val relativeClusterAssociationNo: Any = Any()
)

data class Speciality(
    @SerializedName("ID")
    val id: String = "",
    @SerializedName("SpecailityName")
    val specailityName: String = "",
    @SerializedName("Description")
    val description: String = ""
)

