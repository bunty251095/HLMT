package com.caressa.home.di

import com.caressa.model.entity.HRASummary

interface ScoreListener {
    fun onScore(hraSummary: HRASummary?)
}