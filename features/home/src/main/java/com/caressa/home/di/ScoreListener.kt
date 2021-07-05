package com.caressa.home.di

import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.TrackParameterMaster

interface ScoreListener {
    fun onScore(hraSummary: HRASummary?)
    fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>)
    fun onStepGoalReceived(goal: Int)
}