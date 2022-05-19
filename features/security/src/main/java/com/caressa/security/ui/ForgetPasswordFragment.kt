package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        viewModel.isEmail.observe(viewLifecycleOwner, {})
        viewModel.otpGenerateData.observe(viewLifecycleOwner, {})
    }

    private fun setClickable() {

        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            validate()
        }

        binding.txtResendMail.setOnClickListener {
            validate()
            //it.findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyCodeFragment)
        }
    }

    private fun validate() {
        val email: String = binding.edtForgotEmail.text.toString()

        if ( Utilities.isNullOrEmpty(email) || !Validation.isValidEmail(email) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMAIL))
        } else {
            viewModel.checkEmailExistOrNot(email)
        }

    }

}
