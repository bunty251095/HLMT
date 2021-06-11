package com.caressa.common.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcConstraintLayout : ConstraintLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        styleLayout(context, attrs)
    }

    private fun styleLayout(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.PRIMARY_COLOR)) {
                    //setBackgroundColor(Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR)))
                    background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR)), BlendModeCompat.SRC_ATOP)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}