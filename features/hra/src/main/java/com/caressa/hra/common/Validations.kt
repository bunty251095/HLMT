package com.caressa.hra.common

import android.content.Context
import android.widget.CheckBox
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Utilities
import com.caressa.common.view.FlowLayout
import com.caressa.hra.R
import timber.log.Timber
import java.util.*

object Validations {

     fun validationBMI(height: Double, weight: Double, context: Context ): Boolean {
         val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
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
                 Utilities.toastMessageLong(context, localResource.getString(R.string.PLEASE_SELECT_VALID_HEIGHT_BETWEEN) +" " + 120 + "-" + 240)
                 isValid = false
             }

             (weight < 30 || weight > 150) -> {
                 Utilities.toastMessageLong(context, localResource.getString(R.string.PLEASE_SELECT_VALID_WEIGHT_BETWEEN) + " " + 30 + "-" + 150)
                 isValid = false
             }

         }
        return isValid
    }

     fun validateBP(systolic: Int, diastolic: Int, context: Context): Boolean {
         val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
        var isValid = false
         Timber.i("BloodPressure---> $systolic , $diastolic")
         when {

             systolic in 30..300 && diastolic in 10..150 && (systolic >= diastolic) -> {
                 isValid = true
             }

             systolic in 30..300 && diastolic in 10..150 && (systolic < diastolic) -> {
                 Utilities.toastMessageLong(context,localResource.getString(R.string.SYSTOLIC_BP_SHOULD_NOT_LESS_THAN_DIASTOLIC_BP))
                 isValid = false
             }

             (systolic < 30) || (systolic > 300)  -> {
                 Utilities.toastMessageLong(context,localResource.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE_BETWEEN_30_TO_300))
                 isValid = false
             }

             (diastolic < 10) || (diastolic > 150)  -> {
                 Utilities.toastMessageLong(context,localResource.getString(R.string.PLEASE_INSERT_DIASTOLIC_BP_VALUE_BETWEEN_10_TO_150))
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