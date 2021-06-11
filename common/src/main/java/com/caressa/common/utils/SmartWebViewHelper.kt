package com.caressa.common.utils

object SmartWebViewHelper {

    //Permission variables
    var ASWP_JSCRIPT = true     //enable JavaScript for webview
    var ASWP_FUPLOAD = true     //upload file from webview
    var ASWP_CAMUPLOAD = true     //enable upload from camera for photos
    var ASWP_ONLYCAM = false    //incase you want only camera files to upload
    var ASWP_MULFILE = false    //upload multiple files in webview
    var ASWP_LOCATION = false     //track GPS locations
    var ASWP_RATINGS =
        true     //show ratings dialog; auto configured, edit method get_rating() for customizations
    var ASWP_PBAR = false    //show progress bar in app
    var ASWP_ZOOM = false    //zoom control for webpages view
    var ASWP_SFORM = false    //save form cache and auto-fill information
    var ASWP_OFFLINE = false    //whether the loading webpages are offline or online
    var ASWP_EXTURL = true     //open external url with default browser instead of app webview

    //Security variables
    var ASWP_CERT_VERIFICATION = true    //verify whether HTTPS port needs certificate verification

    //Configuration variables
    var ASWV_URL = "" //complete URL of your website or webpage
    var ASWV_F_TYPE =
        "*/*"  //to upload any file type using "*/*"; check file type references for more
}