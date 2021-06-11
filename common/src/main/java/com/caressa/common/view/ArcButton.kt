package com.caressa.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration
import com.caressa.common.constants.Constants
import org.json.JSONObject
import timber.log.Timber

class ArcButton : AppCompatButton {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleButton(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        styleButton(context, attrs)
    }

    private fun styleButton(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        val isLeftButton = a.getBoolean(R.styleable.CustomTemplate_isLeftButton, false)
        if (isTemplate) {
            //Timber.tag("ClientConfig=> ").d(ClientConfiguration.getAppTemplateConfig())
            try {
                val templateJSON = JSONObject(ClientConfiguration.getAppTemplateConfig())
                if (templateJSON.has(Constants.LEFT_BUTTON_COLOR)) {
                    if (isLeftButton) {
                        setTextColor(Color.parseColor(templateJSON.getString(Constants.LEFT_BUTTON_TEXT_COLOR)))
                        //background.setColorFilter(Color.parseColor(templateJSON.getString(Constants.LEFT_BUTTON_COLOR)),PorterDuff.Mode.MULTIPLY)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            backgroundTintList = ColorStateList.valueOf(Color.parseColor(templateJSON.getString(Constants.LEFT_BUTTON_COLOR)))
                        }
                    } else {
                        setTextColor(Color.parseColor(templateJSON.getString(Constants.RIGHT_BUTTON_TEXT_COLOR)))
                        //background.setColorFilter(Color.parseColor(templateJSON.getString(Constants.RIGHT_BUTTON_COLOR)),PorterDuff.Mode.MULTIPLY)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            backgroundTintList = ColorStateList.valueOf(Color.parseColor(templateJSON.getString(Constants.RIGHT_BUTTON_COLOR)))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}