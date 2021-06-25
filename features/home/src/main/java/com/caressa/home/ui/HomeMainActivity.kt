package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.adapter.NavigationDrawerListAdapter
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivityHomeMainBinding
import com.caressa.home.di.ScoreListener
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.singleton.ToolsTrackerSingleton
import com.caressa.repository.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import kotlinx.android.synthetic.main.app_bar_home_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeMainActivity : BaseActivity(), NavigationDrawerListAdapter.DrawerClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: DashboardViewModel by viewModel()
    private val backgroudCallViewModel: BackgroundCallViewModel by viewModel()
    private lateinit var binding : ActivityHomeMainBinding

    private val appColorHelper = AppColorHelper.instance!!

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var navigationDrawerListAdapter : NavigationDrawerListAdapter? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var scoreListener:ScoreListener

    override fun getViewModel(): BaseViewModel = backgroudCallViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        binding.viewModel = viewModel
        binding.backViewModel = backgroudCallViewModel
        binding.lifecycleOwner = this
        setSupportActionBar(toolbar)
        configureNavController()
        callDashboardAPIs()
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = appColorHelper.primaryColor()
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        configureDrawerRecyclerView()



        Timber.i("HOME onCreate")

        backgroudCallViewModel.saveCloudMessagingId.observe(this, {})
        backgroudCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()

        // This observer is written because In background thread we are listening .
        backgroudCallViewModel.medicalProfileSummary.observe(this, {
            Timber.i("backgroudCallViewModel.medicalProfileSummary")
            if (it.data != null && it.status == Resource.Status.SUCCESS) {
//                Timber.i("Summery :: "+it.data?.MedicalProfileSummary)
//                viewModel.hraDetails.value = it.data?.MedicalProfileSummary
//                viewModel.hraDetails.postValue(it.data?.MedicalProfileSummary)
//                viewModel.getHraDetails()
                ToolsTrackerSingleton.instance.hraSummary = it!!.data!!.MedicalProfileSummary!!
                scoreListener.onScore(it.data!!.MedicalProfileSummary)

            }
        })
    }

    private fun navigateToMedicineDashboard() {
        Timber.e("Intent--->$intent")
        val launchIntent = Intent()
        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
        launchIntent.putExtra(Constants.DATE,intent.getStringExtra(Constants.DATE))
        startActivity(launchIntent)
    }

    private fun configureNavController() {
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        super.navigationController(navController)
    }

    private fun callDashboardAPIs() {
        viewModel.getLoggedInPersonDetails()
    }

    private fun configureDrawerRecyclerView() {
         viewModel.getDrawerOptionList()
         navigationDrawerListAdapter = NavigationDrawerListAdapter(viewModel, this, this)
         binding.navDrawerList.adapter = navigationDrawerListAdapter

         val versionName = Utilities.getVersionName(this)
         if (!Utilities.isNullOrEmpty(versionName)) {
             var versionText = "Powered by Vivant (v $versionName )"
             if  (Constants.environment.equals("UAT",ignoreCase = true)) {
                 versionText = "$versionText UAT"
             }
             binding.txtVersionName.text = versionText
         }
     }

    override fun onResume() {
        super.onResume()
        Timber.e("Inside onResume")
        var showProgress = false
        if ( intent.hasExtra(Constants.FROM) &&
            intent.getStringExtra(Constants.FROM).equals(Constants.NOTIFICATION_ACTION,ignoreCase = true) ) {
            showProgress = true
            backgroudCallViewModel.isBackgroundApiCall = false
        }

        if ( showProgress ) {
            navigateToMedicineDashboard()
        } else {
            backgroudCallViewModel.getAppVersionDetails(this)
            backgroudCallViewModel.checkAppUpdate.observe(this, { })
        }

        backgroudCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()
        backgroudCallViewModel.callBackgroundApiCall(showProgress)

        backgroudCallViewModel.listRelatives.observe(this, {
            if (it.data != null && it.status == Resource.Status.SUCCESS) {
                viewModel.getUserRelatives()
            }
        })

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e("HomeMainActivity : Fetching FCM registration token failed : $task.exception")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result!!
            Timber.e("\nToken=>$token")
            backgroudCallViewModel.callSaveCloudMessagingIdApi(token, true)
        })
    }

    override fun onStop() {
        super.onStop()
        backgroudCallViewModel.isBackgroundApiCall = false
        navigationDrawerListAdapter?.isProfileUpdate = false
    }

    fun refreshView() {
        drawerLayout.closeDrawers()
        backgroudCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()
        backgroudCallViewModel.isBackgroundApiCall = false
        backgroudCallViewModel.profileSwitched = true
        backgroudCallViewModel.callBackgroundApiCall(true)
        viewModel.getDrawerOptionList()
    }

    override fun onDrawerClick(item: DataHandler.NavDrawerOption) {
        drawerLayout.closeDrawers()
        when (item.id) {
            DataHandler.NavDrawer.MY_PRO -> viewModel.navigateToMyProfileActivityWithStoragePermission(this)
            DataHandler.NavDrawer.FAMILY_PRO -> viewModel.navigateToFamilyProfileActivity()
            DataHandler.NavDrawer.FAMILY_DOCTOR -> viewModel.navigateToFamilyDoctorsActivity()
            DataHandler.NavDrawer.CONTACT_US -> viewModel.navigateToContactUsActivity()
            DataHandler.NavDrawer.SPREAD_THE_WORD -> viewModel.spreadTheWord()
            DataHandler.NavDrawer.SETTINGS -> viewModel.navigateToSettingsActivity()
            //DataHandler.NavDrawer.HOME -> drawerLayout.closeDrawers()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //Required to set New Intent from Medicine Notification
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun registerListener(dashboardFragment: DashboardFragment) {
        scoreListener = dashboardFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) { }

}
