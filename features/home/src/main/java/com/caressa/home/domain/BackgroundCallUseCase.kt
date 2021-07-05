package com.caressa.home.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.*
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.StepsHistoryModel
import com.caressa.model.home.CheckAppUpdateModel
import com.caressa.model.home.SaveCloudMessagingIdModel
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.model.medication.MedicationListModel
import com.caressa.model.parameter.*
import com.caressa.model.shr.ListDocumentTypesModel
import com.caressa.model.shr.ListRelativesModel
import com.caressa.repository.*
import com.caressa.repository.utils.Resource

class BackgroundCallUseCase(private val homeRepository: HomeRepository, private val hraRepository : HraRepository,
                            private val shrRepository : StoreRecordRepository, private val trackParamRepo : ParameterRepository,
                            private val medicationRepository: MedicationRepository, private val fitnessRepository: FitnessRepository) {

    suspend fun invokeGetAppVersionDetails( ) : AppVersion {
        return homeRepository.getAppVersionDetails()
    }

    suspend fun invokeSaveCloudMessagingId(isForceRefresh : Boolean, data: SaveCloudMessagingIdModel): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {
        return Transformations.map(
            homeRepository.saveCloudMessagingId(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeCheckAppUpdate(isForceRefresh : Boolean, data: CheckAppUpdateModel): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {
        return Transformations.map(
            homeRepository.checkAppUpdate(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeDocumentType(isForceRefresh : Boolean, data: ListDocumentTypesModel): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> {
        return Transformations.map(
            shrRepository.fetchDocumentType(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeRelativesList(isForceRefresh : Boolean, data: ListRelativesModel): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {
        return Transformations.map(
            homeRepository.fetchRelativesList(isForceRefresh, data)) {
            it
        }
    }

    suspend fun invokeMedicalProfileSummary(isForceRefresh : Boolean, data: HraMedicalProfileSummaryModel,personId:String): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return Transformations.map(
            hraRepository.getMedicalProfileSummary(isForceRefresh,data,personId)) {
            it
        }
    }

    suspend fun invokeParamList(isForceRefresh : Boolean = true,data : ParameterListModel): LiveData<Resource<ParameterListModel.Response>> {
        return Transformations.map(
            trackParamRepo.fetchParamList(isForceRefresh,data)){
            it
        }
    }

    suspend fun invokeBMIHistory(data : BMIHistoryModel, personId: String): LiveData<Resource<BMIHistoryModel.Response>>{
        return Transformations.map(
            trackParamRepo.fetchBMIHistory(data,personId)){
            it
        }
    }

    suspend fun invokeWHRHistory(data : WHRHistoryModel, personId: String): LiveData<Resource<WHRHistoryModel.Response>>{
        return Transformations.map(
            trackParamRepo.fetchWHRHistory(data,personId)){
            it
        }
    }

    suspend fun invokeBloodPressureHistory(data : BloodPressureHistoryModel, personId: String): LiveData<Resource<BloodPressureHistoryModel.Response>>{
        return Transformations.map(
            trackParamRepo.fetchBloodPressureHistory(data,personId)){
            it
        }
    }

    suspend fun invokeLabRecordsList(isForceRefresh : Boolean = true,data : LabRecordsListModel, personId: String): LiveData<Resource<TrackParameterMaster.HistoryResponse>> {
        return Transformations.map(
            trackParamRepo.fetchLabRecordsList(data,personId)){
            it
        }
    }

    suspend fun invokeFetchStepsGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>> {
        return Transformations.map(
            fitnessRepository.fetchLatestGoal(data = data)) {
            it
        }
    }

    suspend fun fetchMedicationList(
        data: MedicationListModel,
        personId: String
    ): LiveData<Resource<MedicationListModel.Response>>{
        return Transformations.map(
            medicationRepository.fetchMedicationList(data = data,personId = personId)){
            it
        }
    }

    suspend fun invokeParameterPreference(data : ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>>{
        return Transformations.map(
            trackParamRepo.fetchParameterPreferences(data)){
            it
        }
    }

    suspend fun invokeGetSyncMasterData(personId: String):List<DataSyncMaster>{
        return homeRepository.getSyncMasterData(personId)
    }

    suspend fun addDataSyncDetails(data : DataSyncMaster){
        return homeRepository.saveSyncDetails(data)
    }

    suspend fun invokeStepsHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return Transformations.map(
            fitnessRepository.fetchStepsListHistory(data = data)) {
            it
        }
    }

    suspend fun invokeGetHraSummaryDetails( ) : HRASummary {
        return hraRepository.getHraSummaryDetails()
    }

    suspend fun invokeDeleteHistoryWithOtherPersonId(personId:String) {
        trackParamRepo.deleteHistoryWithOtherPersonId(personId)
    }

    suspend fun getVitalsData(profileCode: String,profileCodeTwo: String, personId: String): List<TrackParameterMaster.History>? {
        return trackParamRepo.getLatestParameterBasedOnProfileCodes(profileCode,profileCodeTwo, personId)
    }

    suspend fun invokeLogout() {
        trackParamRepo.logoutUser()
        hraRepository.logoutUser()
        medicationRepository.logoutUser()
        shrRepository.logoutUser()
        homeRepository.logoutUser()
        fitnessRepository.logoutUser()
    }
}