package com.caressa.security.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.security.R
import com.caressa.security.databinding.FragmentHlmtStepOneBinding
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.android.viewmodel.ext.android.viewModel

class HlmtStepOneFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentHlmtStepOneBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHlmtStepOneBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        googlePlusInit()
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

    private fun googlePlusInit() {
//        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.google_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(
//            activity as SecurityActivity,
//            googleSignInOptions)
//        googleSignOut()
        FitnessDataManager(context).signOutGoogleAccount()
    }

    private fun googleSignOut() {
        try {
            googleSignInClient.signOut()
        }catch (e: Exception){e.printStackTrace()}
    }

}