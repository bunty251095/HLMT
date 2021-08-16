package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.caressa.home.databinding.ActivityReferAndEarnBinding
import com.caressa.home.viewmodel.DashboardViewModel
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_refer_and_earn.*
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ReferAndEarnActivity : BaseActivity() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : ActivityReferAndEarnBinding

    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refer_and_earn)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initialise()
        setupToolbar()
        setClickable()
    }

    private fun initialise() {
        createDynamicLink()
    }

    private fun setClickable() {
        binding.iconShare.setOnClickListener {
            shareReferral()
        }
    }

    private fun shareReferral() {
        viewModel.inviteUser(txtLink.text.toString())
    }

    private fun createDynamicLink() {
        val uid = 123456
        val invitationLink = "https://hlpace.com/?invitedby=$uid"

        var mInvitationUrl: Uri? = Uri.parse("https://hlmt.page.link")
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(invitationLink)
            domainUriPrefix = "https://hlmt.page.link"
            androidParameters(NavigationConstants.APPID) {
                minimumVersion = 1
            }
            iosParameters(NavigationConstants.APPID) {
            }
        }.addOnSuccessListener { shortDynamicLink ->
            mInvitationUrl = shortDynamicLink.shortLink
            Timber.i("Invitation URL => "+mInvitationUrl)
            binding.txtLink.text = mInvitationUrl.toString()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        toolbar_title.text = resources.getString(R.string.REFER_AND_EARN)
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