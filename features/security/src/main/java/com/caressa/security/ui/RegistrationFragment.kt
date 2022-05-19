package com.caressa.security.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.security.R
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.databinding.FragmentRegistrationBinding
import com.caressa.security.databinding.FragmentUserDetailsBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.caressa.security.viewmodel.RegistrationViewModel
import com.yalantis.ucrop.UCrop
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.*

class RegistrationFragment: BaseFragment(), DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel : RegistrationViewModel by viewModel()
    private lateinit var binding: FragmentRegistrationBinding
    private var fName = ""
    private var fPath = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        registerObserver()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.edtReenterNewPassword.transformationMethod = null
        binding.btnDone.setOnClickListener {
             var gender = "M"
                if (binding.rbMale.isChecked) {
                    gender = "M"
                } else {
                    gender = "F"
                }
                viewModel.checkLoginNameExistOrNot(name = binding.edtUsername.text.toString().trim(),
                    emailAddress = binding.edtEmail.text.toString().trim(),
                    password = binding.edtNewPassword.text.toString().trim(),
                    confirmPassword = binding.edtReenterNewPassword.text.toString().trim(),
                    phoneNumber = binding.edtPhoneNumber.text.toString().trim()
                    )

        }

        binding.imgPasswordInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.PASSWORD_CRITERIA),
                message = Html.fromHtml("<a>" +  "- ${resources.getString(R.string.PASSWORD_CRITERIA_DESC1)} <br/><br/> - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC2)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC3)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC4)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC5)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC6)}" + "</a>").toString(),
                showLeftBtn = false)
        }

    }

    private fun registerObserver() {
        viewModel.isLoginName.observe(viewLifecycleOwner, {
            Timber.i("Data=> "+it)
        })
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {

    }
}