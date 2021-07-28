package com.caressa.track_parameter.di

import com.caressa.track_parameter.domain.ParameterManagementUseCase
import com.caressa.track_parameter.viewmodel.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureParameterModule = module {
    factory { ParameterManagementUseCase(get()) }
    viewModel { DashboardViewModel(get(),get(),get(),get()) }
    viewModel { UpdateParamViewModel(get(), get(),get()) }
    viewModel { HistoryViewModel(get(), get(),get()) }

}