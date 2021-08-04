package com.caressa.model.fitness

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitnessModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken.toString())) {

    data class JSONDataRequest(
        @SerializedName("Message")
        val message: String,
        @SerializedName("SearchCriteria")
        val searchCriteria: SearchCriteria
    )

    data class SearchCriteria(
        @SerializedName("FromDate")
        val fromDate: String,
        @SerializedName("PersonID")
        val personID: String,
        @SerializedName("ToDate")
        val toDate: String
    )

    class Response {

    }

}