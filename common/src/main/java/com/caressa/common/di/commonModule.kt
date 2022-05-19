package com.caressa.common.di

import com.caressa.common.utils.PreferenceUtils
import org.koin.dsl.module.module

val commonModule = module {
    single { PreferenceUtils(get()) }
}