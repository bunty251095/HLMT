package com.caressa.hra.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.caressa.hra.R
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
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
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        val welcomeMsg = resources.getString(R.string.TEXT_START_ASSESSMENT,viewModel.firstName)
        binding.lblIntroMsg.setHtmlText( welcomeMsg )

    }

    private fun setClickable() {

            binding.btnStartHra.setOnClickListener {
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_INITIATED_EVENT)
            it.findNavController().navigate(R.id.action_introductionFragment_to_selectFamilyMemberFragment)
        }

    }
}
