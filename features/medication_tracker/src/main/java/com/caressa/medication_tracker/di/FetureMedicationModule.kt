package com.caressa.medication_tracker.di

import com.caressa.medication_tracker.common.MedicationTrackerHelper
import com.caressa.medication_tracker.domain.MedicationManagementUseCase
import com.caressa.medication_tracker.viewmodel.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureMedicationModule = module {
    single { MedicationTrackerHelper( get() ) }
    factory { MedicationManagementUseCase(get(),get()) }
    viewModel { MedicineTrackerViewModel(get(),get(),get(),get(),get()) }
}