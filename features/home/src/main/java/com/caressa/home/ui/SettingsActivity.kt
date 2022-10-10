package com.caressa.home.ui

import android.app.AlertDialog
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
import com.caressa.home.R
import com.caressa.home.adapter.OptionSettingsAdapter
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivitySettingsBinding
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import android.content.DialogInterface
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*


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
        FirebaseHelper.logScreenEvent(FirebaseConstants.SETTINGS_SCREEN)
        setupToolbar()
        initialise()
    }

    private fun initialise() {
        Timber.i("Language=> "+LocaleHelper.getLanguage(this))
        if(LocaleHelper.getLanguage(this).equals("ms",true)) {
            viewModel.getSettingsOptionList1("Melayu")
        }else{
            viewModel.getSettingsOptionList1("English")
        }
        optionSettingsAdapter = OptionSettingsAdapter(viewModel, baseContext,this)
        binding.rvOptions.adapter = optionSettingsAdapter
    }

    override fun onSettingsOptionListener(position: Int, option: DataHandler.Option) {
        Timber.e("SelectedPosition=>$position")
        when (option.code) {
            "LANGUAGE" -> {
                //showLanguageDialog()
                val intent = Intent(this,LanguageActivity::class.java)
                intent.putExtra(Constants.FROM, "")
                startActivity(intent)
            }
            "RATE_US" -> {
                DataHandler(this).goToPlayStore(this)
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.RATE_US_CLICK)
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
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.LOGOUT_CLICK)
            }
            "CHANGE_PASSWORD" -> {
                val intent = Intent()
                intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.PASSWORD_CHANGE)
                startActivity(intent)
            }
        }
    }

    private fun showLanguageDialog() {
        val items = arrayOf<String>("English", "Melayu")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.LANGUAGE))
        builder.setItems(items) { dialog, item -> // Do something with the selection
            viewModel.getSettingsOptionList1(items[item])
            if (items[item].equals("malay", true)) {
                LocaleHelper.setLocale(this@SettingsActivity, "ms")
            } else {
                LocaleHelper.setLocale(this@SettingsActivity, "en")
            }
            recreate()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
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
            LocaleHelper.setLocale(this, "en")
            DataHandler(this).logout(backGroudCallViewModel,this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

}