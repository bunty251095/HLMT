package com.caressa.repository.di

import com.caressa.repository.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

val repositoryModule = module {
    factory { AppDispatchers(Dispatchers.Main, Dispatchers.IO) }
    factory { UserRepositoryImpl(get(), get()) as UserRepository }
    factory { HomeRepositoryImpl(get(),get(),get(),get(),get(),get(),get()) as HomeRepository }
    factory { MedicationRepositoryImpl(get(),get(), get()) as MedicationRepository }
    factory { FitnessRepositoryImpl(get(),get()) as FitnessRepository }
    factory { HraRepositoryImpl( get() , get(), get() , get() ) as HraRepository}
    factory { ToolsRepositoryImpl( get()) as ToolsRepository}
    factory { ParameterRepositoryImpl(get(), get(), get()) as ParameterRepository}
    factory { ShrRepositoryImpl( get() , get() , get() , get() ) as StoreRecordRepository}
    factory { BlogsRepositoryImpl( get() ) as BlogsRepository}
    factory { ToolsCalculatorsRepositoryImpl( get() ) as ToolsCalculatorsRepository}
}