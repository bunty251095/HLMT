package com.caressa.security.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.security.R
import com.caressa.security.databinding.FragmentHlmtLoginBinding
import com.caressa.security.databinding.FragmentHlmtStepOneBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.caressa.security.viewmodel.LoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HlmtLoginFragment : BaseFragment() {

    private val viewModel: HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentHlmtLoginBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHlmtLoginBinding.inflate(inflater, container, false)
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

       binding.btnLogin.setOnClickListener {
           viewModel.checkLoginNameExistOrNot(name = "",username = binding.edtLoginEmailaddress.text.toString(),passwordStr = binding.edtLoginPassword.text.toString())
//           viewModel.callLogin(true,"Mayuresh",binding.edtLoginEmailaddress.text.toString(),binding.edtLoginPassword.text.toString())
       }
    }
}