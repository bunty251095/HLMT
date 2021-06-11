package com.caressa.fitness_tracker.common

import com.caressa.model.entity.FitnessEntity
import com.caressa.model.entity.FitnessEntity.StepGoalHistory
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.GetStepsGoalModel.LatestGoal
import com.caressa.model.hra.Option
import java.util.*

class StepsDataSingleton private constructor() {

    var latestGoal = LatestGoal()
    var stepHistoryList: MutableList<StepGoalHistory> = mutableListOf()
    var selectedDateHistory = StepGoalHistory(lastRefreshed = Date())

    fun clearStepsData() {
        instance = null
        stepHistoryList.clear()
        latestGoal = LatestGoal()
        selectedDateHistory = StepGoalHistory(lastRefreshed = Date())
    }

    companion object {
        var instance: StepsDataSingleton? = null
            get() {
                if (field == null) {
                    synchronized(StepsDataSingleton::class.java) {
                        if (field == null) {
                            field = StepsDataSingleton()
                        }
                    }
                }
                return field
            }
            private set
    }

}