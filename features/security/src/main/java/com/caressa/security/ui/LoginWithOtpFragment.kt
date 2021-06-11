package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.security.databinding.FragmentLoginWithOtpBinding
import org.koin.android.viewmodel.ext.android.viewModel
import com.caressa.security.viewmodel.LoginWithOtpViewModel
import com.caressa.security.R

class LoginWithOtpFragment : BaseFragment() {

    private val viewModel: LoginWithOtpViewModel by viewModel()
    lateinit var binding: FragmentLoginWithOtpBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginWithOtpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        return binding.root
    }

    private fun setClickable() {

        binding.btnBack.setOnClickListener {

        }

        binding.btnContinue.setOnClickListener {

        }

    }

}
