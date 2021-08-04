package com.caressa.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.caressa.common.constants.ApiConstants
import com.caressa.common.utils.DateHelper
import com.caressa.local.dao.DataSyncMasterDao
import com.caressa.local.dao.HRADao
import com.caressa.local.dao.TrackParameterDao
import com.caressa.model.entity.*
import com.caressa.model.hra.*
import com.caressa.remote.HraDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import timber.log.Timber
import java.util.*

interface HraRepository {

    suspend fun startHra(
        forceRefresh: Boolean = false,
        data: HraStartModel,
        relativeId: String
    ): LiveData<Resource<HraStartModel.HraStartResponse>>

    suspend fun isBMIExist(
        forceRefresh: Boolean = false,
        data: BMIExistModel
    ): LiveData<Resource<BMIExistModel.BMIExistResponse>>

    suspend fun isBPExist(
        forceRefresh: Boolean = false,
        data: BPExistModel
    ): LiveData<Resource<BPExistModel.BPExistResponse>>

    suspend fun getLabRecords(
        forceRefresh: Boolean = false,
        data: LabRecordsModel
    ): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>>

    suspend fun saveAndSubmitHRA(
        forceRefresh: Boolean = false,
        data: SaveAndSubmitHraModel
    ): LiveData<Resource<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>>

    suspend fun getMedicalProfileSummary(
        forceRefresh: Boolean = false,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>

    suspend fun getAssessmentSummary(
        forceRefresh: Boolean = false,
        data: HraAssessmentSummaryModel
    ): LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>>

    suspend fun getListRecommendedTests(
        forceRefresh: Boolean = false,
        data: HraListRecommendedTestsModel
    ): LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>>

    suspend fun getHRAHistory(data: HraHistoryModel): LiveData<Resource<HraHistoryModel.HRAHistoryResponse>>

    suspend fun saveResponse(response: ArrayList<HRAQuestions>)
    suspend fun saveHRALabValue(hraLabValues: HRALabDetails)
    suspend fun saveVitalDetailsToDb(hraVitalDetails: ArrayList<HRAVitalDetails>)

    suspend fun getLabDetails(): List<HRALabDetails>
    suspend fun getVitalDetails(): List<HRAVitalDetails>
    suspend fun getSavedResponse(questionCode: String): String
    suspend fun getHRASavedDetailsWithQuestionCode(code: String): List<HRAQuestions>
    suspend fun getHRASavedDetailsWithCode(code: String): List<HRAQuestions>
    suspend fun getSavedDetailsTabNameByCategory(tabName: String): List<HRAQuestions>
    suspend fun getHraSummaryDetails(): HRASummary

    suspend fun getParameterDataByProfileCode(profileCode: String): List<TrackParameterMaster.Parameter>

    suspend fun clearQuestionResponse(quesCode: String)
    suspend fun clearHRALabValue(parameterCode: String)
    suspend fun clearHraQuestionsTable()
    suspend fun clearHraDataTables()
    suspend fun logoutUser()
}

class HraRepositoryImpl(
    private val dataSource: HraDatasource,
    private val hraDao: HRADao,
    private val paramDao: TrackParameterDao,
    private val dataSyncMasterDao: DataSyncMasterDao,
    sharedPref: SharedPreferences
) : HraRepository {

    override suspend fun startHra(
        forceRefresh: Boolean,
        data: HraStartModel,
        relativeId: String
    ): LiveData<Resource<HraStartModel.HraStartResponse>> {

        return object :
            NetworkBoundResource<HraStartModel.HraStartResponse, BaseResponse<HraStartModel.HraStartResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HraStartModel.HraStartResponse {
                return HraStartModel.HraStartResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<HraStartModel.HraStartResponse>> {
                return dataSource.getHraStartResponse(data)
            }

            override fun processResponse(response: BaseResponse<HraStartModel.HraStartResponse>): HraStartModel.HraStartResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: HraStartModel.HraStartResponse) {}

            override fun shouldFetch(data: HraStartModel.HraStartResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun isBMIExist(
        forceRefresh: Boolean,
        data: BMIExistModel
    ): LiveData<Resource<BMIExistModel.BMIExistResponse>> {

        return object :
            NetworkBoundResource<BMIExistModel.BMIExistResponse, BaseResponse<BMIExistModel.BMIExistResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): BMIExistModel.BMIExistResponse {
                return BMIExistModel.BMIExistResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<BMIExistModel.BMIExistResponse>> {
                return dataSource.getBMIExistResponse(data)
            }

            override fun processResponse(response: BaseResponse<BMIExistModel.BMIExistResponse>): BMIExistModel.BMIExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: BMIExistModel.BMIExistResponse) {}

            override fun shouldFetch(data: BMIExistModel.BMIExistResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun isBPExist(
        forceRefresh: Boolean,
        data: BPExistModel
    ): LiveData<Resource<BPExistModel.BPExistResponse>> {

        return object :
            NetworkBoundResource<BPExistModel.BPExistResponse, BaseResponse<BPExistModel.BPExistResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): BPExistModel.BPExistResponse {
                return BPExistModel.BPExistResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<BPExistModel.BPExistResponse>> {
                return dataSource.getBPExistResponse(data)
            }

            override fun processResponse(response: BaseResponse<BPExistModel.BPExistResponse>): BPExistModel.BPExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: BPExistModel.BPExistResponse) {}

            override fun shouldFetch(data: BPExistModel.BPExistResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getLabRecords(
        forceRefresh: Boolean,
        data: LabRecordsModel
    ): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> {

        return object :
            NetworkBoundResource<LabRecordsModel.LabRecordsExistResponse, BaseResponse<LabRecordsModel.LabRecordsExistResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): LabRecordsModel.LabRecordsExistResponse {
                return LabRecordsModel.LabRecordsExistResponse()
            }

            override suspend fun saveCallResults(items: LabRecordsModel.LabRecordsExistResponse) {}

            override fun createCallAsync(): Deferred<BaseResponse<LabRecordsModel.LabRecordsExistResponse>> {
                return dataSource.getLabRecordsResponse(data)
            }

            override fun processResponse(response: BaseResponse<LabRecordsModel.LabRecordsExistResponse>): LabRecordsModel.LabRecordsExistResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: LabRecordsModel.LabRecordsExistResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun saveAndSubmitHRA(forceRefresh: Boolean, data: SaveAndSubmitHraModel):
            LiveData<Resource<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>> {

        return object :
            NetworkBoundResource<SaveAndSubmitHraModel.SaveAndSubmitHraResponse, BaseResponse<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveAndSubmitHraModel.SaveAndSubmitHraResponse {
                return SaveAndSubmitHraModel.SaveAndSubmitHraResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>> {
                return dataSource.getSaveAndSubmitHraResponse(data)
            }

            override fun processResponse(response: BaseResponse<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>): SaveAndSubmitHraModel.SaveAndSubmitHraResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SaveAndSubmitHraModel.SaveAndSubmitHraResponse) {}

            override fun shouldFetch(data: SaveAndSubmitHraModel.SaveAndSubmitHraResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getMedicalProfileSummary(
        forceRefresh: Boolean,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ):
            LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {

        return object :
            NetworkBoundResource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse, BaseResponse<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>>() {

            override fun shouldStoreInDb(): Boolean = true

            override fun createCallAsync(): Deferred<BaseResponse<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
                return dataSource.getMedicalProfileSummary(data)
            }

            override fun processResponse(response: BaseResponse<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>): HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse {
                return response.jSONData
            }

            override suspend fun loadFromDb(): HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse {
                val dbData = HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse()
                dbData.MedicalProfileSummary = hraDao.getHRASummary()
                return dbData
            }

            override suspend fun saveCallResults(items: HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse) {
                hraDao.deleteHRASummaryTable()
                hraDao.insertHRASummaryRecord(items.MedicalProfileSummary!!)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.MEDICAL_PROFILE_SUMMERY,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                    personId = personId
                )
//                if(dataSyncMasterDao.getLastSyncDataList().find { it.apiName == ApiConstants.MEDICAL_PROFILE_SUMMERY } == null)
                dataSyncMasterDao.insertApiSyncData(dataSyc)
//                else
//                    dataSyncMasterDao.updateRecord(dataSyc)
            }

            override fun shouldFetch(data: HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getAssessmentSummary(
        forceRefresh: Boolean,
        data: HraAssessmentSummaryModel
    ):
            LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>> {

        return object :
            NetworkBoundResource<HraAssessmentSummaryModel.AssessmentSummaryResponce, BaseResponse<HraAssessmentSummaryModel.AssessmentSummaryResponce>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HraAssessmentSummaryModel.AssessmentSummaryResponce {
                return HraAssessmentSummaryModel.AssessmentSummaryResponce()
            }

            override fun createCallAsync(): Deferred<BaseResponse<HraAssessmentSummaryModel.AssessmentSummaryResponce>> {
                return dataSource.getAssessmentSummary(data)
            }

            override fun processResponse(response: BaseResponse<HraAssessmentSummaryModel.AssessmentSummaryResponce>): HraAssessmentSummaryModel.AssessmentSummaryResponce {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: HraAssessmentSummaryModel.AssessmentSummaryResponce) {}

            override fun shouldFetch(data: HraAssessmentSummaryModel.AssessmentSummaryResponce?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getListRecommendedTests(
        forceRefresh: Boolean,
        data: HraListRecommendedTestsModel
    ):
            LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> {

        return object :
            NetworkBoundResource<HraListRecommendedTestsModel.ListRecommendedTestsResponce, BaseResponse<HraListRecommendedTestsModel.ListRecommendedTestsResponce>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HraListRecommendedTestsModel.ListRecommendedTestsResponce {
                return HraListRecommendedTestsModel.ListRecommendedTestsResponce()
            }

            override fun createCallAsync(): Deferred<BaseResponse<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> {
                return dataSource.getListRecommendedTests(data)
            }

            override fun processResponse(response: BaseResponse<HraListRecommendedTestsModel.ListRecommendedTestsResponce>): HraListRecommendedTestsModel.ListRecommendedTestsResponce {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: HraListRecommendedTestsModel.ListRecommendedTestsResponce) {}

            override fun shouldFetch(data: HraListRecommendedTestsModel.ListRecommendedTestsResponce?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getHRAHistory(data: HraHistoryModel): LiveData<Resource<HraHistoryModel.HRAHistoryResponse>> {
        return object :
            NetworkBoundResource<HraHistoryModel.HRAHistoryResponse, BaseResponse<HraHistoryModel.HRAHistoryResponse>>() {

            @SuppressLint("BinaryOperationInTimber")
            override fun processResponse(response: BaseResponse<HraHistoryModel.HRAHistoryResponse>): HraHistoryModel.HRAHistoryResponse {
                Timber.i("Inside processResponse :: " + response.jSONData)
                return response.jSONData
            }

            override suspend fun saveCallResults(items: HraHistoryModel.HRAHistoryResponse) {}

            override fun shouldFetch(data: HraHistoryModel.HRAHistoryResponse?): Boolean = true

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): HraHistoryModel.HRAHistoryResponse {
                return HraHistoryModel.HRAHistoryResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<HraHistoryModel.HRAHistoryResponse>> {
                return dataSource.getHraHistory(data = data)
            }

        }.build().asLiveData()
    }

    override suspend fun saveResponse(response: ArrayList<HRAQuestions>) {
        for (i in response) {
            hraDao.insertHraQuesResponse(i)
        }
    }

    override suspend fun saveHRALabValue(hraLabValues: HRALabDetails) {
        hraDao.insertHraLabDetails(hraLabValues)
    }

    override suspend fun saveVitalDetailsToDb(hraVitalDetails: ArrayList<HRAVitalDetails>) {
        for (i in hraVitalDetails) {
            hraDao.insertHraVitalDetails(i)
        }
    }

    override suspend fun getLabDetails(): List<HRALabDetails> {
        return hraDao.getHraSaveLabDetails()
    }

    override suspend fun getVitalDetails(): List<HRAVitalDetails> {
        return hraDao.getHraSaveVitalDetails()
    }

    override suspend fun getSavedResponse(questionCode: String): String {
        return hraDao.getHraSaveAnswerCode(questionCode)
    }

    override suspend fun getHRASavedDetailsWithQuestionCode(code: String): List<HRAQuestions> {
        return hraDao.getHRASaveDetailsWhereQuestionCode(code)
    }

    override suspend fun getHRASavedDetailsWithCode(code: String): List<HRAQuestions> {
        return hraDao.getHRASaveDetailsWhereCode(code)
    }

    override suspend fun getSavedDetailsTabNameByCategory(tabName: String): List<HRAQuestions> {
        return hraDao.getHRASaveDetailsTabName(tabName)
    }

    override suspend fun getHraSummaryDetails(): HRASummary {
        return hraDao.getHRASummary()
    }

    override suspend fun getParameterDataByProfileCode(profileCode: String): List<TrackParameterMaster.Parameter> {
        return paramDao.getParameterDataByProfileCode(profileCode)
    }

    override suspend fun clearQuestionResponse(quesCode: String) {
        hraDao.deleteHRASaveWhereQuestionCode(quesCode)
    }

    override suspend fun clearHRALabValue(parameterCode: String) {
        hraDao.deleteHraLabDetailsWhereParameterCode(parameterCode)
    }

    override suspend fun clearHraQuestionsTable() {
        hraDao.deleteHraQuesTable()
    }

    override suspend fun clearHraDataTables() {
        hraDao.deleteHraQuesTable()
        hraDao.deleteHraVitalDetailsTable()
        hraDao.deleteHraLabDetailsTable()
    }

    override suspend fun logoutUser() {
        hraDao.deleteHraQuesTable()
        hraDao.deleteHraVitalDetailsTable()
        hraDao.deleteHraLabDetailsTable()
        hraDao.deleteHRASummaryTable()
    }

}