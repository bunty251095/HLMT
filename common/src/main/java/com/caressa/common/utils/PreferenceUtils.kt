package com.caressa.common.utils

import android.content.SharedPreferences

class PreferenceUtils( private val sharedPreference: SharedPreferences ) {

    //private val tag = "FileUtils : "

    fun storePreference(key:String,value: String ) {
        sharedPreference.edit().putString(key,value).apply()
    }

    fun getPreference(key:String): String {
        return sharedPreference.getString(key, "")!!
    }

}