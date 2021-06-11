package com.caressa.home.ui.WebViews

import android.content.ComponentName
import android.content.Intent
import com.caressa.home.R
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.caressa.common.base.BaseViewModel
import com.caressa.common.base.BaseWebViewActivity
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.EncryptionUtility
import androidx.lifecycle.Observer
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.viewmodel.WebViewViewModel
import kotlinx.android.synthetic.main.activity_common_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class CommonWebViewActivity : BaseWebViewActivity() {

    private val viewModel : WebViewViewModel by viewModel()
    //private lateinit var binding : ActivityCommonWebViewBinding

    private var strWebUrl = ""
    private var hasCookies : Boolean = false
    private var encodedString = ""
    private var toolbarTitle = ""
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_web_view)
/*    binding = DataBindingUtil.setContentView(this, R.layout.activity_common_web_view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this*/
        //initBase()
        initialise()
        setupToolbar()
        initBase()
        setClickable()
    }

    private fun initialise() {
        toolbarTitle = intent.getStringExtra(Constants.Toobar_Title)!!
        strWebUrl = intent.getStringExtra(Constants.WEB_URL)!!
        hasCookies = intent.getBooleanExtra(Constants.HAS_COOKIES , false)
        Timber.i("WebUrl , HasCookies -----> $strWebUrl , $hasCookies , $toolbarTitle")
        val webView = webView_home
        val loaderImgView = img_loader
/*        val webView = binding.webViewHome
        val loaderImgView = binding.imgLoader*/
        if ( strWebUrl == Constants.strApolloHtml ) {
            btn_medicalStoreDetails.visibility = View.VISIBLE
        } else {
            btn_medicalStoreDetails.visibility = View.GONE
        }
        setWEB_URL(strWebUrl)
        setAsw_view(webView)
        setLoadingImageView(loaderImgView)

        if ( hasCookies ) {
            viewModel.getLoggedInPersonDetails( )
            viewModel.userDetails.observe( this , Observer {
                if ( it != null ) {
                    val userDetails = it
                    try {
                        val dob = DateHelper.getDateTimeAs_ddMMMyyyy(userDetails.dateOfBirth)
                        val userSessionDetailsObject = JSONObject()
                        userSessionDetailsObject.put("FN", userDetails.firstName.trim { fn -> fn <= ' ' })
                        userSessionDetailsObject.put("C", userDetails.phoneNumber)
                        userSessionDetailsObject.put("G", if (userDetails.gender == "1") "Male" else "Female")
                        userSessionDetailsObject.put("DOB", dob)
                        userSessionDetailsObject.put("E", userDetails.emailAddress)
                        userSessionDetailsObject.put("A", userDetails.accountId.toDouble().toInt().toString())
                        userSessionDetailsObject.put("AST", userDetails.accountStatus)
                        userSessionDetailsObject.put("ACT", userDetails.accountType)
                        userSessionDetailsObject.put("PCD", userDetails.partnerCode)
                        userSessionDetailsObject.put("isMobile", true)
                        userSessionDetailsObject.put("SKey", userDetails.authToken)
                        Timber.i("Before Encryption-----> $userSessionDetailsObject")
                        encodedString = EncryptionUtility.encrypt(Configuration.SecurityKey, userSessionDetailsObject.toString(), Configuration.SecurityKey)
                    } catch ( e : Exception ) {
                        e.printStackTrace()
                    }
                    val cookieString = "UserDetails=$encodedString"
                    setCOOKIE_STRING(cookieString)
                }
            } )
        }
    }

    private fun setClickable() {
        btn_medicalStoreDetails.setOnClickListener {
            //val file = File("file:///android_asset/apollo/ApolloStoreListwithPincodes.xlsx")
/*            val path = File(RealPathUtil.getRecordFolderLocation())
            RealPathUtil.copyAndOpenAssetFile(this, "apollo", path)*/
            //val path = Environment.getExternalStorageDirectory().toString()
            val path = getExternalFilesDir(null).toString()
            Timber.i("Path---->$path")
            viewModel.copyAndOpenAssetFile(this, "apollo", path)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_webview)
        toolbar_title_webview.text = toolbarTitle
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolbar_webview.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)

        toolbar_webview.setNavigationOnClickListener {
            onBackPressed()
        }

        img_vivant_logo_white.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
        }
    }

}
