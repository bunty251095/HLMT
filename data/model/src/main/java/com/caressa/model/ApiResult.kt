package com.caressa.model

import com.google.gson.annotations.SerializedName

data class ApiResult<T>(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("items") val items: List<T>
)

data class ApiResponse<T>(
    @SerializedName("JObject") val data: T,
    @SerializedName("StatusCode") val statusCode: String = "0",
    @SerializedName("ErrorNumber") val errorNumber: String = "0",
    @SerializedName("Message") val message: String
)