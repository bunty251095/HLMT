package com.caressa.common.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.ProgressBar
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcProgressBar : ProgressBar {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleImage(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        styleImage(context, attrs)
    }

    private fun styleImage(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.PRIMARY_COLOR)) {
                    //setBackgroundColor(Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR)));
                    progressDrawable.setColorFilter(Color.parseColor(Constants.PRIMARY_COLOR),PorterDuff.Mode.SRC_IN)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}