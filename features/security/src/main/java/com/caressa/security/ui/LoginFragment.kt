package com.caressa.security.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.extension.showSnackbar
import com.caressa.common.utils.Validation
import com.caressa.security.R
import com.caressa.security.databinding.FragmentLoginBinding
import com.caressa.security.viewmodel.LoginViewModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding

    private lateinit var mCallbackManager: CallbackManager
    private var accessToken: AccessToken? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 1000
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        val strTermsOfServices = "<u><a><B><font color='#797e7d'>"+ resources.getString(R.string.VIVANT_TERMS_CONDITIONS) +"</font></B></a></u>"

        binding.txtLoginTermsofservice.text = Html.fromHtml(resources.getString(R.string.TERMS_OF_SERVICE_LOGIN) + "<br/>"
                + strTermsOfServices + " " + resources.getString(R.string.YOU_HAVE_READ_THEM))

        binding.edtLoginEmailaddress.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (Validation.isValidEmail(editable.toString()) || Validation.isValidPhoneNumber(editable.toString())) {
                    binding.imgValidation.visibility = View.VISIBLE
                } else {
                    binding.imgValidation.visibility = View.GONE
                }
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtLoginEmailPhone.error = null
                    binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                }
/*                if (editable.toString().length > 3) {
                    if (TextUtils.isDigitsOnly(editable.toString())) {
                        if (!Validation.isValidPhoneNumber(editable.toString())) {
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                            binding.tilEdtLoginEmailPhone.error = "Please Enter valid Phone Number"
                        } else {
                            binding.tilEdtLoginEmailPhone.error = null
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                        }
                    } else {
                        if (!editable.toString().contains("@")) {
                            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                            binding.tilEdtLoginEmailPhone.error = "Email address must include @"
                        } else {
                            if (!Validation.isValidEmail(editable.toString())) {
                                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                                binding.tilEdtLoginEmailPhone.error = "Please Enter valid Email"
                            } else {
                                binding.tilEdtLoginEmailPhone.error = null
                                binding.tilEdtLoginEmailPhone.isErrorEnabled = false
                            }
                        }
                    }
                }*/
            }
        })

        binding.edtLoginPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtLoginPassword.error = null
                    binding.tilEdtLoginPassword.isErrorEnabled = false
                }
            }
        })

        firebaseAuth = FirebaseAuth.getInstance()
        accessToken = AccessToken.getCurrentAccessToken()
        disconnectFromFacebook()
        facebookInit()
        googlePlusInit()
    }


    private fun setClickable() {

        binding.btnLogin.setOnClickListener {
            validateLogin()
        }

        binding.btnLoginFacebook.setOnClickListener {
            logoutFacebook()
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList<String>("public_profile", "email"))
        }
        binding.btnLoginGoogleplus.setOnClickListener {
            googleSignOut()
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        binding.tvLoginSignup.setOnClickListener {
            it.findNavController().navigate(R.id.next_sign_up)
        }

        binding.tvForgotPassword.setOnClickListener {
            it.findNavController().navigate(R.id.action_login_fragment_to_forgotPasswordFragment)
        }

        binding.txtLoginTermsofservice.setOnClickListener {
            it.findNavController().navigate(R.id.action_login_terms)
        }

        viewModel.isEmail.observe( viewLifecycleOwner , Observer {})
        viewModel.isPhone.observe( viewLifecycleOwner , Observer {})
        viewModel.user.observe( viewLifecycleOwner , Observer {})
    }

    private fun validateLogin() {
        val emailStr = binding.edtLoginEmailaddress.text.toString().trim()
        val passwordStr = binding.edtLoginPassword.text.toString().trim()

        if ( TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(passwordStr) ) {
            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
            binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            binding.tilEdtLoginPassword.isErrorEnabled = true
            binding.tilEdtLoginPassword.error = requireContext().resources.getString(R.string.ERROR_INVALID_PASSWORD)
        } else if ( TextUtils.isEmpty(emailStr) ) {
            binding.tilEdtLoginEmailPhone.isErrorEnabled = true
            binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
        } else if ( TextUtils.isEmpty(passwordStr) ) {
            binding.tilEdtLoginPassword.isErrorEnabled = true
            binding.tilEdtLoginPassword.error = requireContext().resources.getString(R.string.ERROR_INVALID_PASSWORD)
        } else if( emailStr.contains("@", true) ) {
            if(Validation.isValidEmail(emailStr)){
                viewModel.callLogin(
                    forceRefresh = true,
                    emailStr = emailStr,
                    passwordStr = passwordStr)
                // Bypass for social login
                // viewModel.callLogin(forceRefresh = true, name = "Keyur", emailStr = emailStr, passwordStr = "123456", socialLogin = true, socialId = "12335555")
            }else{
                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            }
        } else {
            if(emailStr.isNullOrEmpty()) {
                binding.tilEdtLoginEmailPhone.isErrorEnabled = true
                binding.tilEdtLoginEmailPhone.error = requireContext().resources.getString(R.string.VALIDATE_EMAIL)
            }else if(Validation.isValidPhoneNumber(emailStr)){
                viewModel.checkPhoneExistAPI(phoneNumber = emailStr, passwordStr = passwordStr)
            }
        }
    }

    private fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null){
            GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
        }
    }

    private fun facebookInit() {
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(
            mCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    accessToken = loginResult.accessToken
                    firebaseAuthWithFacebook(loginResult.accessToken)
                }

                override fun onCancel() {
                    logoutFacebook()
                }

                override fun onError(e: FacebookException) {
                    logoutFacebook()
                }
            })
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Timber.i("firebaseAuthWithGoogle "+acct.email)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity as SecurityActivity) { task ->
                    if (task.isSuccessful) {
                        val strEmail: String?
                        val googleId: String
                        var strName: String? = null
                        try {
                            val profile = task.result!!.additionalUserInfo!!.profile
                            val email = profile!!["email"]
                            val user = firebaseAuth.currentUser
                            strEmail = email.toString()//user?.email
                            googleId = user!!.uid
                            strName = Objects.requireNonNull<String>(strEmail).split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                            if (user.displayName != null) {
                                strName = user.displayName
                            }
                            viewModel.checkEmailExistOrNot(
                                name = strName!!,
                                emailStr = strEmail,
                                socialLogin = true,
                                socialId = googleId)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Timber.e(task.exception?.message)
                        googleSignOut()
                        showSnackbar(resources.getString(R.string.GOOGLE_SIGN_IN_FAILED), Snackbar.LENGTH_SHORT)
                    }

        }
    }

    private fun googlePlusInit() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(
            activity as SecurityActivity,
            googleSignInOptions)
        googleSignOut()
    }

    private fun googleSignOut() {
        try {
            googleSignInClient.signOut()
        }catch (e: Exception){e.printStackTrace()}
    }


    private fun logoutFacebook() {
        // Facebook sign out
        GraphRequest(
            accessToken,
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
        LoginManager.getInstance().logOut()
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        Timber.e("FB ACCESS TOKEN" + token.token)

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity as SecurityActivity) { task ->
                if (task.isSuccessful) {
                    val strEmail: String?
                    val facebookId: String
                    var strName: String? = null
                    try {
                        val profile = task.result!!.additionalUserInfo!!.profile
                        val email = profile!!["email"]
                        val user = firebaseAuth.currentUser
                        strEmail = email.toString()//user?.email
                        facebookId = user!!.uid
                        strName = Objects.requireNonNull<String>(strEmail).split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        if (user.displayName != null) {
                            strName = user.displayName
                        }
                        viewModel.checkEmailExistOrNot(
                            name = strName!!,
                            emailStr = strEmail,
                            socialLogin = true,
                            socialId = facebookId
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Timber.e(task.exception?.message)
                    logoutFacebook()
                    showSnackbar(
                        resources.getString(R.string.FB_SIGN_IN_FAILED),
                        Snackbar.LENGTH_SHORT
                    );
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                googleSignOut()
            }
        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

}
