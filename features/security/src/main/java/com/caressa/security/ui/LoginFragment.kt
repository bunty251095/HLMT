package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.security.databinding.FragmentHlmtLoginBinding
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginFragment : BaseFragment() {

    private val viewModel: HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        return binding.root
    }

    private fun setClickable() {
        viewModel.isLoginName.observe(viewLifecycleOwner, Observer {
            Timber.i("LoginNameExistData=> "+it)
        })

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            Timber.i("Login Response=> "+it)
        })

        viewModel.hlmt360LoginResponse.observe(viewLifecycleOwner,{})

//       binding.btnLogin.setOnClickListener {
//           viewModel.checkLoginNameExistOrNot(username = binding.edtUsername.text.toString(),passwordStr = binding.edtLoginPassword.text.toString())
//       }
        binding.imgSubmit.setOnClickListener {
            viewModel.checkLoginNameExistOrNot(username = binding.edtUsername.text.toString(),passwordStr = binding.edtLoginPassword.text.toString())
        }
        binding.tvForgotPassword.setOnClickListener {
            viewModel.navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
        }
    }




}