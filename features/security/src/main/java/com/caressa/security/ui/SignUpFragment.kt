package com.caressa.security.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.extension.showSnackbar
import com.caressa.common.utils.*
import com.caressa.security.R
import com.caressa.security.databinding.FragmentSignUpBinding
import com.caressa.security.viewmodel.SignUpViewModel
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

 class SignUpFragment : BaseFragment() , DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: SignUpViewModel by viewModel()
    private lateinit var binding: FragmentSignUpBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mCallbackManager: CallbackManager
    private var accessToken: AccessToken? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialiseView()
        setClickable()
        return binding.root
    }

    private fun initialiseView() {

        val strTermsOfServices = "<u><a><B><font color='#797e7d'>"+resources.getString(R.string.VIVANT_TERMS_CONDITIONS)+"</font></B></a></u>"
        binding.txtSignupTermsofservice.text = Html.fromHtml(resources.getString(R.string.TERMS_OF_SERVICE_SIGNUP)
                + "<br/>" + strTermsOfServices + " " + resources.getString(R.string.YOU_HAVE_READ_THEM))

        binding.edtSignupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtSignupName.error = null
                    binding.tilEdtSignupName.isErrorEnabled = false
                }
            }
        })

        binding.edtSignupPhone.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtSignupPhone.error = null
                    binding.tilEdtSignupPhone.isErrorEnabled = false
                }
            }
        })

        binding.edtSignupEmailaddress.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().length > 3) {
                    if (!editable.toString().contains("@")) {
                        binding.tilEdtSignupEmailaddress.setErrorEnabled(true)
                        binding.tilEdtSignupEmailaddress.error = resources.getString(R.string.ERROR_EMAIL_MUST_INCLUDE)
                    } else {
                        if (!Validation.isValidEmail(editable.toString())) {
                            binding.tilEdtSignupEmailaddress.isErrorEnabled = true
                            binding.tilEdtSignupEmailaddress.error = resources.getString(R.string.ERROR_INVALID_EMAIL)
                        } else {
                            binding.tilEdtSignupEmailaddress.error = null
                            binding.tilEdtSignupEmailaddress.isErrorEnabled = false
                        }
                    }
                }
                if (Validation.isValidEmail(editable.toString())) {
                    binding.imgValidationEmail.visibility = View.VISIBLE
                } else {
                    binding.imgValidationEmail.visibility = View.GONE
                }
                if (editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtSignupEmailaddress.error = null
                    binding.tilEdtSignupEmailaddress.isErrorEnabled = false
                }
            }
        })

        binding.edtSignupPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtSignupPassword.error = null
                    binding.tilEdtSignupPassword.isErrorEnabled = false
                }
                if (Validation.isValidPassword(editable.toString())) {
                    binding.imgTickPassword.visibility = View.VISIBLE
                    binding.tilEdtSignupPassword.error = null
                    binding.tilEdtSignupPassword.isErrorEnabled = false
                } else {
                    binding.imgTickPassword.visibility = View.GONE
                    binding.tilEdtSignupPassword.isErrorEnabled = true
                    binding.tilEdtSignupPassword.error = resources.getString(R.string.ERROR_PASSWORD_DOESNOT_MATCH_REQUIREMENTS)
                }
            }
        })

        mCallbackManager = CallbackManager.Factory.create()

        firebaseAuth = FirebaseAuth.getInstance()
        accessToken = AccessToken.getCurrentAccessToken()
        disconnectFromFacebook()
        googlePlusInit()
        facebookLoginInit()
        signOut()
    }

    private fun setClickable() {

        binding.imgPasswordInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.PASSWORD_CRITERIA),
                message = resources.getString(R.string.PASSWORD_CRITERIA_DESC),
                showLeftBtn = false)
        }

        binding.txtSignupTermsofservice.setOnClickListener {
            it.findNavController().navigate(R.id.action_signup_terms)
        }

        binding.btnSignup.setOnClickListener {
            validateSignUp()
        }

        binding.btnSignupFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList<String>(
                    "public_profile",
                    "email"))
        }

        binding.btnSignupGoogleplus.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, LoginFragment.RC_SIGN_IN)
        }

        viewModel.isEmail.observe( viewLifecycleOwner , Observer {})
        viewModel.isPhone.observe( viewLifecycleOwner , Observer {})
        viewModel.user.observe( viewLifecycleOwner , Observer {})
        viewModel.otpData.observe( viewLifecycleOwner , Observer {})
    }

    private fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {

        } else {
            GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
        }
    }

    private fun validateSignUp() {

        val nameStr = binding.edtSignupName.text.toString().trim()
        val phoneStr = binding.edtSignupPhone.text.toString().trim()
        val emailStr = binding.edtSignupEmailaddress.text.toString().trim()
        val passwordStr = binding.edtSignupPassword.text.toString().trim()

        if (Validation.isEmpty(nameStr) || !Validation.isValidName(nameStr)) {
            binding.tilEdtSignupName.isErrorEnabled = true
            binding.tilEdtSignupName.error = resources.getString(R.string.VALIDATE_NAME)
        }

        if (!Validation.isEmpty(phoneStr)) {
            if (!Validation.isValidPhoneNumber(phoneStr)) {
                binding.tilEdtSignupPhone.isErrorEnabled = true
                binding.tilEdtSignupPhone.error = resources.getString(R.string.VALIDATE_PHONE)
            }
        }

        if (Validation.isEmpty(emailStr)) {
            binding.tilEdtSignupEmailaddress.isErrorEnabled = true
            binding.tilEdtSignupEmailaddress.error = resources.getString(R.string.VALIDATE_EMAIL)
        }

        if (emailStr.length > 3) {
            if (!emailStr.contains("@")) {
                binding.tilEdtSignupEmailaddress.isErrorEnabled = true
                binding.tilEdtSignupEmailaddress.error = resources.getString(R.string.VALIDATE_EMAIL_MUST_INCLUDE)
            } else {
                if (!Validation.isValidEmail(emailStr)) {
                    binding.tilEdtSignupEmailaddress.isErrorEnabled = true
                    binding.tilEdtSignupEmailaddress.error = resources.getString(R.string.VALIDATE_EMAIL)
                } else {
                    binding.tilEdtSignupEmailaddress.isErrorEnabled = false
                    binding.tilEdtSignupEmailaddress.error = null
                }
            }
        }

        if (Validation.isEmpty(passwordStr) || !Validation.isValidPassword(passwordStr)) {
            binding.tilEdtSignupPassword.isErrorEnabled = true
            binding.tilEdtSignupPassword.error = resources.getString(R.string.ERROR_INVALID_PASSWORD)
        }

        if ( !binding.tilEdtSignupName.isErrorEnabled && !binding.tilEdtSignupPhone.isErrorEnabled
            && !binding.tilEdtSignupEmailaddress.isErrorEnabled && !binding.tilEdtSignupPassword.isErrorEnabled ) {
            KeyboardUtils.hideSoftInput(activity as SecurityActivity)
            viewModel.checkEmailExistOrNot(
                name = nameStr,
                emailStr = emailStr,
                passwordStr = passwordStr,
                phoneNumber = phoneStr)
        }
    }

    private fun googlePlusInit() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient =
            GoogleSignIn.getClient(activity as SecurityActivity, googleSignInOptions)
    }

    private fun facebookLoginInit() {
        LoginManager.getInstance()
            .registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    firebaseAuthWithFacebook(loginResult.accessToken)
                }

                override fun onCancel() {
                    signOut()
                    showSnackbar(
                        resources.getString(R.string.FB_SIGN_IN_FAILED),
                        Snackbar.LENGTH_LONG)
                }

                override fun onError(error: FacebookException) {
                    signOut()
                    showSnackbar(
                        resources.getString(R.string.FB_SIGN_IN_FAILED),
                        Snackbar.LENGTH_LONG)
                }
            })
    }

    private fun signOut() {
        // Google sign out
        googleSignInClient.signOut()

        // Facebook sign out
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
        LoginManager.getInstance().logOut()

        // Firebase sign out
        firebaseAuth.currentUser?.delete()
        firebaseAuth.signOut()
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        Timber.i("FB Token=> " + token)
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
                        strName =
                            Objects.requireNonNull<String>(strEmail).split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
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
                    signOut()
                    showSnackbar(
                        resources.getString(R.string.FB_SIGN_IN_FAILED),
                        Snackbar.LENGTH_LONG
                    )
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.i("onActivityResult  === " + resultCode + " :: " + requestCode)
        if (requestCode == LoginFragment.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                signOut()
                showSnackbar(
                    resources.getString(R.string.GOOGLE_SIGN_IN_FAILED),
                    Snackbar.LENGTH_LONG
                )
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Timber.i("firebaseAuthWithGoogle => " + acct.email)
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
                        strName =
                            Objects.requireNonNull<String>(strEmail).split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        if (user.displayName != null) {
                            strName = user.displayName
                        }
                        viewModel.checkEmailExistOrNot(
                            name = strName!!,
                            emailStr = strEmail,
                            socialLogin = true,
                            socialId = googleId
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Timber.e(task.exception?.message)
                    signOut()
                    showSnackbar(
                        resources.getString(R.string.GOOGLE_SIGN_IN_FAILED),
                        Snackbar.LENGTH_LONG
                    )
                }

            }
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}
