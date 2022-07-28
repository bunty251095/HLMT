package com.caressa.tools_calculators

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.LocaleHelper
import com.caressa.model.toolscalculators.UserInfoModel
import kotlinx.android.synthetic.main.activity_tools_calculators_home.*
import kotlinx.android.synthetic.main.toolbar_tools_calculators.*

class ToolsCalculatorsHomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            LocaleHelper.onAttach(this, LocaleHelper.getLanguage(this))
        } catch (e:Exception) {
            e.printStackTrace()
        }
        setContentView(R.layout.activity_tools_calculators_home)
        UserInfoModel.getInstance()!!.isDataLoaded = false

        setSupportActionBar(toolBarToolsCalculator)
        // Setting up a back button
        navController = nav_host_fragment_tools_calculators.findNavController()
        setupActionBarWithNavController(navController)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.toolsCalculatorsDashboardFragment -> resources.getString(R.string.TITLE_TOOLS_CALCULATORS)

                R.id.heartAgeFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartSummaryFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartReportFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)
                R.id.heartAgeRecalculateFragment -> resources.getString(R.string.HEART_AGE_CALCULATOR)

                R.id.diabetesCalculatorFragment -> resources.getString(R.string.DIABETES_CALCULATOR)
                R.id.diabetesSummaryFragment -> resources.getString(R.string.DIABETES_CALCULATOR)
                R.id.diabetesReportFragment -> resources.getString(R.string.DIABETES_CALCULATOR)

                R.id.hypertensionInputFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionSummeryFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionReportFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)
                R.id.hypertensionRecalculateFragment -> resources.getString(R.string.HYPERTENSION_CALCULATOR)

                R.id.stressAndAnxietyInputFragment -> resources.getString(R.string.STRESS_ANXIETY_CALCULATOR)
                R.id.stressAndAnxietySummeryFragment -> resources.getString(R.string.STRESS_ANXIETY_CALCULATOR)

                R.id.smartPhoneInputFragment -> resources.getString(R.string.SMART_PHONE_CALCULATOR)
                R.id.smartPhoneAddictionSummaryFragment -> resources.getString(R.string.SMART_PHONE_CALCULATOR)

                R.id.dueDateInputFragment -> resources.getString(R.string.DUE_DATE_CALCULATOR)
                R.id.dueDateCalculatorReportFragment -> resources.getString(R.string.DUE_DATE_CALCULATOR)
                else -> resources.getString(R.string.TITLE_TOOLS_CALCULATORS)
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
            toolBarToolsCalculator.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
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

}
