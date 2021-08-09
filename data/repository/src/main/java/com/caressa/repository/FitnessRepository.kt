package com.caressa.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.caressa.local.dao.FitnessDao
import com.caressa.model.entity.FitnessEntity
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.SetGoalModel
import com.caressa.model.fitness.StepsHistoryModel
import com.caressa.model.fitness.StepsSaveListModel
import com.caressa.remote.FitnessDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import java.util.*

interface FitnessRepository {

    suspend fun fetchStepsListHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>>
    suspend fun fetchLatestGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>>
    suspend fun saveStepsGoal(data: SetGoalModel): LiveData<Resource<SetGoalModel.Response>>
    suspend fun saveStepsList(data: StepsSaveListModel): LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>>
    suspend fun logoutUser()

}

class FitnessRepositoryImpl(
    private val dataSource: FitnessDatasource,
    private val fitnessDao: FitnessDao,
    private val context: Context) : FitnessRepository {

    override suspend fun fetchStepsListHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {

        return object :
            NetworkBoundResource<StepsHistoryModel.Response, BaseResponse<StepsHistoryModel.Response>>(context) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): StepsHistoryModel.Response {
                return StepsHistoryModel.Response(fitnessDao.getUserStepsData())
            }

            override fun createCallAsync(): Deferred<BaseResponse<StepsHistoryModel.Response>> {
                return dataSource.fetchStepsListHistory(data)
            }

            override fun processResponse(response: BaseResponse<StepsHistoryModel.Response>): StepsHistoryModel.Response {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: StepsHistoryModel.Response) {
                val stepGoalHistory: MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()
                for (i in items.stepGoalHistory) {
                    val history = FitnessEntity.StepGoalHistory(
                        stepID = i.stepID,
                        goalID = i.goalID,
                        recordDate = i.recordDate.split("T").toTypedArray()[0],
                        stepsCount = i.stepsCount,
                        totalGoal = i.totalGoal,
                        distance = i.distance,
                        calories = i.calories,
                        goalPercentile = i.goalPercentile,
                        activeTime = "",
                        lastRefreshed = Date()
                    )
                    stepGoalHistory.add(history)
                }
                fitnessDao.save(stepGoalHistory)
            }

            override fun shouldFetch(data: StepsHistoryModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun fetchLatestGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>> {

        return object :
            NetworkBoundResource<GetStepsGoalModel.Response, BaseResponse<GetStepsGoalModel.Response>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetStepsGoalModel.Response {
                return GetStepsGoalModel.Response()
            }

            override fun createCallAsync(): Deferred<BaseResponse<GetStepsGoalModel.Response>> {
                return dataSource.fetchLatestGoal(data)
            }

            override fun processResponse(response: BaseResponse<GetStepsGoalModel.Response>): GetStepsGoalModel.Response {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: GetStepsGoalModel.Response) {}

            override fun shouldFetch(data: GetStepsGoalModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun saveStepsGoal(data: SetGoalModel): LiveData<Resource<SetGoalModel.Response>> {

        return object :
            NetworkBoundResource<SetGoalModel.Response, BaseResponse<SetGoalModel.Response>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SetGoalModel.Response {
                return SetGoalModel.Response()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SetGoalModel.Response>> {
                return dataSource.saveStepsGoal(data)
            }

            override fun processResponse(response: BaseResponse<SetGoalModel.Response>): SetGoalModel.Response {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SetGoalModel.Response) {}

            override fun shouldFetch(data: SetGoalModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun saveStepsList(data: StepsSaveListModel): LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> {

        return object :
            NetworkBoundResource<StepsSaveListModel.StepsSaveListResponse, BaseResponse<StepsSaveListModel.StepsSaveListResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): StepsSaveListModel.StepsSaveListResponse {
                return StepsSaveListModel.StepsSaveListResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<StepsSaveListModel.StepsSaveListResponse>> {
                return dataSource.saveStepsList(data)
            }

            override fun processResponse(response: BaseResponse<StepsSaveListModel.StepsSaveListResponse>): StepsSaveListModel.StepsSaveListResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: StepsSaveListModel.StepsSaveListResponse) {}

            override fun shouldFetch(data: StepsSaveListModel.StepsSaveListResponse?): Boolean =
                true

        }.build().asLiveData()

    }

    override suspend fun logoutUser() {
        fitnessDao.deleteStepGoalHistoryTable()
    }

}
