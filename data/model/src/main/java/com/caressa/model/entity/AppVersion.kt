package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "AppUpdateDetailsTable")
data class AppVersion(
    @PrimaryKey
    @SerializedName("ID")
    val id: Int = 0,
    @SerializedName("CurrentVersion")
    val currentVersion: String? = "",
    @SerializedName("ForceUpdate")
    val forceUpdate: Boolean = false,
    @SerializedName("Description")
    val description: String? = "",
    @SerializedName("ImagePath")
    val imagePath: String? = "",
    @SerializedName("APICallInterval")
    val apiCallInterval: Int = 0,
    @SerializedName("LastUpdateDate")
    val lastUpdateDate: String? = currentDateInYYYYMMDD,
    @SerializedName("Application")
    val application: String? = "",
    @SerializedName("DeviceType")
    val deviceType: String? = "",
    @SerializedName("Features")
    val features: String? = "",
    @SerializedName("ReleasedDate")
    val releasedDate: String? = "" ) {

    companion object {
        val currentDateInYYYYMMDD: String
            get() {
                val calendar = Calendar.getInstance()
                val df = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
                return df.format(calendar.time)
            }
    }
}
