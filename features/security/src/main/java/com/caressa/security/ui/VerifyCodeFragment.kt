package com.caressa.security.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.security.R
import com.caressa.security.databinding.FragmentVerifyCodeBinding
import com.caressa.security.viewmodel.ForgetPasswordViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class VerifyCodeFragment : BaseFragment() {

    private val viewModel: ForgetPasswordViewModel by viewModel()
    private lateinit var binding: FragmentVerifyCodeBinding
    var email = ""
    var phone = ""
    var verificationCode = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        email =  requireArguments().getString("email")!!
        Timber.e( "Email--->$email" )
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.layoutCodeView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                verificationCode = editable.toString()
                if (verificationCode.length == 6) {
                    binding.layoutCodeView.setLineColor(ContextCompat.getColor(requireContext(),R.color.vivantGreen))
                    Timber.i("VerificationCode--->$verificationCode")
                } else {
                    binding.layoutCodeView.setLineColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey))
                }
            }
        })

        viewModel.otpGenerateData.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.btnVerify.setOnClickListener {
            if (Utilities.isNullOrEmpty(binding.layoutCodeView.text.toString()) ||
                binding.layoutCodeView.text.toString().length != 6) {
                Utilities.toastMessageShort(context, "Please Enter Valid Verification Code")
            } else {
                viewModel.callValidateVerificationCode(binding.layoutCodeView.text.toString(),email)
            }
        }

        binding.txtResendCode.setOnClickListener {
            viewModel.callGenerateVerificationCode( email = email , from = "VerifyCode")
        }

    }

}
