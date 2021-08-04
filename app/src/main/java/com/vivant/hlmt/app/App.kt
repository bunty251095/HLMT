package com.vivant.hlmt.app

import android.app.Application
import com.vivant.hlmt.app.di.appComponent
import org.koin.android.ext.android.startKoin
import timber.log.Timber

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        configureDi()
        Timber.plant(Timber.DebugTree())

    }

    // CONFIGURATION ---
    open fun configureDi() =
        startKoin(this, provideComponent())

    // PUBLIC API ---
    open fun provideComponent() = appComponent
}