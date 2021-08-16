package com.caressa.medication_tracker

import android.content.*
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import kotlinx.android.synthetic.main.activity_medication_home.*
import kotlinx.android.synthetic.main.toolbar_layout_medication.*
import kotlinx.android.synthetic.main.toolbar_layout_medication.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class MedicationHomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MedicineTrackerViewModel by viewModel()
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_home)

        setSupportActionBar(toolBarMedication)
        // Setting up a back button
        navController = nav_host_fragment_medication.findNavController()
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if ( intent.hasExtra(Constants.FROM) ) {
            when(intent.getStringExtra(Constants.FROM)) {

                Constants.NOTIFICATION_ACTION -> {
                    bundle.putString(Constants.FROM,Constants.NOTIFICATION_ACTION)
                    bundle.putString(Constants.DATE,intent.getStringExtra(Constants.DATE))
                }
                Constants.TRACK_PARAMETER -> {
                    bundle.putString(Constants.FROM,Constants.TRACK_PARAMETER)
                }

            }
        }
        navController.setGraph(R.navigation.medication_tracker_nav_graph,bundle)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.medicineHome -> resources.getString(R.string.TITLE_MEDICINE_TRACKER)
                R.id.medicineDashboardFragment -> resources.getString(R.string.DASHBOARD)
                R.id.myMedicationsFragment -> resources.getString(R.string.TITLE_MY_MEDICATIONS)
                R.id.addMedicineFragment -> resources.getString(R.string.TITLE_ADD_MEDICATION)
                R.id.scheduleMedicineFragment -> resources.getString(R.string.TITLE_SCHEDULE_MEDICATION)
                else -> resources.getString(R.string.TITLE_MEDICINE_TRACKER)
            }

            if ( destination.id == R.id.medicineHome ) {
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
            toolBarMedication.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
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

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                //onBackPressedDispatcher.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
