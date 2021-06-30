package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.FirebaseHelper
import com.caressa.repository.utils.Resource
import com.caressa.security.databinding.FragmentLoginWithOtpBinding
import org.koin.android.viewmodel.ext.android.viewModel
import com.caressa.security.viewmodel.LoginWithOtpViewModel
import com.caressa.security.R
import kotlinx.android.synthetic.main.fragment_login_with_otp.*

class LoginWithOtpFragment : BaseFragment() {

    private val viewModel: LoginWithOtpViewModel by viewModel()
    lateinit var binding: FragmentLoginWithOtpBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginWithOtpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.NON_HLMT_LOGIN_SCREEN_EVENT,false)
        init()
        setClickable()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        viewModel.isLoginName.observe(viewLifecycleOwner, Observer {

        })

        viewModel.otpVerifyData.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if (it.validity?.equals("TRUE",true) == true){
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_VERIFICATION_SUCCESSFUL_EVENT)
                }else{
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_VERIFICATION_FAIL_EVENT)
                }
            }
        })

        viewModel.otpGenerateData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if (it.status?.equals("SUCCESS", true) == true) {
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_SUCCESSFUL_EVENT)
                    binding.verifyCodeLayout.visibility = View.VISIBLE
                    binding.btnSendOtp.visibility = View.GONE
                } else {
                    binding.verifyCodeLayout.visibility = View.GONE
                    binding.btnSendOtp.visibility = View.VISIBLE
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_FAIL_EVENT)
                }
            }else{
                binding.verifyCodeLayout.visibility = View.GONE
                binding.btnSendOtp.visibility = View.VISIBLE

            }
        })
    }

    private fun init() {
        binding.verifyCodeLayout.visibility = View.GONE
    }

    private fun setClickable() {

        binding.btnSendOtp.setOnClickListener {
            viewModel.checkLoginNameExistOrNot(binding.edtMobileNumber.text.toString())
        }

        binding.btnBack.setOnClickListener {

        }

        binding.btnVerify.setOnClickListener {
            viewModel.callValidateOTPforUserAPI(binding.layoutCodeView.text.toString(),binding.edtMobileNumber.text.toString());
        }

        binding.txtResendCode.setOnClickListener {
            binding.layoutCodeView.setText("")
            viewModel.checkLoginNameExistOrNot(binding.edtMobileNumber.text.toString())
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.RESEND_OTP_EVENT)
        }

    }

}
