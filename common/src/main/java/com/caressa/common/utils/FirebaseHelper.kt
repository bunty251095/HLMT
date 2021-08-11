package com.caressa.common.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = Firebase.analytics
    }

    public fun getInstance() {

    }

    fun logCustomFirebaseEvent(eventName: String) {
//        if(addPrefix) {
        firebaseAnalytics.logEvent(eventName, Bundle())
//        }else{
//            firebaseAnalytics.logEvent(eventName, Bundle())
//        }
    }

    fun logScreenEvent(eventName: String) {
        logCustomFirebaseEvent(eventName)
    }
}