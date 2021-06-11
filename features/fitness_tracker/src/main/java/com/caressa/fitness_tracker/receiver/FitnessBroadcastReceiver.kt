package com.caressa.fitness_tracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.fitness_tracker.common.StepsDataSingleton
import com.caressa.model.entity.FitnessEntity
import com.caressa.model.fitness.GetStepsGoalModel
import timber.log.Timber
import java.util.*

class FitnessBroadcastReceiver : BroadcastReceiver() {

    private val stepsDataSingleton = StepsDataSingleton.instance!!

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Timber.e("action---> $action")
        if ( !Utilities.isNullOrEmpty(action) ) {
            if (action.equals(Constants.CLEAR_FITNESS_DATA, ignoreCase = true)) {
                stepsDataSingleton.clearStepsData()
/*                stepsDataSingleton.stepHistoryList.clear()
                stepsDataSingleton.latestGoal = GetStepsGoalModel.LatestGoal()
                stepsDataSingleton.selectedDateHistory = FitnessEntity.StepGoalHistory(lastRefreshed = Date())*/
            }
        }
    }
}