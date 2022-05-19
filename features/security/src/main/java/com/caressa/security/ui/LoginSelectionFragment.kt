package com.caressa.security.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.security.R
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
        init()
        setClickable()
        googlePlusInit()
        return binding.root
    }

    private fun init() {
        val strTermsOfServices =
            "<u><a><B><font color='#0273ff'>" + resources.getString(R.string.VIVANT_TERMS_CONDITIONS) + "</font></B></a></u>"

        val strPrivacyPolicy =
            "<u><a><B><font color='#0273ff'>" + resources.getString(R.string.PRIVACY_POLICY) + "</font></B></a></u>"

        binding.txtLoginTermsofservice.text = Html.fromHtml(
            resources.getString(R.string.TERMS_OF_SERVICE_LOGIN) + "<br/>"
                    + strTermsOfServices +" "+ resources.getString(R.string.AND)
        )
        binding.txtLoginPrivacyPolicy.text = Html.fromHtml(
             strPrivacyPolicy + " " + resources.getString(R.string.YOU_HAVE_READ_THEM)
        )
    }

    private fun setClickable() {
        viewModel.updateUserPreference()
        binding.btnRegister.setOnClickListener {
            if(binding.termsCheckBox.isChecked) {
                viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelectionFragmentToRegistrationFragment())
            }else{
                viewModel.toastMessage("Please accept Terms and Conditions")
            }
        }
        binding.btnSignIn.setOnClickListener {
            if(binding.termsCheckBox.isChecked) {
                viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelectionFragmentToLoginFragment())
            }else{
                viewModel.toastMessage("Please accept Terms and Conditions")
            }
        }
        binding.txtLoginTermsofservice.setOnClickListener {
            viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelectionFragmentToTermsAndConditionFragment())
        }
        binding.txtLoginPrivacyPolicy.setOnClickListener {
            viewModel.navigate(LoginSelectionFragmentDirections.actionLoginSelectionFragmentToPrivacyPolicyFragment())
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