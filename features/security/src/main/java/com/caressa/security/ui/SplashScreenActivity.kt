package com.caressa.security.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.showDialog
import com.caressa.model.AppConfigurationSingleton
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.viewmodel.HraViewModel
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.hlpace_logo_layout.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SplashScreenActivity : AppCompatActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: HraViewModel by viewModel()

    private val appConfigurationSingleton: AppConfigurationSingleton by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        animationFadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                logo_container.img_logo.startAnimation(animationFadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        animationFadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                logo_container.img_logo.startAnimation(animationFadeOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        logo_container.img_logo.startAnimation(animationFadeOut)

        registerObserver()
        proceedInApp()
    }

    private fun registerObserver() {

        viewModel.hraHistorySummary.observe(this, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    Timber.i("DATA--->${it.data}")
                    if (it!!.data!!.hRAHistory!!.previousWellnessScore.isEmpty()) {
                        appConfigurationSingleton.hraHistory = it.data!!.hRAHistory!!
                        navigateToHome()
                    } else {
                        navigateToHome()
                    }
                }
            } else if (it.status == Resource.Status.ERROR) {
                Timber.i("ERROR--->${it.errorMessage} :: ${it.errorNumber}")
                when {
                    it.errorNumber.equals("111", true) -> {
                        navigateToHome()
                    }
                    it.errorNumber.equals("0", true) -> {
                        showDialog(
                            listener = this,
                            title = resources.getString(R.string.NO_INTERNET_AVAILABLE),
                            message = resources.getString(R.string.ERROR_INTERNET_UNAVAILABLE),
                            showLeftBtn = false
                        )
                    }
                    else -> {
                        navigateToLogin()
                    }
                }
            }
        })
    }

    private fun proceedInApp() {
        if (viewModel.getLoginStatus()) {
//            viewModel.getMedicalProfileSummary(forceRefresh = true)
//            viewModel.getHraHistory()
            navigateToHome()
        } else {
            navigateToLogin()
        }
    }

    private fun navigateToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intentToPass = Intent()
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentToPass.component =
                ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            startActivity(intentToPass)
            finish()
        }, 4000)
    }

    private fun navigateToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intentToPass = Intent()
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intentToPass.component =
                ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
            startActivity(intentToPass)
            finish()
        }, 4000)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}
}