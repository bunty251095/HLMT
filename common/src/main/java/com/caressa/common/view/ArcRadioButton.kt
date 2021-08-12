package com.caressa.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import org.json.JSONObject

class ArcRadioButton : AppCompatRadioButton {

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        style(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        style(context, attrs)
    }

    private fun style(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        val isCalculator = a.getBoolean(R.styleable.CustomTemplate_isCalculator, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                val selectionColor =
                    Color.parseColor(templateJSON.getString(Constants.SELECTION_COLOR))
                val deselectionColor =
                    Color.parseColor(templateJSON.getString(Constants.DESELECTION_COLOR))
                val textColor = Color.parseColor(templateJSON.getString(Constants.TEXT_COLOR))
                if (!isCalculator) {
                    val colorStateList = ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_enabled),
                            intArrayOf(-android.R.attr.state_enabled)
                        ),
                        intArrayOf(
                            selectionColor,
                            deselectionColor,
                            selectionColor,
                            deselectionColor
                        )
                    )
                    val textColorStateList = ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_enabled),
                            intArrayOf(-android.R.attr.state_enabled)
                        ),
                        intArrayOf(
                            Color.WHITE,
                            deselectionColor,
                            Color.WHITE,
                            deselectionColor
                        )
                    )

                    setTextColor(textColorStateList)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        backgroundTintList = colorStateList
                    }
                    setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setTextColor(textColorStateList)
                                backgroundTintList = colorStateList
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setTextColor(textColorStateList)
                                backgroundTintList = colorStateList
                            }
                        }
                    }
                } else {
                    val disableDrawable = GradientDrawable()
                    disableDrawable.shape = GradientDrawable.RECTANGLE
                    disableDrawable.cornerRadius = 5f
                    disableDrawable.setStroke(3, selectionColor)
                    disableDrawable.setColor(Color.WHITE)
                    val enableDrawable = GradientDrawable()
                    enableDrawable.shape = GradientDrawable.RECTANGLE
                    enableDrawable.cornerRadius = 5f
                    enableDrawable.setStroke(3, selectionColor)
                    enableDrawable.setColor(selectionColor)
                    val stateListDrawable = StateListDrawable()
                    stateListDrawable.addState(
                        intArrayOf(android.R.attr.state_selected),
                        enableDrawable
                    )
                    stateListDrawable.addState(
                        intArrayOf(android.R.attr.state_pressed),
                        enableDrawable
                    )
                    stateListDrawable.addState(
                        intArrayOf(android.R.attr.state_checked),
                        enableDrawable
                    )
                    stateListDrawable.addState(intArrayOf(), disableDrawable)
                    background = stateListDrawable
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
                            textColor,
                            textColor
                        )
                    )
                    setTextColor(textColorStateList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}