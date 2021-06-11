package com.caressa.tools_calculators.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.model.hra.LabRecordsModel
import com.caressa.model.toolscalculators.*
import com.caressa.repository.HraRepository
import com.caressa.repository.ToolsCalculatorsRepository
import com.caressa.repository.utils.Resource

class ToolsCalculatorsUseCase( private val repository: ToolsCalculatorsRepository,private val hraRepository : HraRepository) {

    suspend fun invokeStartQuiz(isForceRefresh : Boolean, data: StartQuizModel ) : LiveData<Resource<StartQuizModel.StartQuizResponse>> {
        return Transformations.map(
            repository.startQuiz(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeHeartAgeSaveResponce(isForceRefresh : Boolean, data: HeartAgeSaveResponceModel) : LiveData<Resource<HeartAgeSaveResponceModel.HeartAgeSaveResponce>> {
        return Transformations.map(
            repository.heartAgeSaveResponce(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeDiabetesSaveResponce(isForceRefresh : Boolean, data: DiabetesSaveResponceModel) : LiveData<Resource<DiabetesSaveResponceModel.DiabetesSaveResponce>> {
        return Transformations.map(
            repository.diabetesSaveResponce(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeHypertensionSaveResponce(isForceRefresh : Boolean, data: HypertensionSaveResponceModel ) : LiveData<Resource<HypertensionSaveResponceModel.HypertensionSaveResponce>> {
        return Transformations.map(
            repository.hypertensionSaveResponce(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeStressAndAnxietySaveResponce(isForceRefresh : Boolean, data: StressAndAnxietySaveResponceModel ) : LiveData<Resource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>> {
        return Transformations.map(
            repository.stressAndAnxietySaveResponce(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeSmartPhoneSaveResponce(isForceRefresh : Boolean, data: SmartPhoneSaveResponceModel) : LiveData<Resource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>> {
        return Transformations.map(
            repository.smartPhoneSaveResponce(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeMedicalProfileSummary(isForceRefresh : Boolean, data: HraMedicalProfileSummaryModel,personId:String): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return Transformations.map(
            hraRepository.getMedicalProfileSummary(isForceRefresh,data,personId)) {
            it
        }
    }

    suspend fun invokeLabRecords(isForceRefresh : Boolean, data: LabRecordsModel): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> {
        return Transformations.map(
            hraRepository.getLabRecords(isForceRefresh,data)) {
            it
        }
    }

}