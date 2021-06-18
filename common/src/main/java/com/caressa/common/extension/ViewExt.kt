package com.caressa.common.extension

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.caressa.common.R
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Event
import com.google.android.material.snackbar.Snackbar

private val appColorHelper = AppColorHelper.instance!!

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun Fragment.showSnackbar(snackbarText: String, timeLength: Int) {
    activity?.let {
        Snackbar.make(
            it.findViewById<View>(android.R.id.content),
            snackbarText,
            timeLength
        ).show()
    }
}

fun Fragment.showToast(toastText: String, timeLength: Int) {
    activity?.let {
        val toast = Toast.makeText(context, toastText, timeLength)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            toast.setGravity(Gravity.CENTER, 0, 0)
            val view = toast.view
            view?.background?.colorFilter = PorterDuffColorFilter(appColorHelper.primaryColor(), PorterDuff.Mode.SRC_IN)
            val text = view?.findViewById<TextView>(android.R.id.message)
            text?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        toast.show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun Fragment.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let { res ->
            context?.let { showSnackbar(it.getString(res), timeLength) }
        }
    })
}

fun Fragment.setupSnackbarMessenger(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<String>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showSnackbar(data, timeLength) }
        }
    })
}

fun Fragment.setupToast(
    lifecycleOwner: LifecycleOwner,
    toastEvent: LiveData<Event<String>>,
    timeLength: Int
) {
    toastEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showToast(data, timeLength) }
        }
    })
}

fun Fragment.setupToastError(
    lifecycleOwner: LifecycleOwner,
    toastEvent: LiveData<Event<Int>>,
    timeLength: Int
) {
    toastEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let { data ->
            context?.let { showToast(it.getString(data), timeLength) }
        }
    })
}