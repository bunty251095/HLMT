package com.caressa.remote

import com.caressa.model.hra.*

class HraDatasource(
    private val defaultService: ApiService,
    private val encryptedService: ApiService
) {

    fun getHraStartResponse(data: HraStartModel) = encryptedService.hraStartAPI(data)

    fun getBMIExistResponse(data: BMIExistModel) = encryptedService.checkBMIExistAPI(data)

    fun getBPExistResponse(data: BPExistModel) = encryptedService.checkBPExistAPI(data)

    fun getLabRecordsResponse(data: LabRecordsModel) = encryptedService.fetchLabRecordsAPI(data)

    fun getSaveAndSubmitHraResponse(data: SaveAndSubmitHraModel) =
        encryptedService.saveAndSubmitHraAPI(data)

    fun getMedicalProfileSummary(data: HraMedicalProfileSummaryModel) =
        encryptedService.getMedicalProfileSummaryAPI(data)

    fun getHraHistory(data: HraHistoryModel) = encryptedService.getHRAHistory(data)

    fun getAssessmentSummary(data: HraAssessmentSummaryModel) =
        encryptedService.getAssessmentSummaryAPI(data)

    fun getListRecommendedTests(data: HraListRecommendedTestsModel) =
        encryptedService.getListRecommendedTestsAPI(data)

}