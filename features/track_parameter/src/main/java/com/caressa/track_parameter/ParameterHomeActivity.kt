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
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.LocaleHelper
import kotlinx.android.synthetic.main.activity_parameter_home.*
import kotlinx.android.synthetic.main.toolbar_layout_parameter.*
import kotlinx.android.synthetic.main.toolbar_layout_parameter.view.*
import timber.log.Timber
import java.util.*

class ParameterHomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
        } catch (e:Exception) {
            e.printStackTrace()
        }
        setContentView(R.layout.activity_parameter_home)

        setSupportActionBar(toolbarLayout.toolBarParameter)
        // Setting up a back button
        navController = nav_host_fragment_medication.findNavController()
        setupActionBarWithNavController(navController)
        val bundle = Bundle()
        if ( intent.hasExtra(Constants.FROM) ) {
            when(intent.getStringExtra(Constants.FROM)) {
                "DashboardBP" -> {
                    bundle.putString(Constants.FROM,"DashboardBP")
                }
                "DashboardBMI" -> {
                    bundle.putString(Constants.FROM,"DashboardBMI")
                }
            }
        }
        navController.setGraph(R.navigation.track_param_nav_graph,bundle)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->

            toolbar_title.text = when (destination.id) {
                R.id.homeFragment -> resources.getString(R.string.TITLE_TRACK_HEALTH_PARAMETERS)
                R.id.selectParameterFragment -> resources.getString(R.string.TITLE_SELECT_PARAMETERS)
                R.id.updateParameterFrag -> resources.getString(R.string.TITLE_UPDATE_PARAMETERS)
                R.id.historyFragment -> resources.getString(R.string.TITLE_PARAMETER_HISTORY)
                R.id.detailHistoryFragment -> resources.getString(R.string.TITLE_COMPLETE_HISTORY)

                else -> resources.getString(R.string.TITLE_TRACK_HEALTH_PARAMETERS)
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
