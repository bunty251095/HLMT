package com.caressa.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.caressa.common_test.rules.CoroutinesMainDispatcherRule
import com.caressa.local.dao.VivantUserDao
import com.caressa.remote.SecurityDatasource
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    // FOR DATA
    private lateinit var userRepository: UserRepository
    private val userService = mockk<SecurityDatasource>()
    private val vuserDao = mockk<VivantUserDao>(relaxed = true)

    @Before
    fun setUp() {
        userRepository = UserRepositoryImpl(userService,vuserDao)
    }

}