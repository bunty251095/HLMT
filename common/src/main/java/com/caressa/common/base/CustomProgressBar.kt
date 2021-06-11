package com.caressa.common.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.animation.AnimationUtils
import com.caressa.common.R
import com.caressa.common.utils.AppColorHelper
import kotlinx.android.synthetic.main.custum_progress_bar.*

class CustomProgressBar(context: Context) : Dialog(context, R.style.TransparentProgressDialog) {

    init {
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        //setOnCancelListener(null)
        setContentView(R.layout.custum_progress_bar)
    }

    override fun show() {
        super.show()
        val anim = AnimationUtils.loadAnimation(context, R.anim.rotate_forward)
        img_custom_progress.animation = anim
        img_custom_progress.startAnimation(anim)
    }
}
