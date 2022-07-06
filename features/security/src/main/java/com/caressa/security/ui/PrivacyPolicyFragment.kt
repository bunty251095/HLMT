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
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.security.R
import com.caressa.security.databinding.FragmentTermsAndConditionBinding
import com.caressa.security.viewmodel.TermsAndConditionViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class PrivacyPolicyFragment : BaseFragment() {

    private val viewModel: TermsAndConditionViewModel by viewModel()
    private lateinit var binding: FragmentTermsAndConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.webViewTerms.loadUrl(Constants.strAPIUrl + "/privacy%20policy.html")
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
