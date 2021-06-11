package com.caressa.home.di

import com.caressa.home.common.DataHandler
import com.caressa.home.domain.BackgroundCallUseCase
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.home.viewmodel.*
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureHomeModule = module {
    factory { DataHandler( get() ) }
    factory { HomeManagementUseCase( get(),get(),get()) }
    factory { BackgroundCallUseCase(get(), get(), get(), get(), get(), get()) }
    viewModel { BackgroundCallViewModel(get(), get(), get(), get()) }
    viewModel { DashboardViewModel(get(), get(), get(), get(), get()) }
    viewModel { FamilyDoctorViewModel(get(), get(), get(), get()) }
    viewModel { WebViewViewModel(get(), get(), get() ,get()) }
    viewModel { ProfileFamilyMemberViewModel( get(), get(), get(), get() , get() ) }
    //    single {  CustomProgress(get()) }

}