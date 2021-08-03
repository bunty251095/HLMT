package com.caressa.model


import com.caressa.model.tempconst.Configuration
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*


open class BaseRequest(
        @SerializedName("Header")
        @Expose
        val header: Header
) {

    data class Header(
        @SerializedName("RequestID")
            @Expose
            private val requestID: String = Configuration.RequestID,
        @SerializedName("DateTime")
            @Expose
            private val dateTime: String = currentDateAsStringddMMMyyyy,
        @SerializedName("ApplicationCode")
            @Expose
            private val applicationCode: String = Configuration.ApplicationCode,
        @SerializedName("AuthTicket")
            @Expose
            private val authTicket: String = "",
        @SerializedName("PartnerCode")
            @Expose
            private val partnerCode: String = Configuration.PartnerCode,
        @SerializedName("EntityType")
            @Expose
            private val entityType: String = Configuration.EntityType,
        @SerializedName("HandShake")
            @Expose
            private val handShake: String = Configuration.Handshake,
        @SerializedName("UTMSource")
        @Expose
        private val utmSource: String = Configuration.UTMSource,
        @SerializedName("UTMMedium")
        @Expose
        private val utmMedium: String = Configuration.UTMMedium
    )

    companion object {
        fun replaceChars(strVal: String): String {
            return strVal.replace("\\u003d", "=")
        }
        /* Current Date : */ val currentDateAsStringddMMMyyyy: String
            get() {
                val calendar = Calendar.getInstance()
                val df = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                return df.format(calendar.time)
            }
    }


}
