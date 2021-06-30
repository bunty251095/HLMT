package com.caressa.records_tracker

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.karumi.dexter.Dexter
import kotlinx.android.synthetic.main.activity_health_records.*
import kotlinx.android.synthetic.main.toolbar_layout_shr.*

class HealthRecordsActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_records)

        setSupportActionBar(toolbar_shr)
        // Setting up a back button
        navController = nav_host_fragment_shr.findNavController()
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if ( intent.hasExtra(Constants.FROM) && !Utilities.isNullOrEmpty(intent.getStringExtra(Constants.FROM)) ) {
            when( intent.getStringExtra(Constants.FROM) ) {
                Constants.MEDICATION -> bundle.putString(Constants.FROM, Constants.MEDICATION)
                Constants.TRACK_PARAMETER -> bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
            }
        }

        navController.setGraph(R.navigation.nav_graph_shr_feature,bundle)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.fragmentDigitize -> resources.getString(R.string.TITLE_DIGITIZED_RECORD)
                R.id.viewRecordsFragment -> resources.getString(R.string.TITLE_UPLOADED_RECORDS)
                R.id.digitizedRecordsListFragment -> resources.getString(R.string.TITLE_DIGITIZED_RECORDS)
                else -> resources.getString(R.string.TITLE_UPLOAD_RECORDS)
            }

            if ( destination.id == R.id.healthRecordsDashboardFragment ) {
                toolBarView.visibility = View.GONE
            } else {
                toolBarView.visibility = View.VISIBLE
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
            toolbar_shr.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
        }

        img_vivant_logo.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_SCREEN)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }



}
