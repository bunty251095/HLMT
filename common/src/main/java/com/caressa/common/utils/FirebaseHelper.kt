package com.caressa.common.utils

import android.net.Uri
import android.os.Bundle
import com.caressa.common.constants.NavigationConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
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

    fun generateContentLink(): Uri {
        val domain = "https://hlpace.page.link"

//        val uid = 123456
//        val invitationLink = "https://hlpace.com/?invitedby=$uid"
//
//        var mInvitationUrl: Uri? = Uri.parse(domain)
//        Firebase.dynamicLinks.shortLinkAsync {
//            link = Uri.parse(invitationLink)
//            domainUriPrefix = "https://example.page.link"
//            androidParameters(NavigationConstants.APPID) {
//                minimumVersion = 1
//            }
//            iosParameters("com.vivant.hlmt.app.uat") {
//            }
//        }.addOnSuccessListener { shortDynamicLink ->
//            mInvitationUrl = shortDynamicLink.shortLink
//            // ...
//        }

        val baseUrl = Uri.parse("https://hlpace.appinvite.page.link")
        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setIosParameters(DynamicLink.IosParameters.Builder("com.vivant.hlmt.app.uat").build())
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder(NavigationConstants.APPID).build())
            .buildDynamicLink()

        return link.uri
    }
}