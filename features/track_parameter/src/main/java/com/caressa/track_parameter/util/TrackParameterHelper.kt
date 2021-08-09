package com.caressa.track_parameter.util

import android.content.Context
import com.caressa.common.utils.Utilities
import com.caressa.track_parameter.R

object TrackParameterHelper {

    fun getProfileImageByProfileCode(code: String): Int {
        var image: Int = R.drawable.img_profile_bmi
        if (code.equals("BMI", ignoreCase = true)) {
            image = R.drawable.img_profile_bmi
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            image = R.drawable.img_profile_bp
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            image = R.drawable.img_profile_blood_sugar
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            image = R.drawable.img_profile_hemogram
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            image = R.drawable.img_profile_kidney
        } else if (code.equals("LIPID", ignoreCase = true)) {
            image = R.drawable.img_profile_lipid
        } else if (code.equals("LIVER", ignoreCase = true)) {
            image = R.drawable.img_profile_liver
        } else if (code.equals("THYROID", ignoreCase = true)) {
            image = R.drawable.img_profile_thyroid
        } else if (code.equals("URINE", ignoreCase = true)) {
            image = R.drawable.img_profile_urine
        } else if (code.equals("WHR", ignoreCase = true)) {
            image = R.drawable.img_profile_whr
        }
        return image
    }

    fun getProfileNameByProfileCode(code: String): Int {
        var profileName: Int = R.string.BMI
        if (code.equals("BMI", ignoreCase = true)) {
            profileName = R.string.BMI
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            profileName = R.string.BP
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            profileName = R.string.BLOOD_SUGAR
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            profileName = R.string.HEMOGRAM
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            profileName = R.string.KIDNEY
        } else if (code.equals("LIPID", ignoreCase = true)) {
            profileName = R.string.LIPID
        } else if (code.equals("LIVER", ignoreCase = true)) {
            profileName = R.string.LIVER
        } else if (code.equals("THYROID", ignoreCase = true)) {
            profileName = R.string.THYROID
        } else if (code.equals("URINE", ignoreCase = true)) {
            profileName = R.string.URINE
        } else if (code.equals("WHR", ignoreCase = true)) {
            profileName = R.string.WHR
        }
        return profileName
    }

    fun getObservationColor(observation: String?, profileCode: String): Int {
        var color: Int = R.color.vivant_charcoal_grey_55
        if (!Utilities.isNullOrEmpty(observation)) {
            color =
                when (observation!!.toUpperCase()) {
                    "VERY HIGH", "HIGH","HIGH NORMAL", "DIABETIC", "MILDLY HIGH", "VERY LOW", "LOW", "POOR", "MILDLY LOW" -> R.color.vivant_watermelon
                    "BORDERLINE HIGH", "EARLY DIABETIC", "MODERATE", "BETTER", "NEAR OPTIMAL", "MODERATE LOW", "MILD LOW", "MILD HIGH", "MODERATE HIGH" -> R.color.vivant_orange_yellow
                    "DESIRABLE", "BEST", "NORMAL", "OPTIMAL", "GOOD" -> R.color.vivant_nasty_green
                    else -> R.color.vivant_charcoal_grey_55
                }
            if (profileCode.equals("BMI", ignoreCase = true)) {
                /* if (observation.equalsIgnoreCase("Underweight") || observation.equalsIgnoreCase("Overweight")) {
                    color = R.color.vivant_orange_yellow;
                } else*/
                color = if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
            if (profileCode.equals("BLOODPRESSURE", ignoreCase = true)) {
                color = if (observation.equals("Low", ignoreCase = true)) {
                    R.color.vivant_orange_yellow
                } else if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
        }
        return color
    }

    fun getPulseObservation(pulseStr: String,context: Context): String {
        var observation = ""
        if (!pulseStr.isNullOrEmpty()) {
            val pulse = pulseStr.toDouble().toInt()
            if (pulse in 1..59) {
                observation = context.resources.getString(R.string.LOW)
            } else if (pulse in 60..100) {
                observation = context.resources.getString(R.string.NORMAL)
            } else if (pulse in 101..999) {
                observation = context.resources.getString(R.string.HIGH)
            }
        }
        return observation
    }

    fun getBPObservation(systolic: Int, diastolic: Int,context: Context): String {
        var strObservation = ""
        if (systolic < 90 || diastolic < 60) {
            strObservation = context.resources.getString(R.string.LOW)
        }
        if (systolic in 90..120 && diastolic in 60..80) {
            strObservation = context.resources.getString(R.string.NORMAL)
        }
        if (systolic in 121..139 || diastolic in 81..89) {
            strObservation =  context.resources.getString(R.string.HIGH_NORMAL)
        }
        if (systolic >= 140 || diastolic >= 90) {
            strObservation =  context.resources.getString(R.string.ABNORMAL)
        }
        return strObservation
    }

    /*fun getBPObservation(systolicParam: Int, diastolicParam: Int): String {
        var strObservation = ""
        var S = 0
        var D = 0
        var BP = 0
        S = if (systolicParam <= 90) {
            1
        } else if (systolicParam <= 120 && systolicParam > 90) {
            2
        } else if (systolicParam <= 129 && systolicParam > 120) {
            3
        } else if (systolicParam <= 139 && systolicParam > 129) {
            4
        } else if (systolicParam <= 180 && systolicParam > 139) {
            5
        } else {
            6
        }
        D = if (diastolicParam <= 60) {
            1
        } else if (diastolicParam <= 80 && diastolicParam > 60) {
            2
        } else if (diastolicParam <= 90 && diastolicParam > 80) {
            3
        } else if (diastolicParam <= 100 && diastolicParam > 90) {
            4
        } else if (diastolicParam <= 110 && diastolicParam > 100) {
            5
        } else {
            6
        }
        BP = S
        if (D > S) {
            BP = D
        }
        if (BP == 1) {
            strObservation = "Low"
        } else if (BP == 2) {
            strObservation = "Normal"
        } else if (BP == 3) {
            strObservation = "PreHypertension"
        } else if (BP == 4) {
            strObservation = "Hypertension- Stage 1"
        } else if (BP == 5) {
            strObservation = "Hypertension- Stage 2"
        } else if (BP == 6) {
            strObservation = "Hypertension- Critical"
        }
        return strObservation
    }*/

    fun convertFeetInchToCm(strFeetValue: String, strInchValue: String?): String {
        var strConvertedValue = ""
        if (strInchValue != null && strInchValue != null) {
            if (!strInchValue.isEmpty() && !strInchValue.isEmpty()) {
                val cm = strFeetValue.toDouble() * 30.48 + strInchValue.toDouble() * 2.54
                strConvertedValue = cm.toInt().toString() + ""
            }
        }
        return strConvertedValue
    }

    fun getBMIObservation(parameterVal: String,context: Context): String {
        var observation = ""
        try {
            if (!parameterVal.isEmpty()){
                var bmi:Double = parameterVal.toDouble()

                if (bmi<=18.49){
                    observation = context.resources.getString(R.string.UNDERWEIGHT)
                }else if (bmi>18.49 && bmi<=22.99){
                    observation = context.resources.getString(R.string.NORMAL)
                }else if (bmi>22.99 && bmi<=24.99){
                    observation = context.resources.getString(R.string.OVERWEIGHT)
                }else if (bmi>24.99 && bmi<=29.99){
                    observation = context.resources.getString(R.string.PRE_OBESE)
                }else if (bmi>29.99 && bmi<=34.99){
                    observation = context.resources.getString(R.string.OBESE_LEVEL_1)
                }else if (bmi>34.99 && bmi<=39.99){
                    observation = context.resources.getString(R.string.OBESE_LEVEL_2)
                }else {
                    observation = context.resources.getString(R.string.OBESE_LEVEL_3)
                }
            }
        }catch (e:Exception){e.printStackTrace()}
        return observation
    }

    fun getWHRObservation(parameterVal: String,gender: Int,context: Context): String {
        var observation = ""
        try {
            if (parameterVal.isNotEmpty()){
                var whr:Double = parameterVal.toDouble()
                if (gender ==1){
                    if (whr<=0.95){
                        observation = context.resources.getString(R.string.NORMAL)
                    }else if (whr>0.95 && whr<=1){
                        observation = context.resources.getString(R.string.MODERATE)
                    }else if (whr>1){
                        observation = context.resources.getString(R.string.HIGH)
                    }
                }else{
                    if (whr<=0.80){
                        observation = context.resources.getString(R.string.NORMAL)
                    }else if (whr>0.80 && whr<=0.85){
                        observation = context.resources.getString(R.string.MODERATE)
                    }else if (whr>0.85){
                        observation = context.resources.getString(R.string.HIGH)
                    }
                }
            }
        }catch (e:Exception){e.printStackTrace()}
        return observation
    }

    fun isNullOrEmptyOrZero(maxPermissibleValue: String?): Boolean {
        var result = false
        try {
            result = if (maxPermissibleValue == null
                || maxPermissibleValue == ""
                || maxPermissibleValue.equals("null", ignoreCase = true)
                || maxPermissibleValue.equals("NULL", ignoreCase = true)
                || maxPermissibleValue == "."
                || maxPermissibleValue == "0"
                || maxPermissibleValue == "0.0"
                || maxPermissibleValue == "0.00") {
                true
            } else {
                false
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }
    /*fun getProfilePosition(selectedProfilesList: ArrayList<ArrayMap<String?, String?>>): Int {
    var position = 0
    if (CacheFactory.has(GlobalConstants.PROFILE_CODE)) {
        for (i in selectedProfilesList.indices) {
            if (selectedProfilesList[i][GlobalConstants.PROFILE_CODE].equals(
                    CacheFactory.get(
                        GlobalConstants.PROFILE_CODE
                    ), ignoreCase = true
                )
            ) {
                position = i
                //                    CacheFactory.remove(GlobalConstants.PROFILE_CODE);
                break
            }
        }
    }
    return position
}*/

}