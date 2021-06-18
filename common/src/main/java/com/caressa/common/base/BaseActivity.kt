package com.caressa.common.base

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.caressa.common.R
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.navigation.NavigationCommand
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    private val progressDialog by lazy {
        //ProgressDialog(this)
        CustomProgressBar(this)
    }

    private val sessionExpiryDialog by lazy {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.SESSION)
        dialogData.message = resources.getString(R.string.MSG_SESSION_EXPIRED)
        dialogData.showLeftButton = false
        dialogData.showDismiss = false
        DefaultNotificationDialog(this,
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        val intent = Intent()
                        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }, dialogData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateEvent(savedInstanceState)
        initBase()
    }

    fun initBase() {
        Timber.i("initBase()=> ")
        observeSession(getViewModel())
        setupSnackbar(this, getViewModel().snackBarError, Snackbar.LENGTH_LONG)
        setupSnackbarMessenger(this, getViewModel().snackMessenger, Snackbar.LENGTH_LONG)
        setupToast(this, getViewModel().toastMessage)

        setUpProgressBar(this, getViewModel().progressBar)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
    }

    fun setupToast(lifecycleOwner: LifecycleOwner, toastEvent: LiveData<Event<String>>) {
        toastEvent.observe(lifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { data ->
                showToast(data)
            }
        })
    }

    private fun showToast(data: String) {
        Utilities.toastMessageShort(this,data)
    }

    private fun setUpProgressBar(lifecycleOwner: LifecycleOwner, progressBar: LiveData<Event<String>>, message: String = "Loading...") {
        progressBar.observe(lifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                //                if(it.equals(Event.HIDE_PROGRESS,true))
//                    showProgress(false)
//                else
                //showProgress(true, it)

                if (it.equals(Event.HIDE_PROGRESS, true))
                    showProgress(false)
                else
                    showProgress(true, it)
            }
        })
    }

    private fun showProgress(showProgress: Boolean, message: String = "Loading...") {
        try {
            if (showProgress) {
                //progressDialog.setMessage(message)
                //progressDialog.isIndeterminate = false
                progressDialog.setCancelable(false)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
            } else {
                if (progressDialog.isShowing) run {
                    progressDialog.cancel()
                    progressDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupSnackbar(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<Int>>,
        timeLength: Int
    ) {
        snackbarEvent.observe(lifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { res ->
                showSnackbar(this.getString(res), timeLength)
            }
        })
    }

    fun setupSnackbarMessenger(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<String>>,
        timeLength: Int
    ) {
        snackbarEvent.observe(lifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                showSnackbar(it, timeLength)
            }
        })
    }

    private fun showSnackbar(message: String, timeLength: Int) {
        Snackbar.make(this.findViewById(android.R.id.content), message, timeLength).show()
    }

    private fun observeSession(viewModel: BaseViewModel) {
        viewModel.sessionError.observe(this, {
            sessionExpiryDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            sessionExpiryDialog.show()
        })
    }

    abstract fun getViewModel(): BaseViewModel

    protected abstract fun onCreateEvent(savedInstanceState: Bundle?)

    fun navigationController(navController: NavController) {
        getViewModel().navigation.observe(this, {
            it?.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is NavigationCommand.To -> navController.navigate(
                        command.directions
                    )
                    is NavigationCommand.Back -> navController.navigateUp()
                }
            }
        })
    }

}