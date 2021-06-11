package com.caressa.track_parameter.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.*
import com.caressa.repository.ParameterRepository
import com.caressa.repository.utils.Resource

class ParameterManagementUseCase(private val repository: ParameterRepository){

    suspend fun invokeParamList(isForceRefresh : Boolean = true,data : ParameterListModel): LiveData<Resource<ParameterListModel.Response>> {
        return Transformations.map(
            repository.fetchParamList(isForceRefresh,data)){
            it
        }
    }

    suspend fun invokeLabRecordsList(isForceRefresh : Boolean = true,data : LabRecordsListModel,personId: String): LiveData<Resource<TrackParameterMaster.HistoryResponse>> {
        return Transformations.map(
            repository.fetchLabRecordsList(data,personId)){
            it
        }
    }

    suspend fun invokeGetSelectParamList(selectedParameter: String, showAllProfile:String = "true"): List<ParameterListModel.SelectedParameter>{
        return repository.getSelectParameterList(selectedParameter,showAllProfile)
    }

    suspend fun invokeGetDBParamList(): List<TrackParameterMaster.Parameter>? {
        return repository.getParametersFromDB()
    }

    suspend fun invokeParameterListBaseOnCode(pCode: String): List<TrackParameterMaster.Parameter>? {
        return repository.fetchParamListBaseOnCode(pCode)
    }

    suspend fun invokeParameterHisBaseOnCode(pCode: String, personId: String): List<TrackParameterMaster.History>? {
        return repository.fetchParamHisBaseOnCode(pCode, personId)
    }

//    suspend fun invokeGetDBDashboardList():List<TrackParameterMaster.ParamDashboardData>?{
//        return repository.fetchDashboardListFromDB()
//    }

    suspend fun invokeGetDashboardData():List<DashboardObservationData>?{
        return repository.fetchDashboardDataFromDB()
    }

    suspend fun invokeParameterPreference(data : ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>>{
        return Transformations.map(
            repository.fetchParameterPreferences(data)){
            it
        }
    }

    suspend fun invokeBMIHistory(data : BMIHistoryModel, personId: String): LiveData<Resource<BMIHistoryModel.Response>>{
        return Transformations.map(
            repository.fetchBMIHistory(data,personId)){
            it
        }
    }

    suspend fun invokeWHRHistory(data : WHRHistoryModel, personId: String): LiveData<Resource<WHRHistoryModel.Response>>{
        return Transformations.map(
            repository.fetchWHRHistory(data,personId)){
            it
        }
    }

    suspend fun invokeBloodPressureHistory(data : BloodPressureHistoryModel, personId: String): LiveData<Resource<BloodPressureHistoryModel.Response>>{
        return Transformations.map(
            repository.fetchBloodPressureHistory(data,personId)){
            it
        }
    }

    suspend fun invokeSaveParameter(data : SaveParameterModel): LiveData<Resource<SaveParameterModel.Response>>{
        return Transformations.map(
            repository.saveLabParameter(data)){
            it
        }
    }

    suspend fun invokeLatestParametersHisBaseOnProfileCode(pCode: String, personId: String): List<TrackParameterMaster.History>? {
        return repository.getLatestParameterBasedOnProfileCode(pCode, personId)
    }

    suspend fun invokeLatestRecordHisBaseOnParameterCode(paramCode: String, personId: String): List<TrackParameterMaster.History>? {
        return repository.getLatestRecordBasedOnParamCode(paramCode, personId)
    }

    suspend fun invokeGetParameterObservationList(paramCode: String, personId: String): List<TrackParameterMaster.TrackParameterRanges>? {
        return repository.getParameterObservationList(paramCode,personId)
    }


    suspend fun invokeGetLatestParametersData(profileCode: String, personId: String): List<ParameterListModel.InputParameterModel>?{
        return repository.getLatestParameterData(profileCode,personId)
    }

    suspend fun invokeGetParameterDataBasedOnRecordDate(profileCode: String,personId: String,recordDate:String): List<ParameterListModel.InputParameterModel>?{
        return repository.getParameterDataBasedOnRecordDate(profileCode,personId,recordDate)
    }

    suspend fun invokeGetAllProfileCodes(): List<ParameterProfile> {
        return repository.getAllProfileCodes()
    }

    suspend fun invokeGetAllProfilesWithRecentSelectionList( personId:String ): List<ParameterProfile> {
        return repository.getAllProfilesWithRecentSelectionList(personId)
    }

    suspend fun invokeListHistoryByProfileCodeAndMonthYear(personId: String, profileCode: String, month: String, year: String): List<TrackParameterMaster.History> {
        return repository.listHistoryByProfileCodeAndMonthYear(personId,profileCode,month,year)
    }

    suspend fun invokeListHistoryWithLatestRecord(personId: String,profileCode: String,parameterCode: String): List<TrackParameterMaster.History> {
        return repository.listHistoryWithLatestRecord(personId,profileCode,parameterCode)
    }

}