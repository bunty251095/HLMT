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
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class HlmtStepOneFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentHlmtStepOneBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHlmtStepOneBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        return binding.root
    }

    private fun setClickable() {
        binding.btnIDont.setOnClickListener {
            viewModel.navigate(HlmtStepOneFragmentDirections.actionStepOneFragmentToLoginWithOtpfragment())
        }
        binding.btnHaveAccount.setOnClickListener {
            viewModel.navigate(HlmtStepOneFragmentDirections.actionStepOneFragmentToHLMTLoginFragment())
        }
    }

}