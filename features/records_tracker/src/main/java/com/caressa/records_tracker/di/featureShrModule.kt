package com.caressa.records_tracker.di

import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.domain.ShrManagementUseCase
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureShrModule = module {
 factory { DataHandler( get() ) }
 factory { ShrManagementUseCase(get()) }
 viewModel { HealthRecordsViewModel( get(), get(), get(), get() , get() ) }
}