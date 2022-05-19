package com.caressa.security.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.*
import com.caressa.security.R
import com.caressa.security.databinding.FragmentChangePasswordBinding
import com.caressa.security.model.UserInfo
import com.caressa.security.viewmodel.ForgetPasswordViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ChangePasswordFragment : BaseFragment(), DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: ForgetPasswordViewModel by viewModel()
    private lateinit var binding: FragmentChangePasswordBinding

    var phone = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

//        dialogPasswordUpdated = DialogSuccess(requireContext())
//        dialogPasswordUpdated!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        btnGoToApp = dialogPasswordUpdated!!.findViewById(R.id.btn_go_to_app)

        viewModel.updatePassword.observe(viewLifecycleOwner, {})
        viewModel.resetPassword.observe(viewLifecycleOwner,{})
        viewModel.loginResponse.observe(viewLifecycleOwner,{})
    }

    private fun setClickable() {

        binding.btnResetPassword.setOnClickListener {
            validate()
            //showPasswordUpdatedDialog()
        }

        binding.imgPasswordInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.PASSWORD_CRITERIA),
                message = Html.fromHtml("<a>" +  "- ${resources.getString(R.string.PASSWORD_CRITERIA_DESC1)} <br/><br/> - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC2)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC3)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC4)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC5)} <br/> \t\t - ${resources.getString(R.string.PASSWORD_CRITERIA_DESC6)}" + "</a>").toString(),
                showLeftBtn = false)
        }


        binding.imgDone.setOnClickListener {
//            it.findNavController().navigate(R.id.action_changePasswordFragment_to_loginFragment)
        }

    }

    private fun validate() {
        val newPassword: String = binding.edtNewPassword.text.toString()
        val confirmPassword: String = binding.edtReenterNewPassword.text.toString()

        binding.edtReenterNewPassword.transformationMethod = null

        if ( Utilities.isNullOrEmpty(newPassword) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMPTY_NEW_PASSWORD))
        } else if ( Utilities.isNullOrEmpty(confirmPassword) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMPTY_CONFIRM_PASSWORD))
        } else if ( !Validation.isValidPassword(newPassword) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_NEW_PASSWORD))
        } else if ( !Validation.isValidPassword(confirmPassword) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_CONFIRM_PASSWORD))
        } else if ( !newPassword.equals(confirmPassword, ignoreCase = true) ) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_PASSWORD_DOES_NOT_MATCH))
        } else {
            viewModel.callResetPassword( UserInfo.emailAddress, confirmPassword)
            //showPasswordUpdatedDialog()
        }
    }

    fun showPasswordUpdatedDialog() {
//        dialogPasswordUpdated!!.show()
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}
}
