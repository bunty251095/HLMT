package com.caressa.home.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.Validation
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivityPasswordChangeBinding
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class PasswordChangeActivity : BaseActivity() {

    private lateinit var binding : ActivityPasswordChangeBinding
    private val viewModel : DashboardViewModel by viewModel()
    private val backgroundCallViewModel : BackgroundCallViewModel by viewModel()

    private var btnReLogin : Button? = null
    private var dialogPasswordUpdated: Dialog? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_change)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        FirebaseHelper.logScreenEvent(FirebaseConstants.PASSWORD_CHANGE_SCREEN)
        initialise()
        setClickable()
    }

    private fun initialise() {

        dialogPasswordUpdated = Dialog(this, R.style.NoTitleDialog)
        dialogPasswordUpdated!!.setContentView(R.layout.dialog_change_password_success)
        dialogPasswordUpdated!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogPasswordUpdated!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogPasswordUpdated!!.window!!.currentFocus
        dialogPasswordUpdated!!.setCanceledOnTouchOutside(false)
        dialogPasswordUpdated!!.setCancelable(false)

        btnReLogin = dialogPasswordUpdated!!.findViewById(R.id.btn_go_to_app)

        btnReLogin!!.text = resources.getString(R.string.RE_LOGIN)

        binding.edtOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtOldPassword.error = null
                    binding.tilEdtOldPassword.isErrorEnabled = false
                }
            }
        })

        binding.edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtNewPassword.error = null
                    binding.tilEdtNewPassword.isErrorEnabled = false
                }
            }
        })

        binding.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtConfirmPassword.error = null
                    binding.tilEdtConfirmPassword.isErrorEnabled = false
                }
            }
        })

        viewModel.passwordChange.observe(this, {})
    }

    private fun setClickable() {

        binding.btnChangePassword.setOnClickListener {
            validate()
        }

        btnReLogin!!.setOnClickListener {
            DataHandler(this).logout(backgroundCallViewModel, this)
            dialogPasswordUpdated!!.dismiss()
        }

    }

    private fun validate() {

        val oldPassword: String = binding.edtOldPassword.text.toString()
        val newPassword: String = binding.edtNewPassword.text.toString()
        val confirmPassword: String = binding.edtConfirmPassword.text.toString()

        if (Utilities.isNullOrEmpty(oldPassword)) {
            binding.tilEdtOldPassword.isErrorEnabled = true
            binding.tilEdtOldPassword.error = resources.getString(R.string.VALIDATE_PASSWORD)
        }
        if (Utilities.isNullOrEmpty(newPassword)) {
            binding.tilEdtNewPassword.isErrorEnabled = true
            binding.tilEdtNewPassword.error = resources.getString(R.string.VALIDATE_PASSWORD)
        }
        if (Utilities.isNullOrEmpty(confirmPassword)) {
            binding.tilEdtConfirmPassword.isErrorEnabled = true
            binding.tilEdtConfirmPassword.error = resources.getString(R.string.VALIDATE_PASSWORD)
        }

        if (!Utilities.isNullOrEmpty(oldPassword)
            && !Utilities.isNullOrEmpty(newPassword)
            && !Utilities.isNullOrEmpty(confirmPassword)) {
            if (!Validation.isValidPassword(newPassword)) {
                binding.tilEdtNewPassword.isErrorEnabled = true
                binding.tilEdtNewPassword.error = resources.getString(R.string.ERROR_PASSWORD_SHOULD_BE_MINIMUM_6_CHARACTERS)
            } else if (!Validation.isValidPassword(confirmPassword)) {
                binding.tilEdtConfirmPassword.isErrorEnabled = true
                binding.tilEdtConfirmPassword.error = resources.getString(R.string.ERROR_PASSWORD_SHOULD_BE_MINIMUM_6_CHARACTERS)
            } else if (oldPassword.equals(newPassword, ignoreCase = true)) {
                binding.tilEdtNewPassword.isErrorEnabled = true
                binding.tilEdtNewPassword.error = resources.getString(R.string.ERROR_NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_OLD_PASSWORD)
            } else if (oldPassword.equals(confirmPassword, ignoreCase = true)) {
                binding.tilEdtConfirmPassword.isErrorEnabled = true
                binding.tilEdtConfirmPassword.error = resources.getString(R.string.ERROR_NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_OLD_PASSWORD)
            } else if (!newPassword.equals(confirmPassword, ignoreCase = true)) {
                binding.tilEdtConfirmPassword.isErrorEnabled = true
                binding.tilEdtConfirmPassword.error = resources.getString(R.string.ERROR_PASSWORD_DOES_NOT_MATCH)
            }
        }

        if ( !binding.tilEdtOldPassword.isErrorEnabled && !binding.tilEdtNewPassword.isErrorEnabled
            && !binding.tilEdtConfirmPassword.isErrorEnabled ) {
            viewModel.callPasswordChangeApi(oldPassword, confirmPassword, this)
        }

    }

    fun showPasswordUpdatedDialog() {
        dialogPasswordUpdated!!.show()
    }

}