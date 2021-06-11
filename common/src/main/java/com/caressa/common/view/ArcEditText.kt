package com.caressa.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration.getAppTemplateConfig
import com.caressa.common.constants.Constants
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import timber.log.Timber

class ArcEditText : TextInputEditText {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleEditText(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        styleEditText(context, attrs)
    }

    private fun styleEditText(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(getAppTemplateConfig())
                if (templateJSON.has(Constants.SELECTION_COLOR)) {
                    val selectionColor = Color.parseColor(templateJSON.getString(Constants.SELECTION_COLOR))
                    val deselectionColor =
                        Color.parseColor(templateJSON.getString(Constants.DESELECTION_COLOR))
                    //                    DrawableCompat.setTint(getBackground(), deselectionColor);
                    setUnderlineColor(selectionColor, deselectionColor)
                    this.onFocusChangeListener = OnFocusChangeListener { view, b ->
                        Timber.d("" + view.isFocused)
                        setUnderlineColor(selectionColor, deselectionColor)
                        //                            if(view.isFocused()) {
////                                DrawableCompat.setTint(getBackground(),Color.RED);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    view.setBackgroundTintList(ColorStateList.valueOf(selectionColor));
////                                    setUnderlineColor(selectionColor,deselectionColor);
//                                }
//                            }else {
////                                DrawableCompat.setTint(getBackground(),Color.BLACK);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    view.setBackgroundTintList(ColorStateList.valueOf(deselectionColor));
//                                }
//                            }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUnderlineColor(selectionColor: Int, deselectionColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_hovered),
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(android.R.attr.state_focused))

            val colors = intArrayOf(
                deselectionColor,
                selectionColor,
                selectionColor,
                selectionColor,
                selectionColor,
                selectionColor,
                selectionColor)

            val myList = ColorStateList(states, colors)
            DrawableCompat.setTintList(background, myList)
            setCursorColor(this, selectionColor)
            //setBackgroundTintList(myList);
        }
    }

    companion object {
        fun setCursorColor(view: EditText, @ColorInt color: Int) {
            try {
                // Get the cursor resource id
                var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                val drawableResId = field.getInt(view)

                // Get the editor
                field = TextView::class.java.getDeclaredField("mEditor")
                field.isAccessible = true
                val editor = field[view]

                // Get the drawable and set a color filter
                val drawable = ContextCompat.getDrawable(view.context, drawableResId)
                drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                val drawables = arrayOf<Drawable?>(drawable, drawable)

                // Set the drawables
                field = editor.javaClass.getDeclaredField("mCursorDrawable")
                field.isAccessible = true
                field[editor] = drawables
            } catch (ignored: Exception) {
            }
        }
    }
}