package com.caressa.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleText(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        styleText(context, attrs)
    }

    private fun styleText(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        val isPrimary = a.getBoolean(R.styleable.CustomTemplate_isPrimary, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.TEXT_COLOR)) {
                    setTextColor(Color.parseColor(templateJSON.getString(Constants.TEXT_COLOR)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (isPrimary) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.PRIMARY_COLOR)) {
                    setTextColor(Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}