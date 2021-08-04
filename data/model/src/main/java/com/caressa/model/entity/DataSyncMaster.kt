package com.caressa.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

//@Entity(tableName = "DataSyncMaster" , primaryKeys = ["apiName","personId"] )
@Entity(tableName = "DataSyncMaster", primaryKeys = ["apiName"])
data class DataSyncMaster(
    @SerializedName("ApiName")
    val apiName: String = "",
    @SerializedName("SyncDate")
    val syncDate: String? = "",
    @SerializedName("PersonId")
    val personId: String = "007"
)