package com.caressa.common.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class DialogHelper {
//    private companion object var instance: DialogHelper? = null
//
//    companion object {
//        fun getInstance(): DialogHelper? {
//            if (instance == null) {
//                instance = DialogHelper()
//            }
//            return instance
//        }
//    }

    fun showDatePickerDialog(
        title: String = "",
        context: Context,
        date: Calendar?,
        minDate: Calendar?,
        maxDate: Calendar?,
        listener: DateListener
    ) {
        val year = date!!.get(Calendar.YEAR)
        val month = date!!.get(Calendar.MONTH)
        val day = date!!.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val _monthOf = DateHelper.returnTwoDigitFromDate(monthOfYear + 1)
                val _monthOfDay = DateHelper.returnTwoDigitFromDate(dayOfMonth)

                val dob = "$year-$_monthOf-$_monthOfDay"
                val strDate = DateHelper.formatDateValue(dob)
                listener.onDateSet(
                    strDate!!,
                    year = year.toString(),
                    month = _monthOf,
                    dayOfMonth = _monthOfDay
                )

            }, year, month, day
        )
        dpd.setTitle(title)
        if (maxDate != null) {
            dpd.datePicker.maxDate = maxDate!!.timeInMillis
        }
        if (minDate != null) {
            dpd.datePicker.minDate = minDate!!.timeInMillis
        }
        dpd.show()
    }

    interface DateListener {
        fun onDateSet(date: String, year: String, month: String, dayOfMonth: String)
    }
}