package com.caressa.hra.common

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.caressa.common.utils.Utilities
import com.caressa.hra.R

object Observations {

     fun setBMIResult(strBMI: String,result_bmi: AppCompatTextView,context: Context) {
         try {
             if ( !Utilities.isNullOrEmptyOrZero(strBMI) ) {
                 val bmi = strBMI.toDouble()
                 val strObservations: String
                 val color: Int
                 when {
                     bmi <= 18.49 -> {
                         strObservations = context.resources.getString(R.string.UNDER_WEIGHT)
                         color = R.color.vivant_watermelon
                     }
                     bmi in 18.5..22.99 -> {
                         strObservations = context.resources.getString(R.string.NORMAL)
                         color = R.color.vivant_green_blue_two
                     }
                     bmi in 23.0..24.99 -> {
                         strObservations = context.resources.getString(R.string.OVER_WEIGHT)
                         color = R.color.vivant_watermelon
                     }
                     bmi in 25.0..29.99 -> {
                         strObservations = context.resources.getString(R.string.PRE_OBESE)
                         color = R.color.vivant_watermelon
                     }
                     bmi in 30.0..34.99 -> {
                         strObservations = context.resources.getString(R.string.OBESS_LEVEL_1)
                         color = R.color.vivant_watermelon
                     }
                     bmi in 35.0..39.99 -> {
                         strObservations = context.resources.getString(R.string.OBESS_LEVEL_2)
                         color = R.color.vivant_watermelon
                     }
                     else -> {
                         strObservations = context.resources.getString(R.string.OBESS_LEVEL_3)
                         color = R.color.vivant_watermelon
                     }
                 }

                 //val strResult = context.resources.getString(R.string.YOUR_BMI_IS)+" "+"<big><b>" + String.format("%.1f", bmi) + "</b> </big><br>(" + strObservations + ")</br>"
                 val strResult = context.resources.getString(R.string.YOUR_BMI_IS) + " (" + String.format("%.1f", bmi) + ") is " + strObservations + "."

                 result_bmi.text = HtmlCompat.fromHtml(strResult,HtmlCompat.FROM_HTML_MODE_LEGACY)
                 result_bmi.setTextColor( ContextCompat.getColor(context,color) )
                 result_bmi.visibility = View.VISIBLE

             } else {
                 result_bmi.visibility = View.INVISIBLE
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }
    }

    fun setBPResult(systolic: Int?, diastolic: Int?, result_bp: AppCompatTextView, context: Context) {
        try {
            val strObservations = getBPObservation(systolic!!, diastolic!!, context)
            if (!Utilities.isNullOrEmpty(strObservations) && systolic >= 30 && systolic <= 300 && diastolic >= 10 && diastolic <= 150) {
                val strResult = context.resources.getString(R.string.YOUR_BP_IS)+ " <b><big>$systolic/$diastolic</b></big> mm Hg ($strObservations)"
                result_bp.text = HtmlCompat.fromHtml(strResult,HtmlCompat.FROM_HTML_MODE_LEGACY)

                when {
                    strObservations.equals("Normal", ignoreCase = true) -> {
                        result_bp.setTextColor(ContextCompat.getColor(context, R.color.vivant_green_blue_two))
                    }
                    strObservations.equals("PreHypertension", ignoreCase = true) -> {
                        result_bp.setTextColor(ContextCompat.getColor(context, R.color.vivant_orange_yellow))
                    }
                    else -> {
                        result_bp.setTextColor(ContextCompat.getColor(context, R.color.vivant_watermelon))
                    }
                }

            }
            if (systolic < 30 || systolic > 300 || diastolic < 10 || diastolic > 150) {
                result_bp.visibility = View.INVISIBLE
            } else {
                result_bp.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBPObservation(systolicParam: Int, diastolicParam: Int, context: Context): String {
        var strObservation = ""
        var bp: Int
        val s: Int = when {
            systolicParam < 90 -> 1
            systolicParam in 90..120 -> 2
            systolicParam in 121..140 -> 3
            systolicParam in 141..160 -> 4
            systolicParam in 161..180 -> 5
            else -> 6
        }

        val d: Int = when {
            diastolicParam < 60 -> 1
            diastolicParam in 60..80 -> 2
            diastolicParam in 81..90 -> 3
            diastolicParam in 91..100 -> 4
            diastolicParam in 101..110 -> 5
            else -> 6
        }

        bp = s
        if ( d > s ) {
            bp = d
        }

        when (bp) {
            1 -> strObservation = context.resources.getString(R.string.LOW)
            2 -> strObservation = context.resources.getString(R.string.NORMAL)
            3 -> strObservation = context.resources.getString(R.string.PRE_HYPERTENSION)
            4 -> strObservation = context.resources.getString(R.string.HYP_TENSION_STAGE1)
            5 -> strObservation = context.resources.getString(R.string.HYP_TENSION_STAGE2)
            6 -> strObservation = context.resources.getString(R.string.HYP_TENSION_CRITICAL)
        }
        return strObservation
    }

}