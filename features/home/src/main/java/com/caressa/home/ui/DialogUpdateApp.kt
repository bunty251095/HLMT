package com.caressa.home.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.DialogAppUpdateBinding
import com.caressa.model.entity.AppVersion

class DialogUpdateApp(context: Context,versionDetails : AppVersion) : Dialog(context) {

    private lateinit var binding : DialogAppUpdateBinding

    private var appVersion: AppVersion? = null

    init {
        this.appVersion = versionDetails
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_app_update, null,false)
        binding.appVersion = appVersion
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        setClickable()
    }

    private fun setClickable() {

        binding.btnLeftUpdate.setOnClickListener {
            dismiss()
        }

        binding.btnRightUpdate.setOnClickListener {
            DataHandler(context).goToPlayStore(context)
        }

    }

}