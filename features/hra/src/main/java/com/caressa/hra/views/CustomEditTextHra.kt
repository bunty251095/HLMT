package com.caressa.hra.views

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.caressa.hra.R


class CustomEditTextHra : ConstraintLayout  {

    private var mContext: Context
    private var layoutContainer: ConstraintLayout? = null
    private var imageView: AppCompatImageView? = null
    var editText: AppCompatEditText? = null
    private var unitText: AppCompatTextView? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initLayout()
    }

    private fun initLayout() {
        val rootView = inflate(mContext, R.layout.custom_edit_text_hra, this)
        layoutContainer = rootView.findViewById<View>(R.id.layoutCustumEditText) as ConstraintLayout
        imageView = rootView.findViewById<View>(R.id.img_value) as AppCompatImageView
        editText = rootView.findViewById<View>(R.id.edt_value) as AppCompatEditText
        unitText = rootView.findViewById<View>(R.id.txt_unit) as AppCompatTextView
    }

    fun setImage(img: Int) {
        imageView!!.setImageResource(img)
    }

    fun setHint(hintText: String) {
        editText!!.hint = hintText
    }

    fun setNonEditable() {
        editText!!.inputType = InputType.TYPE_NULL
        //editText!!.isEnabled = false;
        //editText!!.keyListener = null
        //editText!!.isFocusable = true
    }

    fun setInputType(type: Int) {
        editText!!.inputType = type
    }

    fun setValue(value: String) {
        editText!!.setText(value)
    }

    fun getValue() : String {
        return editText!!.text.toString()
    }

    fun setUnit(unit: String) {
        unitText!!.text = unit
    }

    fun getUnit() : String {
        return unitText!!.text.toString()
    }

}