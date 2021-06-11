package com.caressa.hra.views

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class HTMLTextView : AppCompatTextView {

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        text = Html.fromHtml(text.toString())
    }

    fun setHtmlText(text: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT))
        } else {
            setText(Html.fromHtml(text))
        }
    }

    fun setHtmlTextFromId(textId: Int?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(context.resources.getString(textId!!), Html.FROM_HTML_MODE_LEGACY)
        } else {
            text = Html.fromHtml(context.resources.getString(textId!!))
        }
    }
}