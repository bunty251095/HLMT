package com.caressa.security.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.hra.HraHistoryModel
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.model.hra.HraStartModel
import com.caressa.repository.HraRepository
import com.caressa.repository.UserRepository
import com.caressa.repository.utils.Resource

class HraManagementUseCase(private val hraRepository: HraRepository){

    suspend fun invokeMedicalProfileSummary(isForceRefresh : Boolean, data: HraMedicalProfileSummaryModel,personId:String): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return Transformations.map(
            hraRepository.getMedicalProfileSummary(isForceRefresh,data,personId)) {
            it
        }
    }

    suspend fun invokeHRAHistory(isForceRefresh : Boolean, data: HraHistoryModel): LiveData<Resource<HraHistoryModel.HRAHistoryResponse>> {
        return Transformations.map(
            hraRepository.getHRAHistory(data)) {
            it
        }
    }

    suspend fun invokeStartHra(isForceRefresh : Boolean, data: HraStartModel,relativeId: String): LiveData<Resource<HraStartModel.HraStartResponse>> {
        return Transformations.map(
            hraRepository.startHra(isForceRefresh,data,relativeId)) {
            it
        }
    }
}