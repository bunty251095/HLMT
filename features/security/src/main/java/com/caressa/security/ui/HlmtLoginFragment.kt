package com.caressa.security.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.security.R
import com.caressa.security.databinding.FragmentHlmtLoginBinding
import com.caressa.security.databinding.FragmentHlmtStepOneBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.caressa.security.viewmodel.LoginViewModel
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_hlmt_login.*
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

        viewModel.hlmt360LoginResponse.observe(viewLifecycleOwner,{})

//       binding.btnLogin.setOnClickListener {
//           viewModel.checkLoginNameExistOrNot(username = binding.edtUsername.text.toString(),passwordStr = binding.edtLoginPassword.text.toString())
//       }
        binding.imgSubmit.setOnClickListener {
            viewModel.checkLoginNameExistOrNot(username = binding.edtUsername.text.toString(),passwordStr = binding.edtLoginPassword.text.toString())
        }
    }




}