package com.caressa.blogs

import android.content.ComponentName
import android.content.Intent
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
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.LocaleHelper
import kotlinx.android.synthetic.main.activity_blogs.*
import kotlinx.android.synthetic.main.toolbar_layout_blogs.*

class BlogsActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_blogs)

        setSupportActionBar(toolbar_blogs)
        navController = nav_host_fragment_blogs.findNavController()
        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener{ controller, destination, _ ->
            toolbar_title.text = when (destination.id) {
                R.id.blogsDashboardFragment -> resources.getString(R.string.HEALTH_LIBRARY)
                R.id.blogDetailFragment -> resources.getString(R.string.BLOG_DETAILS)
                else -> ""
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
            toolbar_blogs.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.primaryColor(),BlendModeCompat.SRC_ATOP)
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
