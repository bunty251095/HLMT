package com.caressa.hra

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.Url
import retrofit2.http.GET
import retrofit2.http.Streaming

interface DownloadReportApiService {
    @GET
    fun downloadHraReport(@Url fileUrl: String): Call<ResponseBody>
}