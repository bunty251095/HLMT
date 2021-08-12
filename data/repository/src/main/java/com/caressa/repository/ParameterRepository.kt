package com.caressa.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.caressa.common.constants.ApiConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.local.dao.DataSyncMasterDao
import com.caressa.local.dao.TrackParameterDao
import com.caressa.model.entity.DataSyncMaster
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.entity.TrackParameters
import com.caressa.model.parameter.*
import com.caressa.remote.ParameterDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.Resource
import com.caressa.repository.utils.StoreNetworkDataBoundResource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import timber.log.Timber

interface ParameterRepository {

    suspend fun fetchBMIHistory(
        data: BMIHistoryModel,
        personId: String
    ): LiveData<Resource<BMIHistoryModel.Response>>

    suspend fun fetchWHRHistory(
        data: WHRHistoryModel,
        personId: String
    ): LiveData<Resource<WHRHistoryModel.Response>>

    suspend fun fetchBloodPressureHistory(
        data: BloodPressureHistoryModel,
        personId: String
    ): LiveData<Resource<BloodPressureHistoryModel.Response>>

    suspend fun fetchLabRecordsList(
        data: LabRecordsListModel,
        personId: String
    ): LiveData<Resource<TrackParameterMaster.HistoryResponse>>

    suspend fun fetchParamList(
        forceRefresh: Boolean = true,
        data: ParameterListModel
    ): LiveData<Resource<ParameterListModel.Response>>

    suspend fun fetchParameterPreferences(data: ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>>
    suspend fun saveLabParameter(data: SaveParameterModel): LiveData<Resource<SaveParameterModel.Response>>

    suspend fun getAllProfileCodes(): List<ParameterProfile>
    suspend fun getAllProfilesWithRecentSelectionList(personId: String): List<ParameterProfile>
    suspend fun listHistoryByProfileCodeAndMonthYear(
        personId: String,
        profileCode: String,
        month: String,
        year: String
    ): List<TrackParameterMaster.History>

    suspend fun listHistoryWithLatestRecord(
        personId: String,
        profileCode: String,
        parameterCode: String
    ): List<TrackParameterMaster.History>

    suspend fun getHistoryTableDataList(
        profileCode: String,
        personId: String
    ): List<HistorytableModel>

    suspend fun getHistoryTableListData(
        profileCode: String,
        personId: String
    ): List<HistorytableDataModel>

    suspend fun getRecentDateList(profileCode: String, personId: String): List<String>
    suspend fun getParametersFromDB(): List<TrackParameterMaster.Parameter>
    suspend fun getTrackParametersList(): List<TrackParameters>
    suspend fun getSelectParameterList(
        selectedParam: String,
        showAllProfile: String
    ): List<ParameterListModel.SelectedParameter>

    suspend fun insertSelectedParameter(trackParameter: TrackParameters)
    suspend fun fetchDashboardDataFromDB(): List<DashboardObservationData>
    suspend fun fetchParamListBaseOnCode(pCode: String): List<TrackParameterMaster.Parameter>
    suspend fun fetchParamHisBaseOnCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    suspend fun getLatestParameterBasedOnProfileCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    suspend fun getLatestParameterBasedOnProfileCodes(
        pCode: String,
        pCodeTwo: String,
        personId: String
    ): List<TrackParameterMaster.History>

    suspend fun getLatestRecordBasedOnParamCode(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    suspend fun deleteSelectedParameters(profileCode: String)
    suspend fun deleteHistoryWithOtherPersonId(personId: String)
    suspend fun getParameterObservationList(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.TrackParameterRanges>

    suspend fun getLatestParameterData(
        profileCode: String,
        personId: String
    ): List<ParameterListModel.InputParameterModel>

    suspend fun getParameterDataBasedOnRecordDate(
        profileCode: String,
        personId: String,
        recordDate: String
    ): List<ParameterListModel.InputParameterModel>

    suspend fun logoutUser()
}

class ParameterRepositoryImpl(
    private val dataSource: ParameterDatasource,
    private val paramDao: TrackParameterDao,
    private val dataSyncMasterDao: DataSyncMasterDao,
    private val context: Context
) : ParameterRepository {

    override suspend fun getRecentDateList(profileCode: String, personId: String): List<String> {
        val recentDatesList: MutableList<String> = mutableListOf()
        var recentDate = ""
        val allList = paramDao.getParameterHisBaseOnProfileCode(profileCode, personId)
        for (i in allList) {
            val date = DateHelper.getDateTimeAs_ddMMMyyyy_FromUtc(i.recordDate)
            if (!recentDate.equals(date, ignoreCase = true)) {
                recentDate = date
                recentDatesList.add(recentDate)
            }
            if (recentDatesList.size == 3)
                break
        }
        Timber.i("recentDatesList=> ${recentDatesList.size}")
        return recentDatesList.reversed()
    }

    override suspend fun getHistoryTableListData(
        profileCode: String,
        personId: String
    ): List<HistorytableDataModel> {
        val recent3DatesList: MutableList<String> = mutableListOf()
        var recentDate = ""
        val allList = paramDao.getParameterHisBaseOnProfileCode(profileCode, personId)
        //Timber.i("allList:" +allList)
        val paramList = paramDao.getParameterListBaseOnCode(profileCode)
        val historyList: MutableList<HistorytableDataModel> = mutableListOf()
        for (i in allList) {
            val date = DateHelper.getDateTimeAs_ddMMMyyyy_FromUtc(i.recordDate)
            if (!recentDate.equals(date, ignoreCase = true)) {
                recentDate = date
                recent3DatesList.add(recentDate)
            }
            if (recent3DatesList.size == 3)
                break
        }
        val list: MutableList<HistorytableDataModel> = mutableListOf()
        if (recent3DatesList.size > 0) {
            for (param in paramList) {
                val subList = paramDao.getParameterBasedOnProfileCodeAndDate(
                    param.code!!,
                    personId,
                    recent3DatesList.size
                )
                val valueList: MutableList<String> = mutableListOf()
                for (i in 0 until recent3DatesList.size) {
                    if (subList.isNotEmpty() && i < subList.size && i < recent3DatesList.size) {
                        val date = DateHelper.getDateTimeAs_ddMMMyyyy_FromUtc(subList[i].recordDate)
                        if (date.equals(recent3DatesList[i], ignoreCase = true)) {
                            valueList.add(subList[i].value.toString())
                        }
                    }
/*                    else {
                        valueList.add("--")
                    }*/
                }
                list.add(
                    HistorytableDataModel(
                        param.code,
                        param.description,
                        param.unit,
                        valueList.reversed()
                    )
                )
            }
        }
        historyList.clear()
        historyList.addAll(list)
        return historyList
    }

    override suspend fun getHistoryTableDataList(
        profileCode: String,
        personId: String
    ): List<HistorytableModel> {

        val paramList = paramDao.getParameterListBaseOnCode(profileCode)
        val historyList: MutableList<HistorytableModel> = mutableListOf()

        for (param in paramList) {
            val subList = paramDao.getParameterHisBaseOnCode(param.code!!, personId)
            val dateValueList: MutableList<DateValue> = mutableListOf()
            //Timber.i("SubList:" +subList)
            for (i in subList) {
                dateValueList.add(DateValue(i.recordDate, i.value))
            }
            dateValueList.reverse()
            historyList.add(
                HistorytableModel(
                    param.code,
                    param.description,
                    param.unit,
                    dateValueList
                )
            )
        }
        return historyList
    }

    override suspend fun fetchBMIHistory(
        data: BMIHistoryModel,
        personId: String
    ): LiveData<Resource<BMIHistoryModel.Response>> {

        return object :
            StoreNetworkDataBoundResource<BMIHistoryModel.Response, BaseResponse<BMIHistoryModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): BMIHistoryModel.Response {
                return BMIHistoryModel.Response()
            }

            override suspend fun saveCallResults(items: BMIHistoryModel.Response) {
                Timber.i("BMI list size=> ${items.bMIHistory.size}")
                for (bmiHistory in items.bMIHistory) {
                    val recordDate = DateHelper.convertServerDateToDBDate(bmiHistory.recordDate)
                    bmiHistory.recordDate =
                        DateHelper.convertServerDateToDBDate(bmiHistory.recordDate)
                    bmiHistory.recordDateMillisec =
                        DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(recordDate)
                }
                paramDao.saveBMIHistory(items)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.BMI_HISTORY,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                    personId = personId
                )
                dataSyncMasterDao.insertApiSyncData(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<BMIHistoryModel.Response>> {
                return dataSource.getBMIHistory(data)
            }

            override fun processResponse(response: BaseResponse<BMIHistoryModel.Response>): BMIHistoryModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: BMIHistoryModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchWHRHistory(
        data: WHRHistoryModel,
        personId: String
    ): LiveData<Resource<WHRHistoryModel.Response>> {

        return object :
            StoreNetworkDataBoundResource<WHRHistoryModel.Response, BaseResponse<WHRHistoryModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): WHRHistoryModel.Response {
                return WHRHistoryModel.Response()
            }

            override suspend fun saveCallResults(items: WHRHistoryModel.Response) {
                Timber.i("WHR list size=> ${items.wHRHistory.size}")
                for (whrHistory in items.wHRHistory) {
                    val recordDate = DateHelper.convertServerDateToDBDate(whrHistory.recordDate)
                    whrHistory.recordDate = recordDate
                    whrHistory.recordDateMillisec =
                        DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(recordDate)
                }
                paramDao.saveWHRHistory(items)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.WHR_HISTORY,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                    personId = personId
                )
                dataSyncMasterDao.insertApiSyncData(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<WHRHistoryModel.Response>> {
                return dataSource.getWHRHistory(data)
            }

            override fun processResponse(response: BaseResponse<WHRHistoryModel.Response>): WHRHistoryModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: WHRHistoryModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchBloodPressureHistory(
        data: BloodPressureHistoryModel,
        personId: String
    ): LiveData<Resource<BloodPressureHistoryModel.Response>> {

        return object :
            StoreNetworkDataBoundResource<BloodPressureHistoryModel.Response, BaseResponse<BloodPressureHistoryModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): BloodPressureHistoryModel.Response {
                return BloodPressureHistoryModel.Response()
            }

            override suspend fun saveCallResults(items: BloodPressureHistoryModel.Response) {
                Timber.i("BloodPressure list size=> ${items.bloodPressureHistory.size}")
                for (bpHistory in items.bloodPressureHistory) {
                    val recordDate = DateHelper.convertServerDateToDBDate(bpHistory.recordDate)
                    bpHistory.recordDate = recordDate
                    bpHistory.recordDateMillisec =
                        DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(recordDate)
                }
                paramDao.saveBloodPressureHistory(items)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.BLOOD_PRESSURE_HISTORY,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                    personId = personId
                )
                dataSyncMasterDao.insertApiSyncData(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<BloodPressureHistoryModel.Response>> {
                return dataSource.getBloodPressureHistory(data)
            }

            override fun processResponse(response: BaseResponse<BloodPressureHistoryModel.Response>): BloodPressureHistoryModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: BloodPressureHistoryModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchLabRecordsList(
        data: LabRecordsListModel,
        personId: String
    ): LiveData<Resource<TrackParameterMaster.HistoryResponse>> {
//        paramDao.deleteHistory()
        return object :
            NetworkBoundResource<TrackParameterMaster.HistoryResponse, BaseResponse<TrackParameterMaster.HistoryResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): TrackParameterMaster.HistoryResponse {
                return TrackParameterMaster.HistoryResponse()
            }

            override suspend fun saveCallResults(items: TrackParameterMaster.HistoryResponse) {
                Timber.i("History list size=> ${items.history.size}")

                for (paramHistory in items.history) {
                    val recordDate = DateHelper.convertServerDateToDBDate(paramHistory.recordDate)
                    paramHistory.recordDate = recordDate
                    paramHistory.recordDateMillisec =
                        DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(recordDate)
                    if (Utilities.isNullOrEmpty(paramHistory.observation)) {
                        paramHistory.observation = ""
                    }
                }

                paramDao.insertHistory(items.history)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.PARAMETER_HISTORY,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                    personId = personId
                )
//                if(dataSyncMasterDao.getLastSyncDataList().find { it.apiName == ApiConstants.PARAMETER_HISTORY } == null)
                dataSyncMasterDao.insertApiSyncData(dataSyc)
//                else
//                    dataSyncMasterDao.updateRecord(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<TrackParameterMaster.HistoryResponse>> {
                return dataSource.fetchLabRecordsList(data)
            }

            override fun processResponse(response: BaseResponse<TrackParameterMaster.HistoryResponse>): TrackParameterMaster.HistoryResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: TrackParameterMaster.HistoryResponse?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchParamList(
        forceRefresh: Boolean,
        data: ParameterListModel
    ): LiveData<Resource<ParameterListModel.Response>> {
        /*return object : NetworkBoundResource<ParameterListModel.Response,BaseResponse<ParameterListModel.Response>>(){

            override fun processResponse(response: BaseResponse<ParameterListModel.Response>): ParameterListModel.Response {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ParameterListModel.Response) { }

            override fun shouldFetch(data: ParameterListModel.Response?): Boolean = true

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ParameterListModel.Response {
                return ParameterListModel.Response()
            }

            override fun createCallAsync(): Deferred<BaseResponse<ParameterListModel.Response>> {
                return dataSource.fetchParamList(data = data)
            }

        }.build().asLiveData()*/

        return object :
            StoreNetworkDataBoundResource<ParameterListModel.Response, BaseResponse<ParameterListModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ParameterListModel.Response {
                return ParameterListModel.Response()
            }

            override suspend fun saveCallResults(items: ParameterListModel.Response) {
                paramDao.deleteAllRecords()
                paramDao.deleteAllRangeData()
                paramDao.save(items)
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.TRACK_PARAM_LIST_MASTER,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd
                )
//                if(dataSyncMasterDao.getLastSyncDataList().find { it.apiName == ApiConstants.TRACK_PARAM_LIST_MASTER } == null)
                dataSyncMasterDao.insertApiSyncData(dataSyc)
//                else
//                    dataSyncMasterDao.updateRecord(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<ParameterListModel.Response>> {
                return dataSource.fetchParamList(data = data)
            }

            override fun processResponse(response: BaseResponse<ParameterListModel.Response>): ParameterListModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: ParameterListModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchParameterPreferences(data: ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>> {

        return object :
            NetworkBoundResource<ParameterPreferenceModel.Response, BaseResponse<ParameterPreferenceModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ParameterPreferenceModel.Response {
                return ParameterPreferenceModel.Response()
            }

            override suspend fun saveCallResults(items: ParameterPreferenceModel.Response) {}

            override fun createCallAsync(): Deferred<BaseResponse<ParameterPreferenceModel.Response>> {
                return dataSource.getParameterPreferences(data)
            }

            override fun processResponse(response: BaseResponse<ParameterPreferenceModel.Response>): ParameterPreferenceModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: ParameterPreferenceModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun saveLabParameter(data: SaveParameterModel): LiveData<Resource<SaveParameterModel.Response>> {

        return object :
            NetworkBoundResource<SaveParameterModel.Response, BaseResponse<SaveParameterModel.Response>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): SaveParameterModel.Response {
                return SaveParameterModel.Response()
            }

            override suspend fun saveCallResults(items: SaveParameterModel.Response) {
                val listHistory: ArrayList<TrackParameterMaster.History> = arrayListOf()
                if (items.records.isNotEmpty()) {
                    for (item in items.records) {
                        val recordDate = DateHelper.convertServerDateToDBDate(item.recordDate)
                        val paramMaster = paramDao.getParameterDetails(item.parameterCode)
                        if (paramMaster.isNotEmpty()) {
                            val historyItem = TrackParameterMaster.History(
                                personID = item.personID,
                                profileCode = item.profileCode,
                                parameterCode = item.parameterCode,
                                recordDate = recordDate,
                                unit = item.unit,
                                value = item.value,
                                sync = false,
                                createdDate = item.createdDate,
                                modifiedBy = item.modifiedBy,
                                modifiedDate = item.modifiedDate,
                                recordDateMillisec = DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(
                                    recordDate
                                ),
                                description = paramMaster[0].description,
                                observation = if (item.observation.equals(
                                        "NA",
                                        true
                                    )
                                ) "" else item.observation,
                                profileName = paramMaster[0].profileName,
                                textValue = item.textValue
                            )
                            listHistory.add(historyItem)
                        } else {
                            val historyItem = TrackParameterMaster.History(
                                personID = item.personID,
                                profileCode = item.profileCode,
                                parameterCode = item.parameterCode,
                                recordDate = recordDate,
                                unit = item.unit,
                                value = item.value,
                                sync = false,
                                createdDate = item.createdDate,
                                modifiedBy = item.modifiedBy,
                                modifiedDate = item.modifiedDate,
                                recordDateMillisec = DateHelper.getDateTimeAs_ddMMMyyyy_ToMilliSec(
                                    recordDate
                                ),
                                description = item.parameterCode,
                                observation = item.observation,
                                profileName = item.profileCode
                            )
                            listHistory.add(historyItem)
                        }
                    }
                    paramDao.insertHistory(listHistory)
                }
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveParameterModel.Response>> {
                return dataSource.addTrackParameter(data)
            }

            override fun processResponse(response: BaseResponse<SaveParameterModel.Response>): SaveParameterModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: SaveParameterModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    private fun getDescription(parameterCode: String): String {
        when {
            parameterCode.equals("HEIGHT") -> {
                return "HEIGHT"
            }
            parameterCode.equals("WEIGHT") -> {
                return "WEIGHT"
            }
            parameterCode.equals("BMI") -> {
                return "BMI"
            }
            else -> {
                return parameterCode
            }
        }
    }

    override suspend fun listHistoryByProfileCodeAndMonthYear(
        personId: String,
        profileCode: String,
        month: String,
        year: String
    ): List<TrackParameterMaster.History> {

        return if (profileCode.equals("URINE", ignoreCase = true)) {
            paramDao.listHistoryByProfileCodeAndMonthYearForUrineProfile(
                personId,
                profileCode,
                month,
                year
            )
        } else {
            paramDao.listHistoryByProfileCodeAndMonthYear(personId, profileCode, month, year)
        }

    }

    override suspend fun listHistoryWithLatestRecord(
        personId: String,
        profileCode: String,
        parameterCode: String
    ): List<TrackParameterMaster.History> {
        val list: ArrayList<TrackParameterMaster.History> = arrayListOf()
        if (parameterCode.isNotEmpty()) {
            list.addAll(paramDao.listHistoryWithLatestRecord(personId, profileCode, parameterCode))
        } else {
            list.addAll(paramDao.listHistoryWithLatestRecord(personId, profileCode))
        }
        return list
    }

    override suspend fun getAllProfileCodes(): List<ParameterProfile> {
        val list: ArrayList<ParameterProfile> = arrayListOf()
        try {
            val databaseList = paramDao.getAllProfileCodes()
            for (listItem in databaseList) {
                list.add(
                    ParameterProfile(
                        profileCode = listItem.profileCode!!,
                        profileName = listItem.profileName!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    override suspend fun getAllProfilesWithRecentSelectionList(personId: String): List<ParameterProfile> {
        val finalList: ArrayList<ParameterProfile> = arrayListOf()
        try {
            val allProfilesList = paramDao.getAllProfileCodes()
            var historyList = paramDao.getTrackParameterHistory(personId)
            if (!historyList.isNullOrEmpty()) {
                historyList = historyList.distinctBy { it.profileCode }
                Utilities.printData("DistinctProfilesList", historyList)
                for (data in allProfilesList) {
                    var isSelection = false
                    for (item in historyList) {
                        if (data.profileCode.equals(item.profileCode, ignoreCase = true)) {
                            isSelection = true
                        }
                    }
                    finalList.add(
                        ParameterProfile(
                            profileCode = data.profileCode!!,
                            profileName = data.profileName!!,
                            isSelection = isSelection
                        )
                    )
                }
            } else {
                Utilities.printData("DistinctProfilesList", historyList)
                for (data in allProfilesList) {
                    finalList.add(
                        ParameterProfile(
                            profileCode = data.profileCode!!,
                            profileName = data.profileName!!
                        )
                    )
                }
            }
            //Utilities.printList("FinalList",finalList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalList
    }

/*    override suspend fun getHistoryTableDataList(profileCode: String,personId: String) : List<HistorytableModel> {

        val paramList = paramDao.getParameterListBaseOnCode(profileCode)
        val historyList : MutableList<HistorytableModel> = mutableListOf()

        for ( param in paramList ) {
            val subList = paramDao.getParameterHisBaseOnCode(param.code!!,personId)
            val dateValueList : MutableList<DateValue> = mutableListOf()
            //Timber.i("SubList:" +subList)
            for ( i in subList  ) {
                dateValueList.add( DateValue( i.recordDate , i.value ) )
            }
            dateValueList.reverse()
            historyList.add( HistorytableModel( param.code , param.description , param.unit , dateValueList ))
        }
        return historyList
    }*/

    override suspend fun insertSelectedParameter(trackParameter: TrackParameters) {
        paramDao.insertTrackParameter(trackParameter)
    }

    override suspend fun getTrackParametersList(): List<TrackParameters> {
        return paramDao.getTrackParametersList()
    }

    override suspend fun fetchParamHisBaseOnCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return paramDao.getParameterCodeHistory(pCode, personId)
    }

    override suspend fun getLatestParameterBasedOnProfileCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return paramDao.getLatestParametersBaseOnHisProfileCode(pCode, personId)
    }

    override suspend fun getLatestParameterBasedOnProfileCodes(
        pCode: String,
        pCodeTwo: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return paramDao.getLatestParametersBaseOnHisProfileCodes(pCode, pCodeTwo, personId)
    }

    override suspend fun getLatestRecordBasedOnParamCode(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return paramDao.getLatestParameterBaseOnHisParameterCode(paramCode, personId)
    }

    override suspend fun fetchParamListBaseOnCode(pCode: String): List<TrackParameterMaster.Parameter> {
        return paramDao.getParameterListBaseOnCode(pCode)
    }

    override suspend fun fetchDashboardDataFromDB(): List<DashboardObservationData> {
        return paramDao.getDashboardObservationData()
    }

    override suspend fun getParametersFromDB(): List<TrackParameterMaster.Parameter> {
        return paramDao.getParameterData()
    }

    override suspend fun deleteSelectedParameters(profileCode: String) {
        paramDao.deletFromTrackParametersList(profileCode)
    }

    override suspend fun deleteHistoryWithOtherPersonId(personId: String) {
        paramDao.deleteHistoryWithOtherPersonId(personId)
    }

    override suspend fun getSelectParameterList(
        selectedParam: String,
        showAllProfile: String
    ): List<ParameterListModel.SelectedParameter> {
        val databaseList = paramDao.getSelectParameterList()
        var item: ParameterListModel.SelectedParameter
        val list: ArrayList<ParameterListModel.SelectedParameter> = arrayListOf()
        for (listItem in databaseList) {
            item = ParameterListModel.SelectedParameter(
                profileCode = listItem.profileCode!!,
                profileName = listItem.profileName!!,
                iconPosition = getIconPosition(listItem.profileCode?.toUpperCase()!!),
                selectionStatus = selectedParam.contains("" + listItem.profileCode?.toUpperCase())
            )
            if (showAllProfile.equals("false", true)) {
                if (selectedParam.contains(item.profileCode, true))
                    list.add(item)
            } else {
                list.add(item)
            }
        }
        return list
    }

    override suspend fun logoutUser() {
        dataSyncMasterDao.deleteAllRecords()
        paramDao.deleteAllRangeData()
        paramDao.deleteHistory()
        paramDao.deleteAllRecords()
        paramDao.deletTrackParametersTable()
    }

    override suspend fun getParameterObservationList(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.TrackParameterRanges> {
        val person = paramDao.getRelativeInformation(personId)
        if (person.isNotEmpty())
            return paramDao.getParameterObservationBasedOnParameterCode(
                pCode,
                person[0].age.toInt(),
                person[0].gender.toInt()
            )
        else
            return paramDao.getParameterObservationBasedOnParameterCode(pCode, 31, 1)
    }

    override suspend fun getLatestParameterData(
        profileCode: String,
        personId: String
    ): List<ParameterListModel.InputParameterModel> {
        return paramDao.getLatestDataOfParameter(profileCode, personId)
    }

    override suspend fun getParameterDataBasedOnRecordDate(
        profileCode: String,
        personId: String,
        recordDate: String
    ): List<ParameterListModel.InputParameterModel> {
        return paramDao.getParameterDataByRecordDate(profileCode, recordDate, personId)
//        return listOf()
    }

    private fun getIconPosition(profileCode: String): Int {
        val position = 0
        when (profileCode) {
            "BMI" -> return 1
            "BLOODPRESSURE" -> return 2
            "DIABETIC" -> return 3
            "HEMOGRAM" -> return 4
            "KIDNEY" -> return 5
            "LIPID" -> return 6
            "LIVER" -> return 7
            "THYROID" -> return 8
            "WHR" -> return 9
            "URINE" -> return 10
        }
        return position
    }

}