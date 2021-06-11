package com.caressa.fitness_tracker.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.SetGoalModel
import com.caressa.model.fitness.StepsHistoryModel
import com.caressa.model.fitness.StepsSaveListModel
import com.caressa.repository.FitnessRepository
import com.caressa.repository.utils.Resource


class FitnessManagementUseCase(private val repository: FitnessRepository) {

    suspend fun invokeStpesHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return Transformations.map(
            repository.fetchStepsListHistory(data = data)) {
            it
        }
    }

    suspend fun invokeFetchStepsGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>> {
        return Transformations.map(
            repository.fetchLatestGoal(data = data)) {
            it
        }
    }

    suspend fun invokeSaveStepsGoal(data: SetGoalModel): LiveData<Resource<SetGoalModel.Response>> {
        return Transformations.map(
            repository.saveStepsGoal(data = data)) {
            it
        }
    }

    suspend fun invokeSaveStepsList(data: StepsSaveListModel): LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> {
        return Transformations.map(
            repository.saveStepsList(data = data)) {
            it
        }
    }

}