package com.caressa.model.shr

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OCRUnitExistModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ParameterCode")
        val parameterCode: String = "",
        @SerializedName("Unit")
        val unit: String = ""
    )

    data class OCRUnitExistResponse(
        @SerializedName("IsExist")
        val isExist: Boolean = true
    )
}