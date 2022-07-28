package com.caressa.home.ui.WebViews

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.caressa.home.R
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.caressa.common.constants.Constants
import kotlinx.android.synthetic.main.activity_order_medicine.*
import kotlinx.android.synthetic.main.toolbar_home.*
import androidx.lifecycle.Observer
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.EncryptionUtility
import com.caressa.common.utils.Utilities
import com.caressa.home.viewmodel.WebViewViewModel
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class OrderMedicineActivity : AppCompatActivity() {

    private val viewModel : WebViewViewModel by viewModel()

    private var from = ""
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_medicine)
        if ( intent.hasExtra(Constants.FROM) ) {
            from = intent.getStringExtra(Constants.FROM)!!
        }
        Timber.e("from----->$from")
        setupToolbar()
        setClickable()
    }

     fun setClickable() {

         viewModel.getLoggedInPersonDetails( )
         layout_apollo.setOnClickListener {
            launchCommonWebViewActivity()
         }

        layout_medlife.setOnClickListener {
            launchMedLifeWebView( )
        }

    }

    private fun launchCommonWebViewActivity() {
        val intentToPass = Intent( this , CommonWebViewActivity::class.java )
        intentToPass.putExtra(Constants.Toobar_Title , Constants.TOOLBAR_APOLLO )
        intentToPass.putExtra(Constants.WEB_URL , Constants.strApolloHtml )
        intentToPass.putExtra(Constants.HAS_COOKIES , false )
        startActivity(intentToPass)
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun launchMedLifeWebView( ) {
        viewModel.userDetails.observe( this , Observer {
            if ( it != null ) {
                val userDetail = it
                try {
                    var strPrimaryContactNo = ""
                    var strEmailAddress = ""

                    val data = JSONObject()
                    data.put("Session_Key", userDetail.authToken)
                    data.put("PC", "RIMEDO20")
                    data.put("Corp", "RIMEDO20")
                    data.put("PersonID", userDetail.personId.toDouble().toInt().toString())
                    data.put("AccountID", userDetail.accountId.toDouble().toInt().toString())
                    data.put("logoURL", "https://vivant.me/wp-content/uploads/2018/07/vivant-1.png")
                    data.put("webHookURL", "https://aalizwell.in/integrations/medlifewebhook.aspx")
                    data.put("Name", userDetail.firstName)
                    if (!Utilities.isNullOrEmpty(userDetail.emailAddress)) {
                        data.put("Email", userDetail.emailAddress)
                        strEmailAddress = userDetail.emailAddress
                    } else {
                        data.put("Email", strEmailAddress)
                    }

                    if (!Utilities.isNullOrEmpty(userDetail.phoneNumber)) {
                        data.put("Phone", userDetail.phoneNumber)
                        strPrimaryContactNo = userDetail.phoneNumber
                    } else {
                        data.put("Phone", strPrimaryContactNo)
                    }
                    if (Utilities.isNullOrEmpty(strEmailAddress) || Utilities.isNullOrEmpty(strPrimaryContactNo)) {
                        val bundle = Bundle()
                        bundle.putString("EmailAddress",strEmailAddress)
                        bundle.putString("PrimaryContactNo", strPrimaryContactNo)
                       // RoutingClass.routeToFragment(AppAH.getCurrentActivity(), GlobalConstants.WellnessUpdateInfo, bundle)
                    } else {
                        Timber.i("MedLife : Data JSON-----> $data")
                        val encodedString = EncryptionUtility.encrypt(Configuration.SecurityKey, data.toString(), Configuration.SecurityKey)
                        if (!Utilities.isNullOrEmpty(encodedString)) {
                            val url = "https://phr.allizhealth.com/pages/Handshake/MdBroker.aspx?q=M&PC=" + userDetail.partnerCode + "&d="
                            val medlifeURL = url + encodedString
                            Timber.i("MedLifeURL-----> "+ Uri.parse(medlifeURL))
                            val intentToPass = Intent( this , CommonWebViewActivity::class.java )
                            intentToPass.putExtra(Constants.Toobar_Title ,Constants.TOOLBAR_MEDLIFE)
                            intentToPass.putExtra(Constants.WEB_URL , medlifeURL  )
                            intentToPass.putExtra(Constants.HAS_COOKIES , true )
                            startActivity(intentToPass)
                        }
/*                        else {
                            DialogHelper.showAlertDialog(AppAH.getCurrentActivity(), "MedLife", "Seems you are not enrolled with MedLife", 1)
                        }*/
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        //toolbar_title.text = resources.getString(R.string.DASH_ORDER_YOUR_MEDICINES)
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

    override fun onBackPressed() {
        if ( from.equals(Constants.MEDICATION,ignoreCase = true) ) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

}
