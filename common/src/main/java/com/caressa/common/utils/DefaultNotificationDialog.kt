package com.caressa.common.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.caressa.common.R
import kotlinx.android.synthetic.main.default_dialog.*

class DefaultNotificationDialog(context: Context?,
                                private val onDialogValueListener: OnDialogValueListener,
                                data: DialogData) : Dialog(context!!), View.OnClickListener {

    private var dialogData: DialogData? = null

    init {
        //Utilities.printData("dialogData", data)
        this.dialogData = data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.default_dialog)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)

        img_close_input.setOnClickListener(this)
        btn_left_side.setOnClickListener(this)
        btn_right_side.setOnClickListener(this)

        btn_left_side.text = dialogData!!.btnLeftName
        btn_right_side.text = dialogData!!.btnRightName
        txt_dialog_title.text = dialogData!!.title
        //txt_dialog_description.text = dialogData!!.message
        txt_dialog_description.text = Html.fromHtml(dialogData!!.message)

        if (!dialogData!!.showLeftButton) {
            btn_left_side.visibility = View.GONE
        } else {
            btn_left_side.visibility = View.VISIBLE
        }
        if (!dialogData!!.showRightButton) {
            btn_right_side.visibility = View.GONE
        } else {
            btn_right_side.visibility = View.VISIBLE
        }

        if (dialogData!!.showDismiss) {
            img_close_input.visibility = View.VISIBLE
        } else {
            img_close_input.visibility = View.GONE
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_left_side -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = true,
                    isButtonRight = false
                )
                dismiss()
            }
            R.id.btn_right_side -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = false,
                    isButtonRight = true
                )
                dismiss()
            }
            R.id.img_close_input -> {
                onDialogValueListener.onDialogClickListener(
                    isButtonLeft = false,
                    isButtonRight = false
                )
                dismiss()
            }
        }
    }

    class DialogData {
        var title = "Title"
        var message = "Message"
        var btnLeftName = "Cancel"
        var btnRightName = "Ok"
        var showLeftButton = true
        var showRightButton = true
        var showDismiss = true
    }

    interface OnDialogValueListener {
        fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean)
    }

}