package com.caressa.local.di

import com.caressa.local.ArchAppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

private const val DATABASE = "DATABASE"

val localModule = module {
    single(DATABASE) { ArchAppDatabase.buildDatabase(androidContext()) }
    factory { (get(DATABASE) as ArchAppDatabase).vivantUserDao() }
    factory { (get(DATABASE) as ArchAppDatabase).medicationDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).fitnessDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).hraDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).shrDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).dataSyncMasterDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).trackParameterDao() }
    factory { (get(DATABASE) as  ArchAppDatabase).appCacheMasterDao() }
}