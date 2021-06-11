package core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class BaseResponse<T>(
        @SerializedName("Header")
        @Expose
        val header: Header? = null,
        @SerializedName("JSONData")
        @Expose
        val jSONData: T,
        @SerializedName("Data")
        @Expose
        val data: String? = null
) {

    data class Header(
            @SerializedName("RequestID")
            @Expose
            val requestID: String? = "79a8af1c-a8b3-47b6-9a7a-8998c5faba5b",
            @SerializedName("ResponseForRequest")
            @Expose
            val responseForRequest: String? = null,
            @SerializedName("SessionID")
            @Expose
            val sessionID: String? = null,
            @SerializedName("HasErrors")
            @Expose
            val hasErrors: Boolean = false,
            @SerializedName("HasWarnings")
            @Expose
            val hasWarnings: Boolean? = null,
            @SerializedName("ResponseCode")
            @Expose
            val responseCode: Int? = null,
            @SerializedName("Errors")
            @Expose
            val errors: List<Error>? = null,
            @SerializedName("Warnings")
            @Expose
            val warnings: List<Any>? = null
    )

    data class Error(
            @SerializedName("ErrorNumber")
            @Expose
            var errorNumber: Int? = null,
            @SerializedName("Message")
            @Expose
            var message: String? = null
    )

}