package com.caressa.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcBorderButton : AppCompatButton {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleButton(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        styleButton(context, attrs)
    }

    private fun styleButton(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        val isRound = a.getBoolean(R.styleable.CustomTemplate_isRound, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                val selectionColor =
                    Color.parseColor(templateJSON.getString(Constants.SELECTION_COLOR))

                val textColorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_selected),
                        intArrayOf(android.R.attr.state_pressed),
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_enabled),
                        intArrayOf(-android.R.attr.state_enabled)
                    ),
                    intArrayOf(
                        Color.WHITE,
                        Color.WHITE,
                        Color.WHITE,
                        selectionColor,
                        selectionColor
                    )
                )
                setTextColor(textColorStateList)

                val disableDrawable = GradientDrawable()
                disableDrawable.shape = GradientDrawable.RECTANGLE
                disableDrawable.setStroke(4, selectionColor)
                disableDrawable.setColor(Color.WHITE)

                val enableDrawable = GradientDrawable()
                enableDrawable.shape = GradientDrawable.RECTANGLE
                enableDrawable.setStroke(4, selectionColor)
                enableDrawable.setColor(selectionColor)

                if (isRound) {
                    disableDrawable.cornerRadius = R.dimen._1sdp.toFloat()
                    enableDrawable.cornerRadius = R.dimen._1sdp.toFloat()
                } else {
                    disableDrawable.cornerRadius = 5f
                    enableDrawable.cornerRadius = 5f
                }

                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(
                    intArrayOf(android.R.attr.state_selected),
                    enableDrawable
                )
                stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), enableDrawable)
                stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), enableDrawable)
                stateListDrawable.addState(intArrayOf(), disableDrawable)
                background = stateListDrawable

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}