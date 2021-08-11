package com.caressa.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcImageView : AppCompatImageView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleImage(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        styleImage(context, attrs)
    }

    private fun styleImage(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        val isBackground = a.getBoolean(R.styleable.CustomTemplate_isBackground, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.ICON_TINT_COLOR)) {
                    if (isBackground) {
/*                        background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.parseColor(templateJSON.getString(Constants.ICON_TINT_COLOR)), BlendModeCompat.SRC_ATOP)*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            backgroundTintList = ColorStateList.valueOf(
                                Color.parseColor(
                                    templateJSON.getString(Constants.ICON_TINT_COLOR)
                                )
                            )
                        }

                    } else {
                        setColorFilter(Color.parseColor(templateJSON.getString(Constants.ICON_TINT_COLOR)))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}