package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.home.R
import com.caressa.home.databinding.ActivityLinkHlmtAccountBinding
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class LinkHlmtAccountActivity : BaseActivity() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : ActivityLinkHlmtAccountBinding

    private val appColorHelper = AppColorHelper.instance!!
    private var isAccountLinked = false

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_link_hlmt_account)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        FirebaseHelper.logScreenEvent(FirebaseConstants.LINK_ACCOUNT_SCREEN)
        initialise()
        setupToolbar()
        setClickable()
    }

    private fun initialise() {

        viewModel.hlmt360LoginResponse.observe(this, {
            if(it!=null){
                if ( it.accountLinkStatus.equals("true",true) ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.lblAccountLink.text = Html.fromHtml(resources.getString(R.string.YOUR_HLMT_360_ACCOUNT) + " <b>${binding.edtUsername.text.toString()} </b> " + resources.getString(R.string.HAS_BEEN_LINKED_TO_PROFILE), Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        binding.lblAccountLink.text = Html.fromHtml(resources.getString(R.string.YOUR_HLMT_360_ACCOUNT) + " <b>${binding.edtUsername.text.toString()} </b> " + resources.getString(R.string.HAS_BEEN_LINKED_TO_PROFILE))
                    }
                    binding.layoutAccLinked.visibility = View.VISIBLE
                    binding.layoutAccNotLinked.visibility = View.GONE
                    binding.imgSubmit.visibility = View.GONE
                } else {
                    binding.layoutAccLinked.visibility = View.GONE
                    binding.layoutAccNotLinked.visibility = View.VISIBLE
                    binding.imgSubmit.visibility = View.VISIBLE
                }
            }
        })
        isAccountLinked = viewModel.getPreference(PreferenceConstants.ACCOUNT_LINK_STATUS).equals("true",true)
        if ( isAccountLinked ) {
            FirebaseHelper.logScreenEvent(FirebaseConstants.LINK_ACCOUNT_STATUS_SCREEN)
            val lblText:String = resources.getString(R.string.YOUR_HLMT_360_ACCOUNT) + " <b> "+viewModel.getPreference(PreferenceConstants.HLMT_USERNAME)+" </b> " + resources.getString(R.string.ACCOUNT_ALREADY_LINKED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.lblAccountLink.text = Html.fromHtml(lblText, Html.FROM_HTML_MODE_COMPACT);
            } else {
                binding.lblAccountLink.text = Html.fromHtml(lblText);
            }
            binding.layoutAccLinked.visibility = View.VISIBLE
            binding.layoutAccNotLinked.visibility = View.GONE
            binding.imgSubmit.visibility = View.GONE
        } else {
            binding.layoutAccLinked.visibility = View.GONE
            binding.layoutAccNotLinked.visibility = View.VISIBLE
            binding.imgSubmit.visibility = View.VISIBLE
        }
    }

    private fun setClickable() {

//        binding.btnLinkAccount.setOnClickListener {
//            viewModel.fetchHLMT360LoginResponse(binding.edtUsername.text.toString(),binding.edtLoginPassword.text.toString())
//        }

        binding.imgSubmit.setOnClickListener {
            viewModel.fetchHLMT360LoginResponse(binding.edtUsername.text.toString(),binding.edtLoginPassword.text.toString())
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