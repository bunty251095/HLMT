package com.caressa.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.caressa.home.di.featureHomeModule
import com.caressa.repository.AppDispatchers
import com.caressa.repository.UserRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeInstrumentedTests: KoinTest {

    private val userRepository = mockk<UserRepository>()

    @Before
    fun setUp(){
        loadKoinModules(featureHomeModule, module {
            factory { AppDispatchers(Dispatchers.Main, Dispatchers.Main) }
            factory { userRepository }
        })
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}