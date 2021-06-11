package com.caressa.track_parameter.util

import android.util.ArrayMap
import com.caressa.common.utils.Utilities
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * This class is used for calculate trackparameters and also get observation
 */
object CalculateParametersAndObservations {

     object GlobalConstants {

        val BMI_OBSERVATION_COLOR = "bmi_color"
        val BMI_OBSERVATION_DESCRIPTION = "bmi_description"
    }
    /**
     * gets BMI
     *
     * @param heightParam
     * @param weightParam
     * @return
     */
    fun getBMIFromMetric(heightParam: Double, weightParam: Double): Double {
        var height = 0.0
        var weight = 0.0
        var bmi = 0.0
        try {
            height = heightParam
            weight = weightParam

            /* if (Helper.isNullOrEmpty(String.valueOf(height)) || height <= 0) {
                return 0;
            } else if (Helper.isNullOrEmpty(String.valueOf(weight)) || weight <= 0) {
                return 0;
            } else {
                try {
                    //bmi = Math.round(Float.parseFloat(String.valueOf(weight)) / Math.pow(Float.parseFloat(String.valueOf(height)) / 100, 2));

                    bmi = Float.parseFloat(String.valueOf(weight)) / Math.pow(Float.parseFloat(String.valueOf(height)) / 100, 2);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }*/


            if (Utilities.isNullOrEmpty(height.toString())) {
                return 0.0
            } else if (Utilities.isNullOrEmpty(weight.toString())) {
                return 0.0
            } else {
                try {
                    val weightValue = java.lang.Float.parseFloat(weight.toString())
                    val heightValue = java.lang.Float.parseFloat(height.toString())
                    /* if ((!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 66 && weightValue <= 551)
                            || (!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_METRIC)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 30 && weightValue <= 250)) {*/
                    if (weightValue >= 30 && weightValue <= 151 && heightValue >= 121 && heightValue <= 214) {
                        bmi = weightValue / Math.pow((heightValue / 100).toDouble(), 2.0)
                        //bmiAsString = new DecimalFormat("##.##").format(bmi).toString();
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } finally {
            // height = null;
            // weight = null;
        }
        return bmi
    }

    fun getBMIFromImperial(heightFeet: Double, heightInch: Double, weightParam: Double): Double {
        var height = 0.0
        var weight = 0.0
        var bmi = 0.0
        try {
            height = java.lang.Double.parseDouble(
                Utilities.convertFeetInchToCm(
                    heightFeet.toString(),
                    heightInch.toString()
                )
            )
            weight = weightParam

            bmi = getBMIFromMetric(height, weightParam)
        } finally {
            // height = null;
            // weight = null;
        }
        return bmi
    }

    /**
     * gets WHR
     *
     * @param waistParam
     * @param hipParam
     * @return
     */
    fun getWHR(waistParam: Double, hipParam: Double): String {
        var waist: String? = ""
        var hip: String? = ""
        var whr = 0f
        var whrAsString = ""

        try {
            waist = waistParam.toString() + ""
            hip = hipParam.toString() + ""

            if (Utilities.isNullOrEmpty(waist) || waistParam <= 0) {
                return ""
            } else if (Utilities.isNullOrEmpty(hip) || hipParam <= 0) {
                return ""
            } else {
                try {
                    whr = java.lang.Float.parseFloat(waist) / java.lang.Float.parseFloat(hip)
                    val nm = NumberFormat.getNumberInstance()
                    //whrAsString=String.valueOf(whr);
                    //whrAsString=(nm.format(whr));
                    whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } finally {
            waist = null
            hip = null
        }
        return whrAsString
    }

    /**
     * get BP Observation
     *
     * @param systolicParam
     * @param diastolicParam
     * @return
     */
    fun getBPObservation(systolicParam: Int, diastolicParam: Int): String {
        var strObservation = ""
        var S = 0
        var D = 0
        var BP = 0
        if (systolicParam < 90) {
            S = 1
        } else if (systolicParam < 120 && systolicParam >= 90) {
            S = 2
        } else if (systolicParam < 140 && systolicParam >= 120) {
            S = 3
        } else if (systolicParam < 160 && systolicParam >= 140) {
            S = 4
        } else if (systolicParam < 180 && systolicParam >= 160) {
            S = 5
        } else {
            S = 6
        }

        if (diastolicParam < 60) {
            D = 1
        } else if (diastolicParam < 80 && diastolicParam >= 60) {
            D = 2
        } else if (diastolicParam < 90 && diastolicParam >= 80) {
            D = 3
        } else if (diastolicParam < 100 && diastolicParam >= 90) {
            D = 4
        } else if (diastolicParam < 110 && diastolicParam >= 100) {
            D = 5
        } else {
            D = 6
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
    }

    fun getBloodSugarObservation(strLabValue: String): String {
        var strObservations = ""
        val lab = roundOff(java.lang.Double.parseDouble(strLabValue), 1)
        if (lab >= 61 && lab <= 79.99) {
            strObservations = "Low"
        } else if (lab >= 80 && lab <= 140) {
            strObservations = "Normal"
        } else if (lab >= 140 && lab <= 199.99) {
            strObservations = "Early Diabetic"
        } else if (lab >= 200 && lab <= 999.99) {
            strObservations = "Diabetic"
        }
        return strObservations
    }

    fun getCholesterolObservation(strLabValue: String): String {
        var strObservations = ""
        val lab = roundOff(java.lang.Double.parseDouble(strLabValue), 1)
        if (lab >= 50 && lab <= 199.99) {
            strObservations = "Desirable"
        } else if (lab >= 200.0 && lab <= 239.99) {
            strObservations = "Mildly High"
        } else if (lab >= 240.0 && lab <= 400) {
            strObservations = "High"
        }
        return strObservations
    }

    /**
     * get BMI Observation
     *
     * @param bmiParam
     * @return
     */
    fun getBMIObservation(bmiParam: Double): ArrayMap<String, String> {
        val bmiObservationMap = ArrayMap<String, String>()
        if (bmiParam <= 18.49) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#4FC1C1"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Underweight"
            return bmiObservationMap
        } else if (bmiParam >= 18.5 && bmiParam <= 22.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#B4D852"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Normal"
            return bmiObservationMap
        } else if (bmiParam >= 23 && bmiParam <= 24.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FFD401"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Overweight"
            return bmiObservationMap
        } else if (bmiParam >= 25 && bmiParam <= 29.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Pre-Obese"
            return bmiObservationMap
        } else if (bmiParam >= 30 && bmiParam <= 34.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 1"
            return bmiObservationMap
        } else if (bmiParam >= 35 && bmiParam <= 39.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 2"
            return bmiObservationMap
        } else {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 3"
            return bmiObservationMap
        }
    }

    fun getBMIObservation(strBMI: String): String {
        val bmi = roundOff(java.lang.Double.parseDouble(strBMI), 1)
        var strObservations = ""
        if (bmi <= 18.49) {
            strObservations = "Underweight"
        } else if (bmi >= 18.5 && bmi <= 22.99) {
            strObservations = "Normal"
        } else if (bmi >= 23.0 && bmi <= 24.99) {
            strObservations = "Overweight"
        } else if (bmi >= 25.0 && bmi <= 29.99) {
            strObservations = "Pre-Obese"
        } else if (bmi >= 30.0 && bmi <= 34.99) {
            strObservations = "Obese Level 1"
        } else if (bmi >= 35.0 && bmi <= 39.99) {
            strObservations = "Obese Level 2"
        } else {
            strObservations = "Obese Level 3"
        }
        return strObservations
    }

    private fun roundOff(value: Double, places: Int): Double {
        var value = value
        require(places >= 0)

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }
}
