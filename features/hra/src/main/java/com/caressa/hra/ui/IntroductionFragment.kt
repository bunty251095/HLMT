package com.caressa.hra.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.caressa.hra.R
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.FirebaseHelper
import com.caressa.hra.viewmodel.HraViewModel
import com.caressa.hra.databinding.FragmentIntroductionBinding
import org.koin.android.viewmodel.ext.android.viewModel

class IntroductionFragment : BaseFragment() {

    private val viewModel: HraViewModel by viewModel()
    private lateinit var binding : FragmentIntroductionBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            FirebaseHelper.logScreenEvent(FirebaseConstants.HRA_START_SCREEN)
            initialise()
            setClickable()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {

        val welcomeMsg = resources.getString(R.string.TEXT_START_ASSESSMENT,viewModel.firstName)
        binding.lblIntroMsg.setHtmlText( welcomeMsg )

    }

    private fun setClickable() {

            binding.btnStartHra.setOnClickListener {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = resources.getString(R.string.DISCLAIMER_TITLE)
                dialogData.message = resources.getString(R.string.DISCLAIMER_MESSAGE_HRA)
                dialogData.btnRightName = resources.getString(R.string.CONTINUE)
                dialogData.showLeftButton = false
                val defaultNotificationDialog = DefaultNotificationDialog(requireContext(),
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_INITIATED)
                                it.findNavController().navigate(R.id.action_introductionFragment_to_selectFamilyMemberFragment)
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
        }

    }
}
