package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "UserRelativesTable")
data class UserRelatives(
    @PrimaryKey
    @SerializedName("RelativeID")
    var relativeID: String = "",
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("DateOfBirth")
    var dateOfBirth: String = "",
    @SerializedName("Age")
    var age: String = "",
    @SerializedName("Gender")
    var gender: String = "",
    @SerializedName("ContactNo")
    var contactNo: String = "",
    @SerializedName("EmailAddress")
    var emailAddress: String = "",
    @SerializedName("RelationshipCode")
    var relationshipCode: String = "",
    @SerializedName("Relationship")
    var relationship: String = "" ,
    @SerializedName("RelationShipID")
    var relationShipID: String = ""
)