package com.caressa.blogs.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.caressa.blogs.R
import com.caressa.common.base.BaseViewModel
import com.caressa.repository.AppDispatchers
import com.caressa.blogs.domain.BlogsManagementUseCase
import com.caressa.model.blogs.BlogItem
import com.caressa.blogs.ui.BlogDashboardFragment
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.FirebaseHelper
import com.caressa.model.blogs.BlogModel
import com.caressa.repository.utils.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import timber.log.Timber
import java.util.*
import org.jsoup.select.Elements

class BlogViewModel (private val dispatchers: AppDispatchers,
                     private val blogManagementUseCase : BlogsManagementUseCase,
                     private val context: Context) : BaseViewModel() {

    var healthBlogList = MutableLiveData<List<BlogItem>>()

    private var blogListSource: LiveData<Resource<List<BlogModel.Blog>>> = MutableLiveData()
    private val _blogList = MediatorLiveData<List<BlogModel.Blog>>()
    val blogList: LiveData<List<BlogModel.Blog>> get() = _blogList

    @SuppressLint("BinaryOperationInTimber")
    fun callGetBlogsFromServerApi(VisibleThreshold : Int, CurrentPage : Int, fragment: BlogDashboardFragment ) = viewModelScope.launch(dispatchers.main) {

        val blogProxyURL = Constants.strBlogsProxyURL
        val blogURL = "$blogProxyURL$VisibleThreshold&offset=$CurrentPage"

        //_progressBar.value = Event("Loading Health Blog...")
        _blogList.removeSource(blogListSource)
        withContext(dispatchers.io) {
            blogListSource = blogManagementUseCase.invokeDownloadBlog(isForceRefresh = true, data = blogURL)
        }
        _blogList.addSource(blogListSource) {
            _blogList.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                fragment.stopShimmer()
                if (it.data != null) {
                    Timber.i("Blog_Response--->"+ it.data!!)
                    extractBlogContent(it.data!!)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                fragment.stopShimmer()
                toastMessage(it.errorMessage)
            }
        }

    }

    private fun extractBlogContent(list : List<BlogModel.Blog> ) {
        val blogList: ArrayList<BlogItem> = ArrayList()
        for ( blog in  list) {
            val link = blog.link
            val id = blog.id.toString()
            val date =  DateHelper.getDateTimeAs_ddMMMyyyyNew(blog.date)
            val renderedTitle = blog.title.rendered
            val renderedContent = blog.content.rendered
            val renderedExcerpt = blog.excerpt.rendered
            Timber.i("Title--->$renderedTitle")
            Timber.i("BlogLink--->$link")
            val documentExcerpt : Document = Jsoup.parse(renderedExcerpt)
            val pExcerpt : Elements = documentExcerpt.getElementsByTag("p")
            var strDescription = ""
            for (x : Element in pExcerpt) {
                strDescription += x.text()
            }
            Timber.i("Description--->$strDescription")
            val documentContent : Document = Jsoup.parse(renderedContent)
            val pContent : Elements  = documentContent.getElementsByTag("p")
            var strBody = ""
            for (x in pContent) {
                strBody += x.text()
            }
            val elementsByClassImage : Elements  = documentContent.getElementsByTag("img")
            val strImgSrc = elementsByClassImage.attr("src")
            Timber.i("ImgUrl--->$strImgSrc")

            val blogItem = BlogItem(
                id = id ,
                title = renderedTitle ,
                description = strDescription ,
                date = date ,
                image = strImgSrc ,
                link = link,
                body = renderedContent)
            if ( !blogList.contains(blogItem) ) {
                blogList.add(blogItem)
            }
        }
        healthBlogList.postValue( blogList )
    }

    fun shareBlog( blog: BlogItem) {
        val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(blog.title, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(blog.title).toString()
        }
        val desc = blog.description!!.substring(0,70) + ".....Continue reading at,"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n" + blog.link)
/*        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + desc + "\n"
                + context.resources.getString(R.string.blog_link))*/
        sendIntent.type = "text/plain"
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(sendIntent)
        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.BLOGS_SHARE_CLICK)
    }

    fun viewBlog( view:View,blog: BlogItem) {
        val bundle = Bundle()
        bundle.putString(Constants.TITLE,blog.title)
        bundle.putString(Constants.BODY,blog.body)
        bundle.putString("LINK",blog.link)
        view.findNavController().navigate(R.id.action_blogsDashboardFragment_to_blogDetailFragment , bundle )
    }

}