package com.caressa.home.ui.ProfileAndFamilyMember

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.R
import kotlinx.android.synthetic.main.activity_family_profile.*
import kotlinx.android.synthetic.main.toolbar_home.*

class FamilyProfileActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_profile)

        setSupportActionBar(toolbar_home)
        // Setting up a back button
         navController = nav_host_fragment_family_profile.findNavController()
        setupActionBarWithNavController(navController)

        val bundle = Bundle()
        if ( intent.hasExtra(Constants.FROM) &&
            intent.getStringExtra(Constants.FROM).equals(Constants.HRA,ignoreCase = true) ) {
            bundle.putString(Constants.FROM, Constants.HRA)
        }
        navController.setGraph(R.navigation.nav_graph_family_profile,bundle)

        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.familyMembersListFragment2 -> resources.getString(R.string.TITLE_FAMILY_MEMBERS)
                R.id.editFamilyMemberDetailsFragment2 -> resources.getString(R.string.TITLE_EDIT_DETAILS)
                R.id.selectRelationshipFragment2 -> resources.getString(R.string.TITLE_SELECT_RELATION)
                R.id.addFamilyMemberFragment2 -> resources.getString(R.string.TITLE_ADD_FAMILY_MEMBER)
                else -> ""
            }

            if ( destination.id == R.id.familyMembersListFragment2 ) {
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
            toolbar_home.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
        }
        //NavigationUI.setupActionBarWithNavController(this, navController)
        img_vivant_logo.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
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

/*    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }*/

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_family_profile).navigateUp()
    }

}
