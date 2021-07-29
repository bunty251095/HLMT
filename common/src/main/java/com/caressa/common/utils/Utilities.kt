package com.caressa.common.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.ConnectivityManager
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.caressa.common.BuildConfig
import com.caressa.common.R
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utilities {

    private val gson: Gson = GsonBuilder().create()
    private val prettyGson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val appColorHelper = AppColorHelper.instance!!

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun getHashKey(context: Context): String {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), Base64.DEFAULT))
                Log.e("FACEBOOK KEYHASH", hashKey)
                return hashKey
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            Log.e("FACEBOOK KEYHASH", "UNABLE TO GENERATE")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FACEBOOK KEYHASH", "UNABLE TO GENERATE")
        }
        return ""
    }

    fun isNullOrEmpty(data: String?): Boolean {
        var result = false
        try {
            result = data == null || data == "" || data.equals(
                "null",
                ignoreCase = true
            ) || data == "." || data.trim { it <= ' ' }.length == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun isNullOrEmptyOrZero(data: String?): Boolean {
        var result = false
        try {
            result = data == null || data == "" || data.equals(
                "null",
                ignoreCase = true
            ) || data == "." || data == "0" || data == "0.0"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun hideKeyboard(view: View, context: Context) {
        try {
            val view = view.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val view = activity.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard(view: View, context: Context) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(
            view, InputMethodManager.SHOW_IMPLICIT
            /* WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE*/
        )
    }

    fun toastMessageLong(context: Context?, message: String) {
        try {
            if (context != null && !isNullOrEmpty(message)) {
                val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 100)
                val view = toast.view
                view?.background?.colorFilter =
                    PorterDuffColorFilter(appColorHelper.secondaryColor(), PorterDuff.Mode.SRC_IN)
                val text = view?.findViewById<TextView>(android.R.id.message)
                text?.setTextColor(ContextCompat.getColor(context, R.color.white))
                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun toastMessageShort(context: Context?, message: String) {
        try {
            if (context != null && !isNullOrEmpty(message)) {
                val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0,  100)
                val view = toast.view
                view!!.background.colorFilter =
                    PorterDuffColorFilter(appColorHelper.secondaryColor(), PorterDuff.Mode.SRC_IN)
                val text = view.findViewById<TextView>(android.R.id.message)
                text.setTextColor(ContextCompat.getColor(context, R.color.white))
                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun FindTypeOfDocument(fileName: String): String {
        val extension = RealPathUtil.getFileExt(fileName)
        println("Extension : $extension")
        var documentType = "Unknown"
        // before uploading verifing it's extension
        if (extension.equals("JPEG", ignoreCase = true) ||
            extension.equals("jpg", ignoreCase = true) ||
            extension.equals("PNG", ignoreCase = true) ||
            extension.equals("png", ignoreCase = true)
        ) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true) || extension.equals(
                "pdf",
                ignoreCase = true
            )
        ) {
            documentType = "PDF"
        } else if (extension.equals("DOC", ignoreCase = true) ||
            extension.equals("doc", ignoreCase = true) ||
            extension.equals("docx", ignoreCase = true) || extension.equals(
                "DOCX",
                ignoreCase = true
            )
        ) {
            documentType = "DOC"
        }
        return documentType
    }

    /**
     * detaches Spinner Window
     *
     * @param spinner
     */
    fun detachSpinnerWindow(spinner: Spinner) {
        try {
            val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRelationImgIdWithGender(relationshipCode: String, gender: String): Int {
        var relationImgTobeAdded: Int = R.drawable.icon_husband

        if (relationshipCode == "SELF" && gender == "1") {
            relationImgTobeAdded = R.drawable.icon_husband
        } else if (relationshipCode == "SELF" && gender == "2") {
            relationImgTobeAdded = R.drawable.icon_wife
        } else if (relationshipCode == "FAT") {
            relationImgTobeAdded = R.drawable.icon_father
        } else if (relationshipCode == "MOT") {
            relationImgTobeAdded = R.drawable.icon_mother
        } else if (relationshipCode == "SON") {
            relationImgTobeAdded = R.drawable.icon_son
        } else if (relationshipCode == "DAU") {
            relationImgTobeAdded = R.drawable.icon_daughter
        } else if (relationshipCode == "GRF") {
            relationImgTobeAdded = R.drawable.icon_gf
        } else if (relationshipCode == "GRM") {
            relationImgTobeAdded = R.drawable.icon_gm
        } else if (relationshipCode == "HUS") {
            relationImgTobeAdded = R.drawable.icon_husband
        } else if (relationshipCode == "WIF") {
            relationImgTobeAdded = R.drawable.icon_wife
        } else if (relationshipCode == "BRO") {
            relationImgTobeAdded = R.drawable.icon_brother
        } else if (relationshipCode == "SIS") {
            relationImgTobeAdded = R.drawable.icon_sister
        }
        return relationImgTobeAdded
    }

    fun isEmailValid(email: String): Boolean {
        //return email.contains("@")
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isMobileValid(mobileNumber: String): Boolean {
        return mobileNumber.length == 10
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val Regex = "[^\\d]"
        val PhoneDigits = phoneNumber.replace(Regex.toRegex(), "")
        val isValid = PhoneDigits.length > 8 && !phoneNumber.startsWith("0")
        Timber.d("isValidPhoneNumber--->$isValid")
        return isValid
    }

    fun isPhoneValid(phoneNumber: String): Boolean {
        val pattern =
            Pattern.compile("\\ic_pdf{10}|(?:\\ic_pdf{3}-){2}\\ic_pdf{4}|\\(\\ic_pdf{3}\\)\\ic_pdf{3}-?\\ic_pdf{4}")
        return phoneNumber.matches(pattern.toRegex())
    }

    fun roundOffPrecision(value: Double, precision: Int): Double {
        val scale = Math.pow(10.0, precision.toDouble()).toInt()
        return Math.round(value * scale).toDouble() / scale
    }

/*    fun roundOffPrecision(value: Double, precision: Int): Double {
        val scale = 10.0.pow(precision.toDouble()).toInt()
        return (value * scale) / scale
        //return (value * scale).roundToInt().toDouble() / scale
    }*/

    fun round(d: Double, decimalPlace: Int): Float {
        try {
            var bd = BigDecimal(java.lang.Double.toString(d))
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return d.toFloat()
    }

    /**
     * convert FeetInch To Kg
     *
     * @param strFeetValue
     * @param strInchValue
     * @return
     */
    fun convertFeetInchToCm(strFeetValue: String, strInchValue: String): String {
        var strConvertedValue = ""
        if (!isNullOrEmpty(strFeetValue) && !isNullOrEmpty(strInchValue)) {
            val cm = java.lang.Double.parseDouble(strFeetValue) * 30.48 +
                    java.lang.Double.parseDouble(strInchValue) * 2.54
            strConvertedValue = cm.toString() + ""
        }
        return strConvertedValue
    }

    fun getAppVersion(context: Context): Int {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }

    fun getVersionName(context: Context): String {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not get package name: $e")
        }
    }

    fun exportdatabase(context: Context) {
        if (BuildConfig.DEBUG) {
            RealPathUtil.exportDatabase(Constants.DATABASE_NAME, context)
        }
    }

    fun deleteFileFromLocalSystem(Path: String): Boolean {
        val file = File(Path)
        return file.delete()
    }

    fun getVitalParameterData(parameter: String, context: Context): VitalParameter {
        val vitalParameter = VitalParameter()

        when (parameter) {

            context.resources.getString(R.string.ft) -> {
                vitalParameter.unit = context.resources.getString(R.string.ft)
                vitalParameter.minRange = 4
                vitalParameter.maxRange = 7
            }

            context.resources.getString(R.string.cm) -> {
                vitalParameter.unit = context.resources.getString(R.string.cm)
                vitalParameter.minRange = 120
                vitalParameter.maxRange = 240
            }

            context.resources.getString(R.string.lbs) -> {
                vitalParameter.unit = context.resources.getString(R.string.lbs)
                vitalParameter.minRange = 64
                vitalParameter.maxRange = 550
            }

            context.resources.getString(R.string.kg) -> {
                vitalParameter.unit = context.resources.getString(R.string.kg)
                vitalParameter.minRange = 30
                vitalParameter.maxRange = 250
            }

        }
        return vitalParameter
    }

    fun getHraObservationColorFromScore(score: Int): Int {
        var wellnessScore = score
        if (wellnessScore <= 0) {
            wellnessScore = 0
        }
        var color: Int = appColorHelper.textColor
        when {
            wellnessScore in 0..15 -> {
                color = R.color.high_risk
            }

            wellnessScore in 16..45 -> {
                color = R.color.moderate_risk
            }

            wellnessScore in 46..85 -> {
                color = R.color.healthy_risk
            }

            wellnessScore > 85 -> {
                color = R.color.optimum_risk
            }
        }
        return color
    }

    fun clearStepsData(context: Context) {
        val intent = Intent()
        intent.action = Constants.CLEAR_FITNESS_DATA
        intent.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_BROADCAST_RECEIVER)
        //intent.putExtra(GlobalConstants.EVENT, event)
        context.sendBroadcast(intent)
    }

    fun printData(tag: String, list: Any, toPretty: Boolean = true) {
        var jsonArrayString = ""
        jsonArrayString = if (toPretty) {
            prettyGson.toJson(list)
        } else {
            gson.toJson(list)
        }
        Timber.i("$tag---> $jsonArrayString")
    }

}