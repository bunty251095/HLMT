package com.caressa.hra.common

import android.content.Context
import android.widget.CheckBox
import com.caressa.common.utils.Utilities
import com.caressa.common.view.FlowLayout
import timber.log.Timber

object Validations {

     fun validationBMI(height: Double, weight: Double, context: Context ): Boolean {
         Timber.i("Height , Weight----->$height $weight")
        var isValid = false

         when {

             (height in 120.0..240.0 && weight in 30.0..150.0) -> {
                 isValid = true
             }

             (height <= 0) -> {
                 isValid = false
             }

             (weight <= 0) -> {
                 isValid = false
             }

             (height < 120 || height > 240) -> {
                 Utilities.toastMessageLong(context, "Please Select valid height between " + 120 + "-" + 240)
                 isValid = false
             }

             (weight < 30 || weight > 150) -> {
                 Utilities.toastMessageLong(context, "Please Select valid weight between " + 30 + "-" + 150)
                 isValid = false
             }

         }
        return isValid
    }

     fun validateBP(systolic: Int, diastolic: Int, context: Context): Boolean {
        var isValid = false
         Timber.i("BloodPressure---> $systolic , $diastolic")
         when {

             systolic in 30..300 && diastolic in 10..150 && (systolic >= diastolic) -> {
                 isValid = true
             }

             systolic in 30..300 && diastolic in 10..150 && (systolic < diastolic) -> {
                 Utilities.toastMessageLong(context, "Systolic BP should not less than diastolic BP")
                 isValid = false
             }

             (systolic < 30) || (systolic > 300)  -> {
                 Utilities.toastMessageLong(context, "Please Enter valid Systolic value between 30-300")
                 isValid = false
             }

             (diastolic < 10) || (diastolic > 150)  -> {
                 Utilities.toastMessageLong(context, "Please Enter valid Diastolic value between 10-150")
                 isValid = false
             }

         }
        return isValid
    }

    fun validateMultiSelectionOptions( flowLayout: FlowLayout): Boolean {
        var isValid = false
        for (i in 0 until flowLayout.childCount) {
            val chk = flowLayout.getChildAt(i) as CheckBox
            if (chk.isChecked) {
                isValid = true
                break
            }
        }
        return isValid
    }

}