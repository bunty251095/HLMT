package com.caressa.model.fitness

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetStepsGoalModel(@SerializedName("JSONData")
                        @Expose
                        private val jsonData: String,
                        private val authToken: String): BaseRequest(Header(authTicket = authToken.toString())) {

    data class JSONDataRequest(@SerializedName("PersonID")
                               val personID: String
    )

    data class Response(
        @SerializedName("LatestGoal")
        val latestGoal: LatestGoal = LatestGoal()
    )

    data class LatestGoal(
        @SerializedName("PersonID")
        var personID: Int = 0,
        @SerializedName("Date")
        var date: String = "",
        @SerializedName("Type")
        var type: String = "",
        @SerializedName("Goal")
        var goal: Int = 0,
        @SerializedName("ID")
        var iD: Int = 0,
        @SerializedName("CreatedBy")
        var createdBy: String = "",
        @SerializedName("CreatedDate")
        var createdDate: String = "",
        @SerializedName("ModifiedBy")
        var modifiedBy: String = "",
        @SerializedName("ModifiedDate")
        var modifiedDate: String = ""
    )
}