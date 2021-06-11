package com.caressa.common.base

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.caressa.common.R
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.extension.setupSnackbar
import com.caressa.common.extension.setupSnackbarMessenger
import com.caressa.common.extension.setupToast
import com.caressa.common.extension.setupToastError
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Event
import com.caressa.navigation.NavigationCommand
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment() {

    private val progressDialog by lazy {
//        ProgressDialog(context)
        CustomProgressBar(requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeNavigation(getViewModel())
        observeSession(getViewModel())
        setupSnackbar(this, getViewModel().snackBarError, Snackbar.LENGTH_LONG)
        setupSnackbarMessenger(this, getViewModel().snackMessenger, Snackbar.LENGTH_LONG)
        setupToast(this, getViewModel().toastError, Toast.LENGTH_LONG)
        setupToastError(this, getViewModel().toastMessage, Toast.LENGTH_LONG)
        setUpProgressBar(this, getViewModel().progressBar)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
    }

    private fun observeSession(viewModel: BaseViewModel) {
        viewModel.sessionError.observe(viewLifecycleOwner, {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = resources.getString(R.string.SESSION)
            dialogData.message = resources.getString(R.string.MSG_SESSION_EXPIRED)
            dialogData.showLeftButton = false
            dialogData.showDismiss = false
            val defaultNotificationDialog = DefaultNotificationDialog(context,
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
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
/*            val alertDialog = AlertDialog.Builder(activity).create()
            alertDialog.setTitle(resources.getString(R.string.SESSION))
            alertDialog.setMessage(resources.getString(R.string.MSG_SESSION_EXPIRED))
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.OK)) { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            alertDialog.show()*/
        })
    }

    fun setUpProgressBar(lifecycleOwner: LifecycleOwner, progressBar: LiveData<Event<String>>) {
        progressBar.observe(lifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                if (it.equals(Event.HIDE_PROGRESS, true))
                    showProgress(false)
                else
                    showProgress(true, it)
            }
        })
    }

    abstract fun getViewModel(): BaseViewModel

    // UTILS METHODS ---

    /**
     * Observe a [NavigationCommand] [Event] [LiveData].
     * When this [LiveData] is updated, [Fragment] will navigate to its destination
     */
    private fun observeNavigation(viewModel: BaseViewModel) {
        viewModel.navigation.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is NavigationCommand.To -> findNavController().navigate(
                        command.directions,
                        getExtras()
                    )
                    is NavigationCommand.Back -> findNavController().navigateUp()
                }
            }
        })
    }

    private fun showProgress(showProgress: Boolean, message: String = "Loading...") {
        try {
            if (showProgress) {
//                progressDialog.setMessage(message)
//                progressDialog.isIndeterminate = false
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

    /**
     * [FragmentNavigatorExtras] mainly used to enable Shared Element transition
     */
    open fun getExtras(): FragmentNavigator.Extras = FragmentNavigatorExtras()

}