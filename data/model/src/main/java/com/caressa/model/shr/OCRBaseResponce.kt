package com.caressa.model.shr

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OCRBaseResponce<T>(
    @SerializedName("jsonData")
    @Expose
    val jsonData: T
)