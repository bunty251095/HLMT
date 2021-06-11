package com.caressa.track_parameter

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.AppColorHelper
import com.caressa.track_parameter.util.TrackParameterHelper
import kotlinx.android.synthetic.main.activity_parameter_home.*
import kotlinx.android.synthetic.main.toolbar_layout_parameter.*
import kotlinx.android.synthetic.main.toolbar_layout_parameter.view.*
import kotlinx.android.synthetic.main.toolbar_layout_parameter.view.toolbar_title
import timber.log.Timber
import java.util.*

class ParameterHomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameter_home)

        setSupportActionBar(toolbarLayout.toolBarParameter)
        // Setting up a back button
        navController = nav_host_fragment_medication.findNavController()
        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener{ controller, destination, _ ->

            toolbar_title.text = when (destination.id) {
                R.id.parameterDashboard -> "Track Health Parameters"
                R.id.homeFragment -> "Track Health Parameters"
                R.id.selectParameterFragment -> "Select Parameters"
                R.id.updateParameterFragment -> "UpdateParameters"
                R.id.dashboardFragment -> "Parameters Dashboard"
                R.id.parameterDetailFragment -> "Track Health Parameters"

                R.id.selectParameterFragment -> "Select Parameters"
                R.id.updateParameterFrag -> "Update Parameters"
                R.id.historyFragment -> "Parameter History"
                R.id.detailHistoryFragment -> "Complete History"
                else -> "Track Health Parameters"
            }

            if ( destination.id == R.id.homeFragment ) {
                toolbarLayout.visibility = View.GONE
            } else {
                toolbarLayout.visibility = View.VISIBLE
            }

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
            toolBarParameter.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
        }
        img_vivant_logo.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
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

    fun setDataReceivedListener( listener: OnGoogleAccountSelectListener ) {
        this.googleAccountListener = listener
    }

    interface OnGoogleAccountSelectListener {
        fun onGoogleAccountSelection(from:String)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            Timber.e("requestCode-> $requestCode")
            Timber.e("resultCode-> $resultCode")
            Timber.e("data-> $data")

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FitRequestCode.READ_DATA.value) {
                    Timber.e("onActivityResult READ_DATA")
                    if ( googleAccountListener != null ) {
                        googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                    }
                }
            } else {
                FitnessDataManager(this).oAuthErrorMsg(requestCode, resultCode)
                if ( googleAccountListener != null ) {
                    googleAccountListener!!.onGoogleAccountSelection(Constants.FAILURE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
