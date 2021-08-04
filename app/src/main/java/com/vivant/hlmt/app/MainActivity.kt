package com.vivant.hlmt.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.caressa.common.constants.NavigationConstants
import com.caressa.security.ui.SplashScreenActivity
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NavigationConstants.APPID = BuildConfig.APPLICATION_ID
        Timber.e("BUILD_TYPE--->${BuildConfig.BUILD_TYPE}")
        Timber.e("AppId--->${NavigationConstants.APPID}")

        startActivity(Intent(this, SplashScreenActivity::class.java))
        finish()
    }

}
