package com.caressa.remote

import com.caressa.model.parameter.*

class ParameterDatasource(
    private val defaultService: ApiService,
    private val encryptedService: ApiService
) {

    fun fetchParamList(data: ParameterListModel) = encryptedService.fetchParamList(data)

    fun fetchLabRecordsList(data: LabRecordsListModel) = encryptedService.getLabRecordList(data)

    fun getParameterPreferences(data: ParameterPreferenceModel) =
        encryptedService.getParameterPreferences(data)

    fun getBMIHistory(data: BMIHistoryModel) = encryptedService.getBMIHistory(data)

    fun getWHRHistory(data: WHRHistoryModel) = encryptedService.getWHRHistory(data)

    fun getBloodPressureHistory(data: BloodPressureHistoryModel) =
        encryptedService.getBloodPressureHistory(data)

    fun addTrackParameter(data: SaveParameterModel) = encryptedService.saveLabRecords(data)
}