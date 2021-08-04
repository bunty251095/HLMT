package com.caressa.model.medication


import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DrugsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String,
    private val authToken: String
) : BaseRequest(Header(authTicket = authToken.toString())) {


    data class JSONDataRequest(
        @SerializedName("Name")
        @Expose
        private val name: String,
        @SerializedName("OrAndFlag")
        @Expose
        var OrAndFlag: Int = 1,
        @SerializedName("Message")
        @Expose
        var message: String = ""
    )

    data class DrugsResponse(
        @SerializedName("Drugs")
        val drugs: List<Drug> = listOf()
    ) {
        data class Drug(
            @SerializedName("Company")
            val company: String = "",
            @SerializedName("Content")
            val content: String = "",
            @SerializedName("CreatedBy")
            val createdBy: Int = 0,
            @SerializedName("CreatedDate")
            val createdDate: String = "",
            @SerializedName("DrugType")
            val drugType: DrugType = DrugType(),
            @SerializedName("DrugTypeCode")
            val drugTypeCode: String = "",
            @SerializedName("ID")
            var iD: String = "",
            @SerializedName("ModifiedBy")
            val modifiedBy: Int = 0,
            @SerializedName("ModifiedDate")
            val modifiedDate: String = "",
            @SerializedName("Name")
            var name: String = "",
            @SerializedName("Status")
            val status: Any? = Any(),
            @SerializedName("Strength")
            var strength: String = ""
        )

        data class DrugType(
            @SerializedName("Code")
            val code: String = "",
            @SerializedName("CreatedBy")
            val createdBy: Any? = Any(),
            @SerializedName("CreatedDate")
            val createdDate: Any? = Any(),
            @SerializedName("Description")
            val description: Any? = Any(),
            @SerializedName("ID")
            val iD: Int = 0,
            @SerializedName("ModifiedBy")
            val modifiedBy: Any? = Any(),
            @SerializedName("ModifiedDate")
            val modifiedDate: Any? = Any()
        )
    }

}

