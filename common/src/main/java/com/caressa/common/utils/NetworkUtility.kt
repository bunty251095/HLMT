package com.caressa.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

object NetworkUtility {
    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0
    fun getConnectivityStatus(context: Context?): Int {
        try {
            if (context == null) {
                throw Exception("Network utility - context is null")
            }
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (null != activeNetwork) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
            }
        } catch (ex: Exception) {
            Log.e("Network Utility", ex.message + ex.stackTrace)
        }
        return TYPE_NOT_CONNECTED
    }

    fun isOnline(context: Context?): Boolean {
        val status = getConnectivityStatus(context)
        return if (status == TYPE_NOT_CONNECTED) {
            false
        } else {
            true
        }
    }

    fun getConnectivityStatusString(context: Context?): String? {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled"
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled"
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet"
        }
        return status
    }
}