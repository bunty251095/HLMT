package com.caressa.common.view

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.caressa.common.utils.Utilities

object CommonBinding {

    @BindingAdapter("android:loadImage")
    @JvmStatic
    fun setImageView(imageView: AppCompatImageView, resource: Int?) {
        try {
            imageView.setImageResource(resource!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:NotNullEmptyText")
    @JvmStatic
    fun setNotNullEmptyText(textView: AppCompatTextView, text: String?) {
        try {
            if (!Utilities.isNullOrEmpty(text)) {
                textView.text = text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:NotNullEmptyZeroText")
    @JvmStatic
    fun setNotNullEmptyZeroText(textView: AppCompatTextView, text: String?) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(text)) {
                textView.text = text
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}