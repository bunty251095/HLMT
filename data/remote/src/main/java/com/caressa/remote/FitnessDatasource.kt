package com.caressa.remote

import com.caressa.model.fitness.*
import com.caressa.model.medication.DrugsModel

class FitnessDatasource(private val defaultService: ApiService, private val encryptedService: ApiService) {

    fun fetchLatestGoal(data : GetStepsGoalModel) = encryptedService.fetchLatestGoal(data)

    fun fetchStepsListHistory(data : StepsHistoryModel) = encryptedService.fetchStepsListHistory(data)

    fun saveStepsGoal(data : SetGoalModel) = encryptedService.saveStepsGoal(data)

    fun saveStepsList(data : StepsSaveListModel) = encryptedService.saveStepsList(data)

    fun saveStepsData(data : FitnessModel) = encryptedService.saveStepsData(data)

}