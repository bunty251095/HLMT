package com.caressa.common.utils

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.caressa.common.R


object ViewUtils {

    private val appColorHelper = AppColorHelper.instance!!

    /**
     * gets Radio Selected Value Tag
     *
     * @return
     */
    fun getRadioSelectedValueTag(radioGroup: RadioGroup): String {
        val checkedRadioId = radioGroup.checkedRadioButtonId
        var selectedRadioValue = ""
        if (checkedRadioId != -1) {
            val radioView = radioGroup.findViewById<View>(checkedRadioId)
            val indexOfChild = radioGroup.indexOfChild(radioView)
            val radioButton = radioGroup.getChildAt(indexOfChild) as RadioButton
            selectedRadioValue = radioButton.tag.toString()
        }
        return selectedRadioValue
    }

    /**
     * gets Radio Button Selected Value
     *
     * @return
     */
    fun getRadioButtonSelectedValue(radioGroup: RadioGroup): String {
        val checkedRadioId = radioGroup.checkedRadioButtonId
        var selectedRadioValue = ""
        if (checkedRadioId != -1) {
            val radioView = radioGroup.findViewById<View>(checkedRadioId)
            val indexOfChild = radioGroup.indexOfChild(radioView)
            val radioButton = radioGroup.getChildAt(indexOfChild) as RadioButton
            selectedRadioValue = radioButton.text.toString()
        }
        return selectedRadioValue
    }

    /**
     * set Radio Button Check By Tag
     */
    fun setRadioButtonCheckByTag(radioGroup: RadioGroup, strCheckTag: String) {
        val radioView = radioGroup.findViewWithTag<View>(strCheckTag)
        if (radioView != null) {
            val indexOfChild = radioGroup.indexOfChild(radioView)
            val radioButton = radioGroup.getChildAt(indexOfChild) as RadioButton
            radioButton.isChecked = true
        }
    }

    /**
     * clear Radio Button Check By Tag
     */
    fun clearRadioButtonCheckByTag(radioGroup: RadioGroup, strCheckTag: String) {
        val radioView = radioGroup.findViewWithTag<View>(strCheckTag)
        if (radioView != null) {
            val indexOfChild = radioGroup.indexOfChild(radioView)
            val radioButton = radioGroup.getChildAt(indexOfChild) as RadioButton
            radioButton.isChecked = false
        }
    }

    fun getArcRbSelectorBgHra(isPrimary: Boolean): StateListDrawable {
        var color = appColorHelper.selectionColor
        if (!isPrimary) {
            color = Color.parseColor("#b4b4b4")
        }
        val disableDrawable = GradientDrawable()
        disableDrawable.shape = GradientDrawable.RECTANGLE
        disableDrawable.cornerRadius = R.dimen._1sdp.toFloat()
        disableDrawable.setStroke(4, color)
        disableDrawable.setColor(Color.WHITE)

        val enableDrawable = GradientDrawable()
        enableDrawable.shape = GradientDrawable.RECTANGLE
        enableDrawable.cornerRadius = R.dimen._1sdp.toFloat()
        enableDrawable.setStroke(4, color)
        enableDrawable.setColor(color)

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), enableDrawable)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), enableDrawable)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), enableDrawable)
        stateListDrawable.addState(intArrayOf(), disableDrawable)
        return stateListDrawable
    }

    fun getArcRbSelectorTxtColorHra(isPrimary: Boolean): ColorStateList {
        var color = appColorHelper.selectionColor
        if (!isPrimary) {
            color = Color.parseColor("#b4b4b4")
        }
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE, color, color)
        )
    }

    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return px / (densityDpi / 160f)
    }

    fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return Math.round(dp * density)
    }

}
