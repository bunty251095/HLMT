package com.caressa.common.utils

import android.content.res.Configuration
import android.os.Bundle
import com.caressa.common.constants.FirebaseConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseHelper {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    init {
        firebaseAnalytics = Firebase.analytics
    }
    public fun getInstance(){

    }

    fun logCustomFirebaseEvent(eventName:String, addPrefix:Boolean = true){
//        if(addPrefix) {
            firebaseAnalytics.logEvent(FirebaseConstants.PARTNER_IDENTIFIER + eventName, Bundle())
//        }else{
//            firebaseAnalytics.logEvent(eventName, Bundle())
//        }
    }

    fun logScreenEvent(eventName: String, addPrefix: Boolean= true){
        logCustomFirebaseEvent(FirebaseConstants.PARTNER_IDENTIFIER+eventName, addPrefix)
    }
}