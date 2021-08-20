package com.caressa.fitness_tracker

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.FirebaseHelper
import com.caressa.fitness_tracker.util.StepCountHelper
import kotlinx.android.synthetic.main.activity_fitness_home.*
import kotlinx.android.synthetic.main.fitness_toolbar_layout.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

class FitnessHomeActivity : AppCompatActivity() , KoinComponent {

    private lateinit var navController: NavController
    //private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    private val stepCountHelper : StepCountHelper = get()
    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitness_home)
        setSupportActionBar(toolBar_fitness)
        // Setting up a back button
        navController = nav_host_fragment.findNavController()
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if ( intent.hasExtra(Constants.FROM) && intent.getStringExtra(Constants.FROM).equals(Constants.TRACK_PARAMETER,ignoreCase = true) ) {
            bundle.putString(Constants.FROM,Constants.TRACK_PARAMETER)
        }
        navController.setGraph(R.navigation.fitness_navigation,bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.fitnessDashboardFragment -> resources.getString(R.string.TITLE_ACTIVITY_TRACKER)
                R.id.fragmentStepsDetail -> resources.getString(R.string.MONTHLY_STATISTICS)
                else -> resources.getString(R.string.TITLE_ACTIVITY_TRACKER)
            }
            if (destination.id == controller.graph.startDestination) {
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
            toolBar_fitness.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP
            )
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            Timber.e("requestCode-> $requestCode")
            Timber.e("resultCode-> $resultCode")
            Timber.e("data-> $data")

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FitRequestCode.READ_DATA.value) {
                    //refreshAdapter()
                    Timber.e("onActivityResult READ_DATA")
                    //viewModel.showProgressBar(resources.getString(R.string.SYNCHRONIZING))
                    if ( googleAccountListener != null ) {
                        googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                    }
                    stepCountHelper.synchronize(this)
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

    interface OnGoogleAccountSelectListener {
        fun onGoogleAccountSelection(from:String)
    }

    fun setDataReceivedListener( listener: OnGoogleAccountSelectListener ) {
        this.googleAccountListener = listener
    }

    fun routeToHomeScreen() {
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

}
