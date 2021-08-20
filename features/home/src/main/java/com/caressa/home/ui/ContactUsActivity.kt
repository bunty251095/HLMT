package com.caressa.home.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.databinding.ActivityContactUsBinding
import com.caressa.home.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ContactUsActivity : BaseActivity() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : ActivityContactUsBinding

    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        FirebaseHelper.logScreenEvent(FirebaseConstants.CONTACT_US_SCREEN)
        initialise()
        setClickable()
    }

    private fun initialise() {
        viewModel.getLoggedInPersonDetails()
        viewModel.contactUs.observe(this, Observer {})

        binding.edtEmail.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtEmail.error = null
                    binding.tilEdtEmail.isErrorEnabled = false
                }
            }
        })

        binding.edtPhoneNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtPhoneNumber.error = null
                    binding.tilEdtPhoneNumber.isErrorEnabled = false
                }
            }
        })

        binding.edtMessage.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtMessage.error = null
                    binding.tilEdtMessage.isErrorEnabled = false
                }
            }
        })

    }

    private fun setClickable() {

        binding.btnSubmitContactus.setOnClickListener {
            val fromEmail = binding.edtEmail.text.toString()
            val fromMobile = binding.edtPhoneNumber.text.toString()
            val message = binding.edtMessage.text.toString()

            if (!Validation.isValidEmail(fromEmail)) {
                binding.tilEdtEmail.error = resources.getString(R.string.VALIDATE_EMAIL)
            }

            val strDialingCode = "+60"
            if (!Validation.isValidPhoneNumber(fromMobile)) {
                binding.tilEdtPhoneNumber.error = resources.getString(R.string.VALIDATE_PHONE)
            }
            if (Validation.isEmpty(message)) {
                binding.tilEdtMessage.error = resources.getString(R.string.VALIDATE_MESSAGE)
            } else if (message.length < 5 || message.length > 1000) {
                binding.tilEdtMessage.error = resources.getString(R.string.ERROR_CONTACT_US_QUERY)
            }

            if ( !binding.tilEdtEmail.isErrorEnabled && !binding.tilEdtPhoneNumber.isErrorEnabled
                && !binding.tilEdtMessage.isErrorEnabled ) {
                if (NetworkUtility.isOnline(this)) {
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.CONTACT_US_SUBMIT)
                    viewModel.callContactUsApi(this,fromEmail,fromMobile,message)
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
                }
            }
        }

        binding.imgBackContactus.setOnClickListener {
            onBackPressed()
        }

    }

}
