package com.caressa.security.model

import android.content.Context
import com.caressa.security.R

class BMIParametersDetails( ) {

    fun getBMIParameterData(parameter : String,context: Context) : BMIParameter {
        val bmiParameter = BMIParameter()
        if ( parameter == context.resources.getString(R.string.FT) ) {
            bmiParameter.unit = context.resources.getString(R.string.FT)
            bmiParameter.minRange = 4
            bmiParameter.maxRange = 8
        } else if ( parameter == context.resources.getString(R.string.CM) ) {
            bmiParameter.unit = context.resources.getString(R.string.CM)
            bmiParameter.minRange = 120
            bmiParameter.maxRange = 240
        } else if ( parameter == context.resources.getString(R.string.LBS) ) {
            bmiParameter.unit = context.resources.getString(R.string.LBS)
            bmiParameter.minRange = 64
            bmiParameter.maxRange = 550
        } else if ( parameter == context.resources.getString(R.string.KG) ) {
            bmiParameter.unit = context.resources.getString(R.string.KG)
            bmiParameter.minRange = 30
            bmiParameter.maxRange = 250
        }
        return bmiParameter
    }
}