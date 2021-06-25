package com.caressa.security.ui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.caressa.common.constants.NavigationConstants
import com.caressa.security.R
import org.koin.android.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.showDialog
import com.caressa.model.AppConfigurationSingleton
import com.caressa.repository.utils.Resource
import com.caressa.security.viewmodel.HraViewModel
import timber.log.Timber
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.koin.android.ext.android.inject
import java.util.*

class SplashScreenActivity : AppCompatActivity(),DefaultNotificationDialog.OnDialogValueListener{

    private val viewModel: HraViewModel by viewModel()
    private val appConfigurationSingleton: AppConfigurationSingleton by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

/*        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_pulse)
        logo_vivant.startAnimation(animation)*/

        registerObserver()
        proceedInApp()
    }

    private fun registerObserver() {

        viewModel.hraHistorySummary.observe(this, Observer {
            val  intentToPass = Intent()
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if(it.status == Resource.Status.SUCCESS) {
                if(it.data != null) {
                    Timber.i("DATA--->${it.data}")
                    if (it!!.data!!.hRAHistory!!.previousWellnessScore.isEmpty()) {
                        appConfigurationSingleton.hraHistory = it.data!!.hRAHistory!!
//                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HRA_START)
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        startActivity(intentToPass)
                    } else {
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        startActivity(intentToPass)
                        finish()
                    }
                }
            } else if(it.status == Resource.Status.ERROR) {
                Timber.i("ERROR--->${it.errorMessage} :: ${it.errorNumber}")
                when {
                    it.errorNumber.equals("111",true) -> {
//                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HRA_START)
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                        startActivity(intentToPass)
                    }
                    it.errorNumber.equals("0",true) -> {
                        showDialog(listener = this,
                            title = resources.getString(R.string.NO_INTERNET_AVAILABLE),
                            message = resources.getString(R.string.ERROR_INTERNET_UNAVAILABLE),
                            showLeftBtn = false)
                    }
                    else -> {
                        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
                        startActivity(intentToPass)
                    }
                }
                //finish()
            }

        })

    }

    private fun proceedInApp() {
        val  intentToPass = Intent()
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//               Handler().postDelayed(Runnable {
        if( viewModel.getLoginStatus() ) {
            viewModel.getMedicalProfileSummary(forceRefresh = true)
            viewModel.getHraHistory()
        } else {
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
            startActivity(intentToPass)
            finish()
        }
//               },1000)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}
}