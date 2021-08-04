package com.caressa.model.security

import com.caressa.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhoneExistsModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String
) : BaseRequest(
    Header(
        authTicket = ""
    )
) {

    data class JSONDataRequest(
        @SerializedName("PrimaryPhone")
        @Expose
        private val primaryPhone: String = "",
        @SerializedName("RequestType")
        @Expose
        private val requestType: String = "POST",
        @SerializedName("Message")
        @Expose
        private val message: String = "Verifying mobile number"
    )

    data class IsExistResponse(

        @SerializedName("Account")
        @Expose
        var account: Account? = null,
        @SerializedName("IsExist")
        @Expose
        var isExist: String? = ""

    )

    data class Account(
        @SerializedName("IsExist")
        @Expose
        var isExist: String? = "",
        @SerializedName("AccountID")
        @Expose
        var accountID: String? = "",
        @SerializedName("PrimaryPhone")
        @Expose
        var primaryPhone: String? = "",
        @SerializedName("EmailAddress")
        @Expose
        var emailAddress: String? = "",
        @SerializedName("AccountStatusCode")
        @Expose
        var accountStatusCode: String? = "",
        @SerializedName("AccountTypeCode")
        @Expose
        var accountTypeCode: String? = ""
    )

}