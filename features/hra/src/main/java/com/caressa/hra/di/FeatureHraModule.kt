package com.caressa.hra.di

import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.viewmodel.HraViewModel
import com.caressa.hra.domain.HraManagementUseCase
import com.caressa.hra.viewmodel.HraSummaryViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureHraModule = module {

    //factory { HraDataSingleton() }
    factory { HraManagementUseCase(get(),get(),get()) }
    viewModel { HraViewModel(get(), get(), get(), get() ) }
    viewModel { HraSummaryViewModel( get(), get(), get(), get() ) }

}