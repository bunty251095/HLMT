package com.caressa.common.base

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import com.caressa.common.R
import com.caressa.common.utils.Utilities
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_image_full_view_common.*

class DialogFullScreenView(context:Context,isImg:Boolean,imgUrl:String,bitmap:Bitmap?)
    : Dialog(context,R.style.TransparentProgressDialog), View.OnClickListener {

    val url = imgUrl
    val isImage = isImg
    val imgBitmap = bitmap

    init {
        setContentView(R.layout.dialog_image_full_view_common)
        val wlmp = window!!.attributes
        wlmp.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = wlmp
        setTitle(null)
        img_close_img.setOnClickListener(this)

        expanded_bitmap_image.setOnViewDragListener { dx, dy ->

        }

    }

    override fun show() {
        super.show()
        if ( !Utilities.isNullOrEmpty(url) ) {
            expanded_image.visibility = View.VISIBLE
            layout_img_bitmap.visibility = View.GONE
            if ( isImage ) {
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.img_placeholder)
                    .resize(15000,12000)
                    .onlyScaleDown()
                    .error(R.drawable.img_placeholder)
                    .into(expanded_image)
                PhotoViewAttacher(expanded_image)
            } else {
                expanded_image.setImageResource(R.drawable.img_placeholder)
            }
        } else {
            layout_img_bitmap.visibility = View.VISIBLE
            expanded_image.visibility = View.GONE
            if ( imgBitmap != null ) {
                setCanceledOnTouchOutside(false)
                expanded_bitmap_image.setImageBitmap(imgBitmap)
            }
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_close_img -> {
                dismiss()
            }
        }
    }

}