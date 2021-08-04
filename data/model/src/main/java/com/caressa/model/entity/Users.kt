package com.caressa.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["accountId"])
data class Users(
    @field:SerializedName("AccountID")
    val accountId: Int = 0,
    @field:SerializedName("PersonID")
    val personId: Int = 0,
    @field:SerializedName("FirstName")
    val firstName: String = "",
    @field:SerializedName("LastName")
    val lastName: String = "",
    @field:SerializedName("DateOfBirth")
    val dateOfBirth: String = "",
    @field:SerializedName("Gender")
    val gender: String = "",
    @field:SerializedName("Age")
    val age: Int = 0,
    @field:SerializedName("EmailAddress")
    val emailAddress: String = "",
    @field:SerializedName("PhoneNumber")
    val phoneNumber: String = "",
    @field:SerializedName("PATH")
    val path: String = "",
    @field:SerializedName("Name")
    var name: String = "",
    @field:SerializedName("Context")
    val authToken: String = "",
    @field:SerializedName("PartnerCode")
    val partnerCode: String = "",
    @field:SerializedName("PartnerID")
    val partnerId: Int = 0,
    @field:SerializedName("MaritalStatus")
    val maritalStatus: String = "",
    @field:SerializedName("NumberOfKids")
    val numberOfCode: Int = 0,
    @field:SerializedName("AccountStatus")
    val accountStatus: String = "",
    @field:SerializedName("AccountType")
    val accountType: String = "",
    @field:SerializedName("ClusterAssociationNo")
    val clusterAssociationNo: String = "",
    @field:SerializedName("ClusterID")
    val clusterId: String = "",
    @field:SerializedName("CountryName")
    val countryName: String = "",
    @field:SerializedName("CreatedDate")
    val createdDate: String = "",
    @field:SerializedName("DialingCode")
    val dialingCode: String = "",
    @field:SerializedName("IsActive")
    val isActive: Boolean = false,
    @field:SerializedName("IsAuthenticated")
    val isAuthenticated: Boolean = false,
    @field:SerializedName("PROFILE_IMG_PATH")
    val profileImgPath: String = "",
    @field:SerializedName("RELATIVE_ID")
    val relativeId: String = ""
)