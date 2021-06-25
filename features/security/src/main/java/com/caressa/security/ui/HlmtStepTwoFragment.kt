package com.caressa.security.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.security.R
import com.caressa.security.databinding.FragmentHlmtStepOneBinding
import com.caressa.security.databinding.FragmentHlmtStepTwoBinding
import com.caressa.security.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class HlmtStepTwoFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentHlmtStepTwoBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHlmtStepTwoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        return binding.root
    }

    private fun setClickable() {
        binding.btnLogin.setOnClickListener {
//            viewModel.navigate(HlmtStepTwoFragmentDirections.actionStepTwoFragmentToLoginWithOtpFragment())
        }
    }

}