package com.caressa.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*
import java.util.concurrent.TimeUnit

class FitnessEntity {

    @Entity(tableName = "StepGoalHistory")
    data class StepGoalHistory(
        @PrimaryKey
        @SerializedName("StepID")
        var stepID: Int = 0,
        @SerializedName("GoalID")
        var goalID: Int = 0,
        @SerializedName("RecordDate")
        var recordDate: String = "",
        @SerializedName("StepsCount")
        var stepsCount: Int = 0,
        @SerializedName("TotalGoal")
        var totalGoal: Int = 0,
        @SerializedName("Distance")
        var distance: Double = 0.0,
        @SerializedName("Calories")
        var calories: Int = 0,
        @SerializedName("GoalPercentile")
        var goalPercentile: Double = 0.0,
        @SerializedName("ActiveTime")
        var activeTime: String = "",

        var lastRefreshed: Date
    ) {

        fun haveToRefreshFromNetwork(): Boolean =
            TimeUnit.MILLISECONDS.toMinutes(Date().time - lastRefreshed.time) >= 10
    }
}