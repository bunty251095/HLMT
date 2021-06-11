package com.caressa.model.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "HRASummaryTable")
data class HRASummary(
    @PrimaryKey
    @SerializedName("Age")
    val age: Int = 0,
    @SerializedName("BMI")
    val bmi: Double = 0.0,
    @SerializedName("BMIObservation")
    val bmiObservation: String = "",
    @SerializedName("BPObservation")
    val bpObservation: String = "",
    @SerializedName("BloodGroup")
    val bloodGroup: String = "",
    @SerializedName("CurrentHRAHistoryID")
    val currentHRAHistoryID: Int = 0,
    @SerializedName("DateOfBirth")
    val dateOfBirth: String = "",
    @SerializedName("Diastolic")
    val diastolic: Double = 0.0,
    @SerializedName("FirstName")
    val firstName: String = "",
    @SerializedName("Gender")
    val gender: String = "",
    @SerializedName("HRACutOff")
    val hraCutOff: String = "",
    @SerializedName("Height")
    val height: Double = 0.0,
    @SerializedName("Hip")
    val hip: Double = 0.0,
    @SerializedName("LastName")
    val lastName: String = "",
    @SerializedName("PersonID")
    val personID: Int = 0,
    @SerializedName("ScorePercentile")
    val scorePercentile: Double = 0.0,
    @SerializedName("Systolic")
    val systolic: Double = 0.0,
    @SerializedName("WHR")
    val wHR: Double = 0.0,
    @SerializedName("WHRObservation")
    val whrObservation: String = "",
    @SerializedName("Waist")
    val waist: Double = 0.0,
    @SerializedName("Weight")
    val weight: Double = 0.0 ,
    @field:SerializedName("LastUpdatedTime")
    val LastUpdatedTime : String = currentUTCDatetimeInMillisecAsString)

/*data class HRASummary(
    @PrimaryKey
    @field:SerializedName("PersonID")
    val PersonID : String,
    @field:SerializedName("Score")
    val Score : String,
    @field:SerializedName("CurrentHRAHistoryID")
    val CurrentHRAHistoryID : String ,
    @field:SerializedName("HRACutOff")
    val HRACutOff : String ,
    @field:SerializedName("Observation")
    val Observation : String ,
    @field:SerializedName("status")
    val status : String ,
    @field:SerializedName("Sync")
    val Sync : String ,
    @field:SerializedName("LastUpdatedTime")
    val LastUpdatedTime : String = currentUTCDatetimeInMillisecAsString) */

{
    companion object {
        val DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss"
        val currentUTCDatetimeInMillisecAsString: String
            get() {
                val sdf = SimpleDateFormat(DATE_FORMAT_UTC)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.format(Date())
            }
    }
}


