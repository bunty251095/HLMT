package com.caressa.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.caressa.common.R
import java.util.*


inline fun Activity.alert(
    layoutResource: Int = 0,
    title: CharSequence? = null,
    message: CharSequence? = null,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    return AlertDialogHelper(this, title, message).apply {
        func()
    }.create()
}

inline fun Activity.alert(
    layoutResource: Int = 0,
    titleResource: Int = 0,
    messageResource: Int = 0,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    val title = if (titleResource == 0) null else getString(titleResource)
    val message = if (messageResource == 0) null else getString(messageResource)
    return AlertDialogHelper(this, title, message).apply {
        func()
    }.create()
}

fun Activity.showDialog(
    listener: DefaultNotificationDialog.OnDialogValueListener,
    title: String = "",
    message: String = "",
    leftText: String = "Cancel",
    rightText: String = "Ok",
    showLeftBtn: Boolean = true,
    showDismiss: Boolean = true
) {

    val dialogData = DefaultNotificationDialog.DialogData()
    dialogData.title = title
    dialogData.message = message
    dialogData.btnLeftName = leftText
    dialogData.btnRightName = rightText
    dialogData.showLeftButton = showLeftBtn
    val defaultNotificationDialog = DefaultNotificationDialog(this, listener, dialogData)
    Objects.requireNonNull(defaultNotificationDialog.window)!!
        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    defaultNotificationDialog.show()
}

fun Fragment.showDialog(
    listener: DefaultNotificationDialog.OnDialogValueListener,
    title: String = "",
    message: String = "",
    leftText: String = "Cancel",
    rightText: String = "Ok",
    showLeftBtn: Boolean = true,
    showDismiss: Boolean = true
) {

    val dialogData = DefaultNotificationDialog.DialogData()
    dialogData.title = title
    dialogData.message = message
    dialogData.btnLeftName = leftText
    dialogData.btnRightName = rightText
    dialogData.showLeftButton = showLeftBtn
    val defaultNotificationDialog = DefaultNotificationDialog(context, listener, dialogData)
    Objects.requireNonNull(defaultNotificationDialog.window)!!
        .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    defaultNotificationDialog.show()
}

inline fun Fragment.alert(
    layoutResource: Int = 0,
    title: CharSequence? = null,
    message: CharSequence? = null,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    return AlertDialogHelper(requireContext(), title, message).apply {
        func()
    }.create()
}

inline fun Fragment.alert(
    layoutResource: Int = 0,
    title: CharSequence? = null,
    message: CharSequence? = null
): AlertDialog {
    return AlertDialogHelper(requireContext(), title, message).create()
}

fun Fragment.showDialogList(view: TextView, title: CharSequence?, list: Array<String>?) {
    val builder = AlertDialog.Builder(view.context)
    builder.setTitle(Html.fromHtml("<font color='#0E5BA3'>$title</font>"))

    builder.setItems(list, DialogInterface.OnClickListener { dialog, which ->
        view.text = list!![which]
        dialog.dismiss()
    })

    builder.show()
}

interface OnTimeSetListener {
    fun onTimeSet(var2: String)
}

fun Fragment.showDatePickerDialog(view: TextView) {
    try {
        val mCurrentDateTime = Calendar.getInstance()
        val mDatePicker = DatePickerDialog(
            view.context,
            R.style.MyDialogTheme,
            DatePickerDialog.OnDateSetListener { datepicker, selectedyear, selectedmonth, selectedday ->
                var selectedmonth = selectedmonth
                selectedmonth = selectedmonth + 1
                val date = getFormattedDate("" + selectedyear, "" + selectedmonth, "" + selectedday)
                val dateDiff = DateHelper.getDateDifferenceInDays(
                    date,
                    DateHelper.getDateAs_ddmmyy(DateHelper.currentDateAsStringddMMMyyyy)
                )
                if (dateDiff >= 30 || dateDiff <= -30) {
                } else
                    view.text = date
            },
            mCurrentDateTime.get(Calendar.YEAR),
            mCurrentDateTime.get(Calendar.MONTH),
            mCurrentDateTime.get(Calendar.DAY_OF_MONTH)
        )
        mDatePicker.setTitle("Select Date")
        mDatePicker.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun Fragment.showTimePickerDialog(listener: OnTimeSetListener) {
    try {
        val mCurrentTime = Calendar.getInstance()
        val mTimePicker = TimePickerDialog(
            context, R.style.MyDialogTheme,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                val time = getFormattedTime("" + selectedHour, "" + selectedMinute)
                listener.onTimeSet(time)
            }, mCurrentTime.get(Calendar.HOUR_OF_DAY), mCurrentTime.get(Calendar.MINUTE), true
        )
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    } catch (var2: Exception) {
        var2.printStackTrace()
    }

}

fun getFormattedTime(hour: String, minute: String): String {
    var hour = hour
    var minute = minute
    if (hour.length == 1) {
        hour = "0$hour"
    }

    if (minute.length == 1) {
        minute = "0$minute"
    }

    return "$hour:$minute"
}

private fun getFormattedDate(
    selectedyear: String,
    selectedmonth: String,
    selectedday: String
): String {
    var selectedmonth = selectedmonth
    var selectedday = selectedday

    if (selectedday.length == 1) {
        selectedday = "0$selectedday"
    }
    if (selectedmonth.length == 1) {
        selectedmonth = "0$selectedmonth"
    }
    return "$selectedday/$selectedmonth/$selectedyear"
}

inline fun Fragment.alert(
    layoutResource: Int = 0,
    titleResource: Int = 0,
    messageResource: Int = 0,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    val title = if (titleResource == 0) null else getString(titleResource)
    val message = if (messageResource == 0) null else getString(messageResource)
    return AlertDialogHelper(requireContext(), title, message).apply {
        func()
    }.create()
}

@SuppressLint("InflateParams")
class AlertDialogHelper(context: Context, title: CharSequence?, message: CharSequence?) {

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)

    /*  private val dialogView: View by lazy {
          LayoutInflater.from(context).inflate(layoutResource, null)
      }

      private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
              .setView(dialogView)

      private val title: TextView by lazy {
          dialogView.findViewById<TextView>(R.id.dialogInfoTitleTextView)
      }

      private val message: TextView by lazy {
          dialogView.findViewById<TextView>(R.id.dialogInfoMessageTextView)
      }

      private val positiveButton: Button by lazy {
          dialogView.findViewById<Button>(R.id.dialogInfoPositiveButton)
      }

      private val negativeButton: Button by lazy {
          dialogView.findViewById<Button>(R.id.dialogInfoNegativeButton)
      }*/

    private var dialog: AlertDialog? = null

    var cancelable: Boolean = true

    fun positiveButton(@StringRes textResource: Int, func: (() -> Unit)? = null) {
        apply {
            func?.invoke()
            dialog?.dismiss()
        }
    }

    fun positiveButton(text: CharSequence, func: (() -> Unit)? = null) {
        apply {
            func?.invoke()
            dialog?.dismiss()
        }
    }

    fun negativeButton(@StringRes textResource: Int, func: (() -> Unit)? = null) {
        apply {
            func?.invoke()
            dialog?.dismiss()
        }
    }

    fun negativeButton(text: CharSequence, func: (() -> Unit)? = null) {
        apply {
            func?.invoke()
            dialog?.dismiss()
        }
    }

    fun onCancel(func: () -> Unit) {
        builder.setOnCancelListener { func() }
    }

    fun create(): AlertDialog {
//        title.goneIfTextEmpty()
//        message.goneIfTextEmpty()
//        positiveButton.goneIfTextEmpty()
//        negativeButton.goneIfTextEmpty()

        dialog = builder
            .setCancelable(cancelable)
            .create()
        return dialog!!
    }

    private fun TextView.goneIfTextEmpty() {
        visibility = if (text.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /*private fun setClickListenerToDialogButton(func: (() -> Unit)?) {
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
    }*/

}
