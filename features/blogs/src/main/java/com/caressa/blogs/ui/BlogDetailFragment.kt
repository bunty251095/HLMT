package com.caressa.blogs.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.caressa.blogs.databinding.FragmentBlogDetailBinding
import com.caressa.blogs.viewmodel.BlogViewModel
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BlogDetailFragment : BaseFragment() {

    private val viewModel: BlogViewModel by viewModel()
    private lateinit var binding: FragmentBlogDetailBinding

    private var body = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.BLOGS_DETAILS_SCREEN)
        initialise()
        return binding.root
    }

    private fun initialise() {
        setWebViewSettings(binding.webViewBlogs)
        body = requireArguments().getString(Constants.BODY)!!
        //Timber.i("Body----->"+body )
        if (!Utilities.isNullOrEmpty(body)) {
            val html1 = ""
            val html2 = ""
            //val html3 = "%s%s"
            val html4 = "<style>\n" +
                    " img {\n" +
                    "         width:100 %!important;\n" +
                    "         padding:1%;\n" +
                    " }\n" +
                    "</style>"
            val str = String.format(html1, "IRANSansMobile_UltraLight.ttf", 40)
            val content = body.replace("text-align:", "")
                .replace("font-family:", "")
                .replace("line-height:", "")
                .replace("dir=", "")
                //.replace(Regex("height=\"[0-9]+\""), "")
                //.replace(Regex("<img(.*?)src=\"([^\"]+)\"(.*?)>"), "<img src=\"$2\">")
                .replace(Regex("<img(.*?)src=\"([^\"]+)\"(.*?)>"),
                    "<img src=\"$2\" style=\"width:100%\">")
                .replace("Loading Custom Ratings...", "")
            //.replace("width=", "width=\"100%;\"")
            val myHtmlString = str + content + html2
            binding.webViewBlogs.loadDataWithBaseURL(
                null,
                html4 + myHtmlString,
                "text/html",
                "UTF-8",
                null)
            //binding.webViewBlogs.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null)
            //binding.webViewBlogs.loadUrl(requireArguments().getString("LINK")!!)
            Timber.i("BLOG_DETAIL-----------\n${(html4 + myHtmlString)}")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(webView: WebView) {
        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        /*settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        //settings.lightTouchEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        //settings.defaultFontSize = R.dimen._7sdp
        settings.textSize = WebSettings.TextSize.LARGEST
        //settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.userAgentString = "Android"*/

        webView.webViewClient = WebViewClient()
    }

}
