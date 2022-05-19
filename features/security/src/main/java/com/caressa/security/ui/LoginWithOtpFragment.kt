package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.security.R
import com.caressa.security.databinding.FragmentLoginWithOtpBinding
import com.caressa.security.model.UserInfo
import com.caressa.security.viewmodel.LoginWithOtpViewModel
import kotlinx.android.synthetic.main.fragment_login_with_otp.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginWithOtpFragment : BaseFragment() {

    private val viewModel: LoginWithOtpViewModel by viewModel()
    lateinit var binding: FragmentLoginWithOtpBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginWithOtpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.NON_HLMT_LOGIN_SCREEN)
        init()
        setClickable()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner, {})
        viewModel.forgetPassword.observe(viewLifecycleOwner,{})
        viewModel.isLoginName.observe(viewLifecycleOwner, Observer {
            if (it!=null) {
                if (it.isExist.equals("TRUE", true)) {
                    binding.btnVerify.text = resources.getString(R.string.VERIFY_AND_LOGIN)
                } else {
                    binding.btnVerify.text = resources.getString(R.string.VERIFY)
                }
            }
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
            /*if(it != null) {
                if (it.status?.equals("SUCCESS", true) == true) {
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_SUCCESSFUL_EVENT)
                    binding.verifyCodeLayout.visibility = View.VISIBLE
                    binding.lblEnterNumberDesc.visibility = View.GONE
                    binding.edtMobileNumber.isClickable = false
                    binding.edtMobileNumber.isCursorVisible = false
                    binding.edtMobileNumber.isEnabled = false
                } else {
                    binding.verifyCodeLayout.visibility = View.GONE
//                    binding.btnSendOtp.visibility = View.VISIBLE
                    binding.lblEnterNumberDesc.visibility = View.VISIBLE
                    binding.edtMobileNumber.isClickable = false
                    binding.edtMobileNumber.isCursorVisible = false
                    binding.edtMobileNumber.isEnabled = false
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SEND_OTP_FAIL_EVENT)
                }
            }else{
                binding.verifyCodeLayout.visibility = View.GONE
//                binding.btnSendOtp.visibility = View.VISIBLE
                binding.lblEnterNumberDesc.visibility = View.VISIBLE
                binding.edtMobileNumber.isClickable = true
                binding.edtMobileNumber.isCursorVisible = true
                binding.edtMobileNumber.isEnabled = true

            }*/
        })
    }

    private fun init() {
        binding.verifyCodeLayout.visibility = View.VISIBLE
        binding.lblEnterNumberDesc.visibility = View.VISIBLE
        binding.edtMobileNumber.isClickable = false
        binding.edtMobileNumber.isCursorVisible = false
        binding.edtMobileNumber.isEnabled = false
        binding.edtMobileNumber.setText(UserInfo.emailAddress)
    }

    private fun setClickable() {

        binding.btnSendOtp.setOnClickListener {
            viewModel.checkLoginNameExistOrNot(binding.edtMobileNumber.text.toString())
            startCountdownTimer(120)
        }

        binding.btnBack.setOnClickListener {

        }

        binding.btnVerify.setOnClickListener {
            viewModel.callValidateOTPforUserAPI(binding.layoutCodeView.text.toString(),binding.edtMobileNumber.text.toString())
        }

        binding.imgSubmit.setOnClickListener {
            if (binding.verifyCodeLayout.visibility != View.VISIBLE){
                viewModel.checkLoginNameExistOrNot(binding.edtMobileNumber.text.toString())
                startCountdownTimer(120)
            }else{
                viewModel.callValidateOTPforUserAPI(binding.layoutCodeView.text.toString(),binding.edtMobileNumber.text.toString())
            }
        }

        binding.txtEditNumber.setOnClickListener {
            binding.layoutCodeView.setText("")
            binding.edtMobileNumber.isClickable = true
            binding.edtMobileNumber.isCursorVisible = true
            binding.edtMobileNumber.isEnabled = true
            binding.verifyCodeLayout.visibility = View.GONE
//            binding.btnSendOtp.visibility = View.VISIBLE
            binding.txtEditNumber.visibility = View.GONE
            binding.lblEnterNumberDesc.visibility = View.VISIBLE
        }

        binding.txtResendCode.setOnClickListener {
            binding.layoutCodeView.setText("")
            viewModel.checkLoginNameExistOrNot(binding.edtMobileNumber.text.toString())
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.RESEND_OTP_EVENT)
        }

    }

    private fun startCountdownTimer(i: Int) {
        /*object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                mTextField.setText("done!")
            }
        }.start()*/
    }

}
