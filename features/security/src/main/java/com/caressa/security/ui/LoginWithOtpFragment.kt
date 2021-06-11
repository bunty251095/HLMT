package com.caressa.security.ui

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.repository.utils.Resource
import com.caressa.security.databinding.FragmentLoginWithOtpBinding
import kotlinx.android.synthetic.main.dialog_otp.*
import org.koin.android.viewmodel.ext.android.viewModel
import com.caressa.security.viewmodel.LoginWithOtpViewModel
import com.caressa.security.R

class LoginWithOtpFragment : BaseFragment() {

    private val viewModel: LoginWithOtpViewModel by viewModel()
    lateinit var binding: FragmentLoginWithOtpBinding

    private lateinit var dialog: Dialog
    private var strOTP : String? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginWithOtpBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()

        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_otp)

        viewModel.otpScreenData.observe(viewLifecycleOwner , Observer {
                    val email = it.get("email").toString()
                    val phone = it.get("phone").toString()
                    showOTPScreen( email , phone )
                })
        return binding.root
    }

    fun setClickable() {

       binding.btnNextOtp.setOnClickListener {
           viewModel.btnNextClick( binding.edtNumberOtp.text.toString())
       }
    }

    fun showOTPScreen( emailStr: String, phoneNumber: String ) {

        if (!dialog.isShowing) {
            dialog.show()
        }

        dialog.progress_otp.max = 30
        dialog.progress_otp.progress = 30
        dialog.txt_seconds_left.text = 30.toString()
        dialog.txt_resend_otp.isEnabled = false

        val otpTimer = object : CountDownTimer(31000, 1000) {
            var total = 0
            override fun onTick(millisUntilFinished: Long) {
                total = (millisUntilFinished / 1000).toInt()
                dialog.progress_otp.progress = total
                dialog.txt_seconds_left.text = total.toString()
            }

            override fun onFinish() {
                dialog.progress_otp.progress = 0
                dialog.txt_seconds_left.text = 0.toString()
                dialog.txt_resend_otp.isEnabled = true
                dialog.progress_view.visibility = View.GONE
                cancel()
            }
        }.start()

        dialog.setOnDismissListener { otpTimer.cancel() }

        dialog.txt_resend_otp.setOnClickListener {
             viewModel.calIGenerateOTPApi(emailStr,phoneNumber)
        }

        dialog.btn_ok.setOnClickListener {

            strOTP = dialog.edt_otp.otp.toString()

            if ( !strOTP.isNullOrEmpty()  && strOTP?.length == 6 ) {

                viewModel.callValidateOTPforUserAPI( strOTP!! , emailStr , phoneNumber )
                viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                    if( it == Resource.Status.SUCCESS) {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                    }
                })

            } else {
                Utilities.toastMessageLong(context,resources.getString(R.string.VALIDATE_OTP))
            }
        }
    }

}
