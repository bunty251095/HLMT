package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.showDialog
import com.caressa.home.R
import com.caressa.home.adapter.OptionSettingsAdapter
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivitySettingsBinding
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SettingsActivity : BaseActivity() , OptionSettingsAdapter.SettingsOptionListener,
    DefaultNotificationDialog.OnDialogValueListener{

    private val viewModel : DashboardViewModel by viewModel()
    private val backGroudCallViewModel: BackgroundCallViewModel by viewModel()
    private lateinit var binding : ActivitySettingsBinding

    private var optionSettingsAdapter: OptionSettingsAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setupToolbar()
        initialise()
    }

    private fun initialise() {
        FirebaseHelper.logScreenEvent(FirebaseConstants.SETTINGS_SCREEN)
        viewModel.getSettingsOptionList1()
        optionSettingsAdapter = OptionSettingsAdapter(viewModel, this,this)
        binding.rvOptions.adapter = optionSettingsAdapter
    }

    override fun onSettingsOptionListener(position: Int, option: DataHandler.Option) {
        Timber.e("SelectedPosition=>$position")
        when (option.code) {
            "RATE_US" -> {
                DataHandler(this).goToPlayStore(this)
            }
            "FEEDBACK" -> {
                val intent = Intent()
                intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FEEDBACK)
                startActivity(intent)
            }
            "LOGOUT" -> {
                showDialog(listener = this,
                    title = resources.getString(R.string.LOGOUT),
                    message = resources.getString(R.string.MSG_LOGOUT_CONFORMATION),
                    leftText = resources.getString(R.string.NO),
                    rightText = resources.getString(R.string.YES))
            }
            "CHANGE_PASSWORD" -> {
                val intent = Intent()
                intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.PASSWORD_CHANGE)
                startActivity(intent)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        toolbar_title.text = resources.getString(R.string.TITLE_SETTINGS)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)

        toolbar_home.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)

        toolbar_home.setNavigationOnClickListener {
            onBackPressed()
        }

        img_vivant_logo.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            DataHandler(this).logout(backGroudCallViewModel,this)
        }
    }

}