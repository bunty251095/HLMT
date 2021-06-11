package com.caressa.medication_tracker

import com.caressa.common.constants.Constants
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MedNotificationApiService {

    @POST(Constants.MEDICATION_GET_API)
    suspend fun getMedicineDetailsApi(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST(Constants.MEDICATION_GET_MEDICINE_IN_TAKE_API)
    suspend fun getMedicationInTakeApi(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST(Constants.MEDICATION_ADD_IN_TAKE_API)
    suspend fun addIntakeApi(@Body requestBody: RequestBody): Response<ResponseBody>

}