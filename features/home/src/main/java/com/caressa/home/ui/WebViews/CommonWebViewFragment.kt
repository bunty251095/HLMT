package com.caressa.home.ui.WebViews

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseViewModel
import com.caressa.common.base.BaseWebViewFragment
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AlertDialogHelper
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.EncryptionUtility
import com.caressa.common.utils.RealPathUtil
import com.caressa.home.databinding.FragmentCommonWebViewBinding
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.activity_common_web_view.*
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class CommonWebViewFragment : BaseWebViewFragment() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : FragmentCommonWebViewBinding

    private var strWebUrl = ""
    private var hasCookies : Boolean = false
    private var encodedString = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCommonWebViewBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        //setClickable()
        return binding.root
    }

    private fun initialise() {
        strWebUrl = requireArguments().getString(Constants.WEB_URL)!!
        hasCookies = requireArguments().getBoolean(Constants.HAS_COOKIES)
        val webView = binding.webView
        val loaderImgView = binding.imgLoading

        if ( strWebUrl == Constants.strApolloHtml ) {
            binding.btnMedicalStoreDetails.visibility = View.VISIBLE
        } else {
            binding.btnMedicalStoreDetails.visibility = View.GONE
        }

        setWEB_URL(strWebUrl)
        setAsw_view(webView)
        setLoadingImageView(loaderImgView)

        if ( hasCookies ) {
            viewModel.getLoggedInPersonDetails( )
            viewModel.userDetails.observe( viewLifecycleOwner , {
                if ( it != null ) {
                    val userDetails = it
                    try {
                        val dob = DateHelper.getDateTimeAs_ddMMMyyyy(userDetails.dateOfBirth)
                        val userSessionDetailsObject = JSONObject()
                        userSessionDetailsObject.put("FN", userDetails.firstName.trim { it <= ' ' })
                        userSessionDetailsObject.put("C", userDetails.phoneNumber)
                        userSessionDetailsObject.put("G", if (userDetails.gender == "1") "Male" else "Female")
                        userSessionDetailsObject.put("DOB", dob)
                        userSessionDetailsObject.put("E", userDetails.emailAddress)
                        userSessionDetailsObject.put("A", userDetails.accountId.toDouble().toInt().toString())
                        //userSessionDetailsObject.put("AST", null)
                       // userSessionDetailsObject.put("ACT", null)
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
            })
        }
    }

}
