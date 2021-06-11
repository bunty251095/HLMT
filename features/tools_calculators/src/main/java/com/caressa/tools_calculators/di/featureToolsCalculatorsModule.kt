package com.caressa.tools_calculators.di

import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.domain.ToolsCalculatorsUseCase
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureToolsCalculatorsModule = module {
    single { DataHandler( get() ) }
    factory { ToolsCalculatorsUseCase(get(), get()) }
    viewModel { ToolsCalculatorsViewModel(get(), get() , get() , get() , get() ) }
}