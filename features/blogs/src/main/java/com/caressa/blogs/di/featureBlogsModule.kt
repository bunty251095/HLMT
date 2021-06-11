package com.caressa.blogs.di

import com.caressa.blogs.domain.BlogsManagementUseCase
import com.caressa.blogs.viewmodel.BlogViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureBlogsModule = module {
    factory { BlogsManagementUseCase(get()) }
    viewModel { BlogViewModel( get(), get(), get()  ) }
}