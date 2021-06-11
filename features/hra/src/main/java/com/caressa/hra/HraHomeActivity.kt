package com.caressa.hra

import android.R.attr.transitionName
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.model.AppConfigurationSingleton
import kotlinx.android.synthetic.main.activity_hra_home.*
import kotlinx.android.synthetic.main.toolbar_layout_hra.*
import kotlinx.android.synthetic.main.toolbar_layout_hra.view.*
import org.koin.android.ext.android.inject


class HraHomeActivity : AppCompatActivity() {

    private val appConfigurationSingleton: AppConfigurationSingleton by inject()
    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hra_home)

        setSupportActionBar(toolbar_hra)
        // Setting up a back button
        navController = nav_host_fragment_hra.findNavController()
        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            if(destination.id == controller.graph.startDestination) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            } else {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }
            toolbar_hra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_hra).navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
/*        if (appConfigurationSingleton.hraHistory.submittedWellnessScore.isEmpty()){
            super.onBackPressed()
        }else {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }*/
    }

}
