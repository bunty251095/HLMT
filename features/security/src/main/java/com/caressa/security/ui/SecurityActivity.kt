package com.caressa.security.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.activity_security.*
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.caressa.common.utils.AppColorHelper
import kotlinx.android.synthetic.main.toolbar_layout_security.*
import com.caressa.security.R


class SecurityActivity : AppCompatActivity() {

    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        /*val crashButton = Button(this)
        crashButton.text = "Crash!"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))*/

        setSupportActionBar(toolBar)
        // Setting up a back button
        val navController = nav_host_fragment_security.findNavController()
        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.termsAndConditionFragment -> resources.getString(R.string.TERMS_CONDITIONS)
                R.id.userInfoFragment -> resources.getString(R.string.ADDITIONAL_DETAILS)
                R.id.hlmtLoginFragment -> ""
                R.id.loginViaOTPFragment -> ""
                R.id.userDetailsFragment -> ""
                else -> ""
            }
            if ( destination.id == R.id.stepOneFragment ) {
                toolBarView.visibility = View.GONE
            } else {
                toolBarView.visibility = View.VISIBLE
            }
            /*            img_vivant_logo.visibility = when (destination.id) {
                R.id.termsAndConditionFragment -> View.VISIBLE
                else -> View.GONE
            }*/
            if(destination.id == controller.graph.startDestination) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
                toolBar.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ContextCompat.getColor(this,R.color.white), BlendModeCompat.SRC_ATOP)
            } else {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
                toolBar.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_security).navigateUp()
    }
}
