package com.caressa.security.view

import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.caressa.repository.utils.Resource
import timber.log.Timber

object TermsAndConditionBinding {

    @BindingAdapter("app:showLoadingImage")
    @JvmStatic
    fun <T>showLoadingImage(view: AppCompatImageView, resource: Resource<T>?) {
        Timber.d("Resource: $resource")
        if (resource != null)
        {
            if(resource.status == Resource.Status.LOADING)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }
    }

    @BindingAdapter("app:showWhenLoading")
    @JvmStatic
    fun showWhenLoading(view: View, status: Resource.Status?) {
        Log.d(TermsAndConditionBinding::class.java.simpleName, "Status: $status")

            if (status != null)
            {
                if(status == Resource.Status.LOADING)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
    }

    /*@BindingAdapter("app:items")
    @JvmStatic fun setItems(recyclerView: RecyclerView, resource: Resource<List<User>>?) {
        with(recyclerView.adapter as HomeAdapter) {
            resource?.data?.let { updateData(it) }
        }
    }

    @BindingAdapter("app:imageUrl")
    @JvmStatic fun loadImage(view: ImageView, url: String) {
        Glide.with(view.context).load(url).apply(RequestOptions.circleCropTransform()).into(view)
    }

    @BindingAdapter("app:showWhenEmptyList")
    @JvmStatic fun showMessageErrorWhenEmptyList(view: View, resource: Resource<List<User>>?) {
        if (resource!=null) {
            view.visibility = if (resource.status == Resource.Status.ERROR
                && resource.data != null
                && resource.data!!.isEmpty()) View.VISIBLE else View.GONE
        }
    }*/
}