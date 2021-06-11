package com.caressa.security.di

import android.content.Context
import android.content.SharedPreferences
import com.caressa.model.AppConfigurationSingleton
import com.caressa.security.domain.HraManagementUseCase
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureSecurityModule = module {
    factory { UserManagementUseCase(get()) }
    factory { HraManagementUseCase(get()) }
    viewModel { LoginViewModel(get(), get(), get() , get() ) }
    viewModel { HraViewModel(get(), get(), get()) }
    viewModel { LoginWithOtpViewModel(get(), get(), get() , get()) }
    viewModel { SignUpViewModel(get(), get(), get() , get() ) }
    viewModel { TermsAndConditionViewModel(get(), get()) }
    viewModel { ForgetPasswordViewModel(get(), get() , get(), get()) }
    single { AppConfigurationSingleton() }
    single<SharedPreferences> { androidContext().getSharedPreferences("VivantPreferences", Context.MODE_PRIVATE) }
}