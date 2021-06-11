package com.caressa.fitness_tracker.di

import com.caressa.fitness_tracker.domain.FitnessManagementUseCase
import com.caressa.fitness_tracker.util.FitnessHelper
import com.caressa.fitness_tracker.util.StepCountHelper
import com.caressa.fitness_tracker.viewmodel.FitnessViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureFitnessModule = module {
    factory { FitnessManagementUseCase(get()) }
    single { FitnessHelper(get()) }
    single { StepCountHelper(get()) }
    viewModel { FitnessViewModel(get(),get(),get(),get()) }

}