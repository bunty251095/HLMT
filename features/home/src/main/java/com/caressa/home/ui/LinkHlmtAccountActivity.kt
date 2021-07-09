package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.R
import com.caressa.home.databinding.ActivityLinkHlmtAccountBinding
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class LinkHlmtAccountActivity : BaseActivity() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : ActivityLinkHlmtAccountBinding

    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_link_hlmt_account)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setupToolbar()
        setClickable()
    }

    private fun setClickable() {

        binding.btnLinkAccount.setOnClickListener {

        }

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        toolbar_title.text = resources.getString(R.string.LINK_HLMT_360_ACCOUNT)
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

}