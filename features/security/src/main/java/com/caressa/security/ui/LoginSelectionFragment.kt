package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.security.databinding.FragmentAccountSelectionBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.koin.android.viewmodel.ext.android.viewModel

class LoginSelectionFragment: BaseFragment() {

    private val viewModel: HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentAccountSelectionBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountSelectionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setClickable()
        googlePlusInit()
        return binding.root
    }

    private fun setClickable() {
        viewModel.updateUserPreference()
        binding.btnRegister.setOnClickListener {
            viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelctionToStepOneFragment())
        }
        binding.btnSignIn.setOnClickListener {
            viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelctionToStepOneFragment())
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