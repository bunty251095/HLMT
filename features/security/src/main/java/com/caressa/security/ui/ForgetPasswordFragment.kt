package com.caressa.security.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.Validation
import com.caressa.security.R
import com.caressa.security.databinding.FragmentForgetPasswordBinding
import com.caressa.security.viewmodel.ForgetPasswordViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ForgetPasswordFragment : BaseFragment() {

    private val viewModel: ForgetPasswordViewModel by viewModel()
    private lateinit var binding: FragmentForgetPasswordBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.edtMobileNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().length > 3) {
                    if (!editable.toString().contains("@")) {
                        binding.tilEdtForgotEmail.isErrorEnabled = true
                        binding.tilEdtForgotEmail.setError("Email address must include @")
                    } else {
                        if (!Validation.isValidEmail(editable.toString())) {
                            binding.tilEdtForgotEmail.isErrorEnabled = true
                            binding.tilEdtForgotEmail.setError("Please Enter valid Email")
                        } else {
                            binding.tilEdtForgotEmail.error = null
                            binding.tilEdtForgotEmail.isErrorEnabled = false
                        }
                    }
                }
                if (Validation.isValidEmail(editable.toString()) || Validation.isValidPhoneNumber(
                        editable.toString())) {
                    binding.imgValidation.visibility = View.VISIBLE
                } else {
                    binding.imgValidation.visibility = View.GONE
                }
                if (editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtForgotEmail.error = null
                    binding.tilEdtForgotEmail.isErrorEnabled = false
                }
            }
        })

        viewModel.isEmail.observe( viewLifecycleOwner , Observer {})
        viewModel.otpGenerateData.observe( viewLifecycleOwner , Observer {})
    }

    private fun setClickable() {

        binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        binding.btnContinue.setOnClickListener {
            validate()
        }

        binding.txtResendMail.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        val email: String = binding.edtMobileNumber.getText().toString()
        if (Utilities.isNullOrEmpty(email) || !Validation.isValidEmail(email)) {
            binding.tilEdtForgotEmail.setErrorEnabled(true)
            binding.tilEdtForgotEmail.setError("Please enter valid Email")
        } else {
            viewModel.checkEmailExistOrNot(email)
        }
    }

}
