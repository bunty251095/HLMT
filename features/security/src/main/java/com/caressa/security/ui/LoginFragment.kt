package com.caressa.security.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.extension.showSnackbar
import com.caressa.common.utils.Validation
import com.caressa.security.R
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        val strTermsOfServices = "<u><a><B><font color='#797e7d'>"+ resources.getString(R.string.VIVANT_TERMS_CONDITIONS) +"</font></B></a></u>"

        binding.txtLoginTermsofservice.text = Html.fromHtml(resources.getString(R.string.TERMS_OF_SERVICE_LOGIN) + "<br/>"
                + strTermsOfServices + " " + resources.getString(R.string.YOU_HAVE_READ_THEM))

        binding.edtLoginEmailaddress.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (Validation.isValidEmail(editable.toString()) || Validation.isValidPhoneNumber(editable.toString())) {
                    binding.imgValidation.visibility = View.VISIBLE
                } else {
                    binding.imgValidation.visibility = View.GONE
                }
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtLoginEmailPhone.error = null
                    binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                }
/*                if (editable.toString().length > 3) {
                    if (TextUtils.isDigitsOnly(editable.toString())) {
                        if (!Validation.isValidPhoneNumber(editable.toString())) {
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                            binding.tilEdtLoginEmailPhone.error = "Please Enter valid Phone Number"
                        } else {
                            binding.tilEdtLoginEmailPhone.error = null
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                        }
                    } else {
                        if (!editable.toString().contains("@")) {
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                            binding.tilEdtLoginEmailPhone.error = "Email address must include @"
                        } else {
                            if (!Validation.isValidEmail(editable.toString())) {
                                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                                binding.tilEdtLoginEmailPhone.error = "Please Enter valid Email"
                            } else {
                                binding.tilEdtLoginEmailPhone.error = null
                                binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                            }
                        }
                    }
                }*/
            }
        })

        binding.edtLoginPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtLoginPassword.error = null
                    binding.tilEdtLoginPassword.isErrorEnabled = false
                }
            }
        })

    }


    private fun setClickable() {

        binding.btnLogin.setOnClickListener {
            validateLogin()
        }

        binding.tvLoginSignup.setOnClickListener {
            it.findNavController().navigate(R.id.next_sign_up)
        }

        binding.tvForgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_login_fragment_to_forgotPasswordFragment)
        }

        binding.txtLoginTermsofservice.setOnClickListener {
            it.findNavController().navigate(R.id.action_login_terms)
        }

        viewModel.isEmail.observe( viewLifecycleOwner , Observer {})
        viewModel.isPhone.observe( viewLifecycleOwner , Observer {})
        viewModel.user.observe( viewLifecycleOwner , Observer {})
    }

    private fun validateLogin() {
        val emailStr = binding.edtLoginEmailaddress.text.toString().trim()
        val passwordStr = binding.edtLoginPassword.text.toString().trim()

        if ( TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(passwordStr) ) {
            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
            binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            binding.tilEdtLoginPassword.isErrorEnabled = true
            binding.tilEdtLoginPassword.error = requireContext().resources.getString(R.string.ERROR_INVALID_PASSWORD)
        } else if ( TextUtils.isEmpty(emailStr) ) {
            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
            binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
        } else if ( TextUtils.isEmpty(passwordStr) ) {
            binding.tilEdtLoginPassword.isErrorEnabled = true
            binding.tilEdtLoginPassword.error = requireContext().resources.getString(R.string.ERROR_INVALID_PASSWORD)
        } else if( emailStr.contains("@", true) ) {
            if(Validation.isValidEmail(emailStr)){
                viewModel.callLogin(
                    forceRefresh = true,
                    emailStr = emailStr,
                    passwordStr = passwordStr)
                // Bypass for social login
                // viewModel.callLogin(forceRefresh = true, name = "Keyur", emailStr = emailStr, passwordStr = "123456", socialLogin = true, socialId = "12335555")
            }else{
                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            }
        } else {
            if(emailStr.isNullOrEmpty()) {
                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            }else if(Validation.isValidPhoneNumber(emailStr)){
                viewModel.checkPhoneExistAPI(phoneNumber = emailStr, passwordStr = passwordStr)
            }
        }
    }
}
