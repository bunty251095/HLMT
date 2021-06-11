package com.caressa.remote.interceptor

import android.util.Log
import com.caressa.common.constants.Configuration
import com.caressa.common.utils.EncryptionUtility
import com.caressa.model.security.TermsConditionsModel
import com.google.gson.Gson
import core.model.BaseResponse
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset


class DecryptInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.isSuccessful) {
            val newResponse = response.newBuilder()
            val contentType = response.header("Content-Type")
            val source = response.body()?.source()
            source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source?.buffer()
            val responseBodyString = buffer?.clone()?.readString(Charset.forName("UTF-8"))
            //Log.ic_pdf("DecryptInterceptor", "encryptedStream  " + responseBodyString)
            Log.d("DecryptInterceptor", "encryptedStream  " + responseBodyString)
            var decrypted: String? = responseBodyString
            try {
                decrypted = EncryptionUtility.decrypt(Configuration.SecurityKey, responseBodyString!!, Configuration.SecurityKey)
                val baseResponse = Gson().fromJson(decrypted.toString(),BaseResponse::class.java)
                val resultResponse = Gson().fromJson(baseResponse.jSONData.toString(), TermsConditionsModel.TermsConditionsResponse::class.java)
                if(resultResponse.termsConditions == null)
                {
                    decrypted = getJsonString(decrypted)
                }else{
                    val jsonObject:JSONObject = JSONObject()
                    val headerJsonObject:JSONObject = JSONObject(Gson().toJson(baseResponse.header,BaseResponse.Header::class.java))
                    val jsonObjJsonObject:JSONObject = JSONObject(Gson().toJson(resultResponse,
                        TermsConditionsModel.TermsConditionsResponse::class.java))
                    jsonObject.put("Header",headerJsonObject)
                    jsonObject.put("JSONData",jsonObjJsonObject)
                    jsonObject.put("Data","")
                    Log.i("Response===> ",""+jsonObject)
                    decrypted = jsonObject.toString()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            newResponse.body(ResponseBody.create(MediaType.parse(contentType!!), decrypted!!))
            return newResponse.build()
        }
        return response
    }

    private fun getJsonString(decrypted: String?): String? {
       /* val decryptedResponse: String = decrypted!!
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\"{", "{")
            .replace("}\"", "}")
        return decryptedResponse*/
        val decryptedResponse: String = decrypted!!
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse

    }
}