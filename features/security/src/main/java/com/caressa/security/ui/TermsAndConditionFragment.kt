package com.caressa.security.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import com.caressa.security.R
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.security.viewmodel.TermsAndConditionViewModel
import com.caressa.security.databinding.FragmentTermsAndConditionBinding
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class TermsAndConditionFragment : BaseFragment() {

    private val viewModel : TermsAndConditionViewModel by viewModel()
    private lateinit var binding: FragmentTermsAndConditionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTermsAndConditionBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialiseWebView()
        return binding.root
    }

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetJavaScriptEnabled")
    private fun initialiseWebView() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward)
        binding.imgLoading.startAnimation(animation)
        binding.imgLoading.visibility = View.VISIBLE

        binding.webViewTerms.webViewClient = CustomWebViewClient()
        val webSettings = binding.webViewTerms.settings
        webSettings.javaScriptEnabled = true
        binding.webViewTerms.loadUrl("https://hlmtcoreuat.vivant.me/HL%20Pace%20App%20Terms%20&%20Conditions.html")

//        viewModel.getTermsAndConditionsData(true)

//        viewModel.termsConditions.observe(viewLifecycleOwner, {
//            if( it != null ) {
//                Timber.i("TermsConditionResponce ------>${it.termsConditions?.description}")
//                binding.webViewTerms.loadUrl("https://hlmtcoreuat.vivant.me/HL%20Pace%20App%20Terms%20&%20Conditions.html")
//            binding.webViewTerms.loadData(it.termsConditions?.description?: "https://portal.vivant.me/terms-and-conditions",
//                "text/html", "UTF-8")
//                binding.webViewTerms.loadDataWithBaseURL("https://hlmtcoreuat.vivant.me/HL", it.termsConditions?.description!!, "text/html", "UTF-8", null)
//            }
//        })
    }

    inner class CustomWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            when {
                url.startsWith("tel:") -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                url.startsWith("mailto:") -> {
                    var mUrl = url
                    mUrl = mUrl.substring(7)
                    val mail = Intent(Intent.ACTION_SEND)
                    mail.type = "application/octet-stream"
                    mail.putExtra(Intent.EXTRA_EMAIL, arrayOf(mUrl))
                    startActivity(mail)
                    return true
                }
                else -> {
                    view.loadUrl(url)
                }
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            binding.imgLoading.clearAnimation()
            binding.imgLoading.visibility = View.GONE
        }

    }

}
