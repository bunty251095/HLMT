package com.caressa.fitness_tracker.view

import com.caressa.fitness_tracker.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomStepsDetailView : ConstraintLayout {

    private var mContext: Context
    private var txtData: TextView? = null
    private var txtValue: TextView? = null
    private var imgData: ImageView? = null

    constructor(context: Context) : super(context) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
        initLayout()
    }

    private fun initLayout() {
        val rootView = inflate(mContext, R.layout.view_custom_steps_detail, this)
        txtData = rootView.findViewById<View>(R.id.txt_data) as TextView
        imgData = rootView.findViewById<View>(R.id.img_data) as ImageView
        txtValue = rootView.findViewById<View>(R.id.txt_value) as TextView
    }

    fun setDataTitle(title: String) {
        txtData!!.text = title
    }

    fun setDataValue(value: String) {
        txtValue!!.text = value
    }

    fun setDataImage(image: Int) {
        imgData!!.setImageResource(image)
    }

}