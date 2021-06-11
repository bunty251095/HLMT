package com.caressa.security.model

import android.content.Context
import com.caressa.security.R

class BMIParametersDetails( ) {

    fun getBMIParameterData(parameter : String,context: Context) : BMIParameter {
        val bmiParameter = BMIParameter()
        if ( parameter == context.resources.getString(R.string.ft) ) {
            bmiParameter.unit = context.resources.getString(R.string.ft)
            bmiParameter.minRange = 4
            bmiParameter.maxRange = 8
        } else if ( parameter == context.resources.getString(R.string.cm) ) {
            bmiParameter.unit = context.resources.getString(R.string.cm)
            bmiParameter.minRange = 120
            bmiParameter.maxRange = 240
        } else if ( parameter == context.resources.getString(R.string.lbs) ) {
            bmiParameter.unit = context.resources.getString(R.string.lbs)
            bmiParameter.minRange = 64
            bmiParameter.maxRange = 550
        } else if ( parameter == context.resources.getString(R.string.kg) ) {
            bmiParameter.unit = context.resources.getString(R.string.kg)
            bmiParameter.minRange = 30
            bmiParameter.maxRange = 250
        }
        return bmiParameter
    }
}