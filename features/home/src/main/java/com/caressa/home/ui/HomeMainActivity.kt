package com.caressa.home.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.*
import com.caressa.common.utils.PermissionUtil.AppPermissionListener
import com.caressa.home.R
import com.caressa.home.adapter.NavigationDrawerListAdapter
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivityHomeMainBinding
import com.caressa.home.di.ScoreListener
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.singleton.ToolsTrackerSingleton
import com.caressa.model.tempconst.Configuration
import com.caressa.repository.utils.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.app_bar_home_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeMainActivity : BaseActivity(), NavigationDrawerListAdapter.DrawerClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: DashboardViewModel by viewModel()
    private val backGroundCallViewModel: BackgroundCallViewModel by viewModel()
    private lateinit var binding : ActivityHomeMainBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var context : Context?= null
    private var activity : Activity?= null
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var navigationDrawerListAdapter : NavigationDrawerListAdapter? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var scoreListener:ScoreListener
    private var googleAccountListener: OnGoogleAccountSelectListener? = null

    private val permissionListener = object : AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Timber.e("$isGranted")
            if ( isGranted ) {
                viewModel.navigateToMyProfileActivity()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = backGroundCallViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        binding.viewModel = viewModel
        binding.backViewModel = backGroundCallViewModel
        binding.lifecycleOwner = this
        context = this
        activity = this

        Configuration.EntityID = viewModel.getMainUserPersonID()
        Configuration.LanguageCode = LocaleHelper.getLanguage(this)

        setSupportActionBar(toolbar)
        configureNavController()
        callDashboardAPIs()
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.NAVIGATION_DRAWER_OPEN, R.string.NAVIGATION_DRAWER_CLOSE)
        toggle.drawerArrowDrawable.color = appColorHelper.primaryColor()
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        configureDrawerRecyclerView()

        Timber.i("HOME onCreate")

        backGroundCallViewModel.saveCloudMessagingId.observe(this) {}
        backGroundCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()

        // This observer is written because In background thread we are listening .
        backGroundCallViewModel.medicalProfileSummary.observe(this) {
            Timber.i("backGroundCallViewModel.medicalProfileSummary")
            if (it.data != null && it.status == Resource.Status.SUCCESS) {
//                Timber.i("Summery :: "+it.data?.MedicalProfileSummary)
//                viewModel.hraDetails.value = it.data?.MedicalProfileSummary
//                viewModel.hraDetails.postValue(it.data?.MedicalProfileSummary)
//                viewModel.getHraDetails()
                ToolsTrackerSingleton.instance.hraSummary = it!!.data!!.MedicalProfileSummary!!
                scoreListener.onScore(it.data!!.MedicalProfileSummary)

            }
        }
        // Mayuresh: This observer is written for getting parameter data in dashboard screen
        backGroundCallViewModel.labRecordList.observe(this) {}
        backGroundCallViewModel.labParameterList.observe(this) {
            scoreListener.onVitalDataUpdateListener(it)

        }
        backGroundCallViewModel.getStepsGoal.observe(this) {
            //Timber.i("Steps=> " + it)
            if (it != null && it.latestGoal != null && it.latestGoal.goal != null) {
                scoreListener.onStepGoalReceived(it.latestGoal.goal)
            }
        }
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
             var versionText = "${resources.getString(R.string.POWERED_BY)} Vivant (v $versionName )"
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
            backGroundCallViewModel.isBackgroundApiCall = false
        }

        if ( showProgress ) {
            navigateToMedicineDashboard()
        } else {
            backGroundCallViewModel.getAppVersionDetails(this)
            backGroundCallViewModel.checkAppUpdate.observe(this) { }
        }

        backGroundCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()
        backGroundCallViewModel.callBackgroundApiCall(showProgress)

        backGroundCallViewModel.listRelatives.observe(this) {
            if (it.data != null && it.status == Resource.Status.SUCCESS) {
                viewModel.getUserRelatives()
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e("HomeMainActivity : Fetching FCM registration token failed : $task.exception")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result!!
            Timber.e("\nToken=>$token")
            backGroundCallViewModel.callSaveCloudMessagingIdApi(token, true)
        })
    }

    override fun onStop() {
        super.onStop()
        backGroundCallViewModel.isBackgroundApiCall = false
        navigationDrawerListAdapter?.isProfileUpdate = false
    }

    fun refreshView() {
        drawerLayout.closeDrawers()
        backGroundCallViewModel.refreshPersonId()
        viewModel.refreshPersonId()
        backGroundCallViewModel.isBackgroundApiCall = false
        backGroundCallViewModel.profileSwitched = true
        backGroundCallViewModel.callBackgroundApiCall(true)
        configureDrawerRecyclerView()
        viewModel.getDrawerOptionList()
    }

    override fun onDrawerClick(item: DataHandler.NavDrawerOption) {
        drawerLayout.closeDrawers()
        when (item.id) {
            DataHandler.NavDrawer.MY_PRO -> viewModel.navigateToMyProfileActivityWithStoragePermission(this,permissionListener)
//            DataHandler.NavDrawer.LINK -> Utilities.toastMessageShort(this,"Coming Soon..")
            DataHandler.NavDrawer.LINK -> viewModel.navigateToLinkAccountActivity()
            DataHandler.NavDrawer.FAMILY_PRO -> viewModel.navigateToFamilyProfileActivity()
            DataHandler.NavDrawer.FAMILY_DOCTOR -> viewModel.navigateToFamilyDoctorsActivity()
            DataHandler.NavDrawer.CONTACT_US -> viewModel.navigateToContactUsActivity()
            DataHandler.NavDrawer.SPREAD_THE_WORD -> viewModel.spreadTheWord()
            DataHandler.NavDrawer.SETTINGS -> viewModel.navigateToSettingsActivity()
            DataHandler.NavDrawer.REFER_AND_EARN -> viewModel.navigateToReferrerActivity()
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

//    fun registerListener(dashboardFragment: DashboardFragment) {
//        scoreListener = dashboardFragment
//    }

    fun registerListener(dashboardFragment: ScoreListener) {
        scoreListener = dashboardFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) { }

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

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val per = Environment.isExternalStorageManager()
                Timber.e("requestCode---> $requestCode")
                Timber.e("permissionGranted---> $per")
                when(requestCode) {
                    Constants.REQ_CODE_STORAGE -> {
                        if (per) {
                            permissionListener.isPermissionGranted(true)
                        } else {
                            Utilities.toastMessageShort(this,resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

}
