package com.caressa.blogs.views

import android.os.Build
import android.text.Html
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.caressa.blogs.R
import com.caressa.blogs.adapter.BlogAdapter
import com.caressa.model.blogs.BlogItem
import com.caressa.common.utils.Utilities
import com.squareup.picasso.Picasso

object BlogsBinding {

    @BindingAdapter("app:loadImageView")
    @JvmStatic fun AppCompatImageView.setImageViewResource( resource: Int) {
        try {
            setImageResource(resource)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:loadImgUrl")
    @JvmStatic fun AppCompatImageView.setImgUrl( imgUrl: String) {
        try {
            if ( !Utilities.isNullOrEmpty(imgUrl) ) {
                Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .resize(6000,3000)
                    .onlyScaleDown()
                    .error(R.drawable.image_placeholder)
                    .into(this)
            } else {
                setImageResource(R.drawable.image_placeholder)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:htmlTxt")
    @JvmStatic fun AppCompatTextView.setHtmlTxt( html: String) {
        try {
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:blogItems")
    @JvmStatic fun setBlogItem(recyclerView: RecyclerView, list: List<BlogItem>?){
       // Timber.i("BlogItemList=====> "+list)
        with(recyclerView.adapter as BlogAdapter){
            list?.let { updateData(it) }
        }
    }

}