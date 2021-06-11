package com.caressa.hra.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.*
import com.caressa.model.hra.*
import com.caressa.repository.HomeRepository
import com.caressa.repository.HraRepository
import com.caressa.repository.StoreRecordRepository
import com.caressa.repository.utils.Resource
import java.util.ArrayList


class HraManagementUseCase(private val hraRepository: HraRepository,
                           private val homeRepository: HomeRepository,
                           private val shrRepository: StoreRecordRepository) {

    suspend fun invokeStartHra(isForceRefresh : Boolean, data: HraStartModel,relativeId: String): LiveData<Resource<HraStartModel.HraStartResponse>> {
        return Transformations.map(
            hraRepository.startHra(isForceRefresh,data,relativeId)) {
            it
        }
    }

    suspend fun invokeBMIExist(isForceRefresh : Boolean, data: BMIExistModel): LiveData<Resource<BMIExistModel.BMIExistResponse>> {
        return Transformations.map(
            hraRepository.isBMIExist(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeBPExist(isForceRefresh : Boolean, data: BPExistModel): LiveData<Resource<BPExistModel.BPExistResponse>> {
        return Transformations.map(
            hraRepository.isBPExist(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeLabRecords(isForceRefresh : Boolean, data: LabRecordsModel): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> {
        return Transformations.map(
            hraRepository.getLabRecords(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeSaveAndSubmitHra( isForceRefresh : Boolean ,data: SaveAndSubmitHraModel) : LiveData<Resource<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>> {
        return Transformations.map(
            hraRepository.saveAndSubmitHRA(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeMedicalProfileSummary(isForceRefresh : Boolean, data: HraMedicalProfileSummaryModel,personId:String): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return Transformations.map(
            hraRepository.getMedicalProfileSummary(isForceRefresh,data,personId)) {
            it
        }
    }

    suspend fun invokeGetAssessmentSummary(isForceRefresh : Boolean, data: HraAssessmentSummaryModel): LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>> {
        return Transformations.map(
            hraRepository.getAssessmentSummary(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeGetListRecommendedTests(isForceRefresh : Boolean, data: HraListRecommendedTestsModel): LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> {
        return Transformations.map(
            hraRepository.getListRecommendedTests(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeSaveQuesResponse(savedOptionList : ArrayList<HRAQuestions> ) {
        hraRepository.saveResponse(savedOptionList)
    }

    suspend fun invokeGetSavedResponse(questionCode : String ) : String {
        return  hraRepository.getSavedResponse(questionCode)
    }

    suspend fun invokeSaveVitalDetails( hraVitalDetails : ArrayList<HRAVitalDetails>) {
        hraRepository.saveVitalDetailsToDb( hraVitalDetails )
    }

    suspend fun invokeSaveLabValue(  hraLabValues : HRALabDetails) {
       hraRepository.saveHRALabValue( hraLabValues )
    }

    suspend fun invokeClearLabValue(  parameterCode : String ) {
        hraRepository.clearHRALabValue( parameterCode )
    }

    suspend fun invokeGetHRASavedDetailsWithQuestionCode(questionCode: String) :  List<HRAQuestions> {
        return hraRepository.getHRASavedDetailsWithQuestionCode(questionCode)
    }

    suspend fun invokeGetHRASavedDetailsWithCode(code: String) :  List<HRAQuestions> {
        return hraRepository.getHRASavedDetailsWithCode(code)
    }

    suspend fun invokeGetVitalDetails( ) : List<HRAVitalDetails> {
        return hraRepository.getVitalDetails()
    }

    suspend fun invokeGetLabDetails( ) : List<HRALabDetails> {
        return hraRepository.getLabDetails()
    }

    suspend fun invokeGetHRASaveDetailsTabName( tabName: String) : List<HRAQuestions> {
        return hraRepository.getSavedDetailsTabNameByCategory(tabName)
    }

    suspend fun invokeClearResponse( questionCode : String) {
        hraRepository.clearQuestionResponse( questionCode )
    }

    suspend fun invokeClearHraQuestionsTable( ) {
        hraRepository.clearHraQuestionsTable( )
    }

    suspend fun invokeClearHraDataTables( ) {
        hraRepository.clearHraDataTables( )
    }

    suspend fun invokeClearTablesForSwitchProfile() {
        homeRepository.clearTablesForSwitchProfile()
    }

    suspend fun invokeGetUserRelatives() : List<UserRelatives> {
        return shrRepository.getUserRelatives()
    }

}