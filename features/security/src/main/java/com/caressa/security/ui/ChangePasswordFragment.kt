package com.caressa.security.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.Validation
import com.caressa.common.utils.showDialog
import com.caressa.security.R
import com.caressa.security.databinding.FragmentChangePasswordBinding
import com.caressa.security.viewmodel.ForgetPasswordViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ChangePasswordFragment : BaseFragment() , DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: ForgetPasswordViewModel by viewModel()
    private lateinit var binding: FragmentChangePasswordBinding
    var dialogPasswordUpdated: Dialog? = null
    private var btnGoToApp : Button? = null
    var email = ""
    var phone = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        email =  requireArguments().getString("email")!!
        Timber.e( "Email--->$email" )
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        dialogPasswordUpdated = Dialog(requireContext(), R.style.NoTitleDialog)
        dialogPasswordUpdated!!.setContentView(R.layout.dialog_change_password_success)
        dialogPasswordUpdated!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogPasswordUpdated!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogPasswordUpdated!!.window!!.currentFocus
        dialogPasswordUpdated!!.setCanceledOnTouchOutside(false)
        dialogPasswordUpdated!!.setCancelable(false)

        btnGoToApp = dialogPasswordUpdated!!.findViewById(R.id.btn_go_to_app)

        binding.edtNewPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtNewPassword.error = null
                    binding.tilEdtNewPassword.isErrorEnabled = false
                }
                if (Validation.isValidPassword(editable.toString())) {
                    binding.imgTickNewPassword.visibility = View.VISIBLE
                    binding.tilEdtNewPassword.error = null
                    binding.tilEdtNewPassword.isErrorEnabled = false
                } else {
                    binding.imgTickNewPassword.visibility = View.GONE
                    binding.tilEdtNewPassword.isErrorEnabled = true
                    binding.tilEdtNewPassword.error = "Please doesn't match the Requirements"
                }
            }
        })

        binding.edtReenterNewPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtReenterNewPassword.error = null
                    binding.tilEdtReenterNewPassword.isErrorEnabled = false
                }
                if (Validation.isValidPassword(editable.toString())) {
                    if (editable.toString() == binding.edtNewPassword.text.toString()) {
                        binding.imgTickReEnterPassword.visibility = View.VISIBLE
                        binding.tilEdtReenterNewPassword.error = null
                        binding.tilEdtReenterNewPassword.isErrorEnabled = false
                    } else {
                        binding.imgTickReEnterPassword.visibility = View.GONE
                        binding.tilEdtReenterNewPassword.isErrorEnabled = true
                        binding.tilEdtReenterNewPassword.error = "Password doesn't match"
                    }
                } else {
                    binding.imgTickReEnterPassword.visibility = View.GONE
                    binding.tilEdtReenterNewPassword.isErrorEnabled = true
                    binding.tilEdtReenterNewPassword.error = "Please doesn't match the Requirements"
                }
            }
        })

        viewModel.updatePassword.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.btnResetPassword.setOnClickListener {
            val password: String = binding.edtNewPassword.text.toString()
            val confirmPassword: String = binding.edtReenterNewPassword.text.toString()
            if (Utilities.isNullOrEmpty(password) && Utilities.isNullOrEmpty(confirmPassword)) {
                binding.tilEdtNewPassword.isErrorEnabled = true
                binding.tilEdtNewPassword.error = resources.getString(R.string.ERROR_INVALID_PASSWORD)
                binding.tilEdtReenterNewPassword.isErrorEnabled = true
                binding.tilEdtReenterNewPassword.error = resources.getString(R.string.ERROR_INVALID_PASSWORD)
            } else if (Utilities.isNullOrEmpty(password)) {
                binding.tilEdtNewPassword.isErrorEnabled = true
                binding.tilEdtNewPassword.error = resources.getString(R.string.ERROR_INVALID_PASSWORD)
            } else if (Utilities.isNullOrEmpty(confirmPassword)) {
                binding.tilEdtReenterNewPassword.isErrorEnabled = true
                binding.tilEdtReenterNewPassword.error = resources.getString(R.string.ERROR_INVALID_PASSWORD)
            } else if (!Validation.isValidPassword(password)) {
                binding.tilEdtNewPassword.isErrorEnabled = true
                binding.tilEdtNewPassword.error = resources.getString(R.string.ERROR_PASSWORD_DOESNOT_MATCH_REQUIREMENTS)
            } else if (!Validation.isValidPassword(confirmPassword)) {
                binding.tilEdtReenterNewPassword.isErrorEnabled = true
                binding.tilEdtReenterNewPassword.error = resources.getString(R.string.ERROR_PASSWORD_DOESNOT_MATCH_REQUIREMENTS)
            } else if (password != confirmPassword) {
                binding.tilEdtReenterNewPassword.isErrorEnabled = true
                binding.tilEdtReenterNewPassword.error = resources.getString(R.string.ERROR_PASSWORD_DOESNOT_MATCH)
            } else {
                viewModel.callUpdatePassword(this,email,confirmPassword)
            }
        }

        binding.imgPasswordInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.PASSWORD_CRITERIA),
                message = resources.getString(R.string.PASSWORD_CRITERIA_DESC),
                showLeftBtn = false)
        }

        btnGoToApp!!.setOnClickListener {
            dialogPasswordUpdated!!.dismiss()
            binding.imgDone.performClick()
        }

        binding.imgDone.setOnClickListener {
            it.findNavController().navigate(R.id.action_changePasswordFragment_to_loginFragment)
        }

    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}
}
