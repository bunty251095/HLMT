package com.caressa.common.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.caressa.common.R
import com.caressa.common.base.DialogFullScreenView
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
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
                Timber.e("FACEBOOK KEYHASH--->$hashKey")
                return hashKey
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            Timber.e("FACEBOOK KEYHASH : UNABLE TO GENERATE")
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e("FACEBOOK KEYHASH : UNABLE TO GENERATE")
        }
        return ""
    }

    fun isNullOrEmpty(data: String?): Boolean {
        var result = false
        try {
            result = data == null || data == ""
                    || data.equals("null", ignoreCase = true)
                    || data == "."
                    || data.trim { it <= ' ' }.length == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun isNullOrEmptyOrZero(data: String?): Boolean {
        var result = false
        try {
            result = data == null
                    || data == ""
                    || data.equals("null", ignoreCase = true)
                    || data == "."
                    || data == "0"
                    || data == "0.0"
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    val view = toast.view
                    view?.background?.colorFilter = PorterDuffColorFilter(appColorHelper.secondaryColor(), PorterDuff.Mode.SRC_IN)
                    val text = view?.findViewById<TextView>(android.R.id.message)
                    text?.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
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
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    toast.setGravity(Gravity.BOTTOM, 0, 100)
                    val view = toast.view
                    view!!.background.colorFilter = PorterDuffColorFilter(appColorHelper.secondaryColor(), PorterDuff.Mode.SRC_IN)
                    val text = view.findViewById<TextView>(android.R.id.message)
                    text.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                toast.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun FindTypeOfDocument(fileName: String): String {
        val extension = FileUtils.getFileExt(fileName)
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

    fun getRelationshipByRelationshipCode(relationshipCode: String, context: Context): String {
        var relationship = ""
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        when (relationshipCode) {
            Constants.SELF_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.MYSELF)
            }
            Constants.FATHER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.BROTHER)
            }
            Constants.MOTHER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.MOTHER)
            }
            Constants.SON_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.SON)
            }
            Constants.DAUGHTER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.DAUGHTER)
            }
            Constants.GRANDFATHER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.GRAND_FATHER)
            }
            Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.GRAND_MOTHER)
            }
            Constants.HUSBAND_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.HUSBAND)
            }
            Constants.WIFE_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.WIFE)
            }
            Constants.BROTHER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.BROTHER)
            }
            Constants.SISTER_RELATIONSHIP_CODE -> {
                relationship = localResource.getString(R.string.SISTER)
            }
        }

        return relationship
    }

    fun getRelationImgIdWithGender(relationshipCode: String, gender: String): Int {
        var relationImgTobeAdded: Int = R.drawable.icon_husband

        if (relationshipCode == Constants.SELF_RELATIONSHIP_CODE && gender == "1") {
            relationImgTobeAdded = R.drawable.icon_husband
        } else if (relationshipCode == Constants.SELF_RELATIONSHIP_CODE && gender == "2") {
            relationImgTobeAdded = R.drawable.icon_wife
        } else if (relationshipCode == Constants.FATHER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_father
        } else if (relationshipCode == Constants.MOTHER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_mother
        } else if (relationshipCode == Constants.SON_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_son
        } else if (relationshipCode == Constants.DAUGHTER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_daughter
        } else if (relationshipCode == Constants.GRANDFATHER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_gf
        } else if (relationshipCode == Constants.GRANDMOTHER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_gm
        } else if (relationshipCode == Constants.HUSBAND_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_husband
        } else if (relationshipCode == Constants.WIFE_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_wife
        } else if (relationshipCode == Constants.BROTHER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_brother
        } else if (relationshipCode == Constants.SISTER_RELATIONSHIP_CODE) {
            relationImgTobeAdded = R.drawable.icon_sister
        }
        return relationImgTobeAdded
    }

    fun isEmailValid(email: String): Boolean {
        //return email.contains("@")
        /*val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()*/
        var isValid = false
        if (!email.isNullOrEmpty() && email.contains('@',true) && email.length > 3)
        {
            isValid = true
        }

        return isValid
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

    fun getAppFolderLocation(context: Context) : String {
        var appFolderLocation = Constants.primaryStorage + "/" + context.resources.getString(R.string.app_name)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val lmn = context.externalMediaDirs
                for ( i in lmn ) {
                    if ( i.absolutePath.contains(context.packageName) ) {
                        Timber.e(i.absolutePath)
                        appFolderLocation = i.absolutePath + "/" + context.resources.getString(R.string.app_name)
                        break
                    }
                }
            }
            val dir = File(appFolderLocation)
            if (!dir.exists()) {
                val directoryCreated = dir.mkdirs()
                Timber.e("DirectoryCreated--->$directoryCreated")
            } else {
                Timber.e("DirectoryAlreadyExist")
            }
            Timber.e("DirectoryName--->$dir")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appFolderLocation
    }

    fun getAppName(context: Context): String {
        return context.resources.getString(R.string.app_name)
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

    fun getDocumentTypeFromExt(extension:String) : String {
        var documentType = ""
        if (extension.equals("JPEG", ignoreCase = true) ||
            extension.equals("jpg", ignoreCase = true) ||
            extension.equals("PNG", ignoreCase = true) ||
            extension.equals("png", ignoreCase = true)) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true) || extension.equals("pdf", ignoreCase = true)) {
            documentType = "PDF"
        } else if (extension.equals("txt", ignoreCase = true) ||
            extension.equals("doc", ignoreCase = true) ||
            extension.equals("docx", ignoreCase = true)) {
            documentType = "DOC"
        }
        return documentType
    }

    fun isAcceptableDocumentType(extension:String) : Boolean {
        var isAcceptable = false
        var documentType = ""
        //val extension1 = RealPathUtil.getFileExt(filePath)
        Timber.e("Extension : $extension")

        if (extension.equals("JPEG", ignoreCase = true) ||
            extension.equals("jpg", ignoreCase = true) ||
            extension.equals("PNG", ignoreCase = true) ||
            extension.equals("png", ignoreCase = true)) {
            documentType = "IMAGE"
        } else if (extension.equals("PDF", ignoreCase = true) || extension.equals("pdf", ignoreCase = true)) {
            documentType = "PDF"
        } else if (extension.equals("txt", ignoreCase = true) ||
            extension.equals("doc", ignoreCase = true) ||
            extension.equals("docx", ignoreCase = true)) {
            documentType = "DOC"
        }
        Timber.e("documentType : $documentType")
        if ( !isNullOrEmpty(documentType) ) {
            isAcceptable = true
        }
        return isAcceptable
    }

    fun deleteFileFromLocalSystem(Path: String) {
        val file = File(Path)
        if ( file.exists() ) {
            val isDeleted = file.delete()
            Timber.e("isDeleted--->$isDeleted")
        } else {
            Timber.e("File not exist")
        }
    }

    fun deleteFile(file: File) {
        if ( file.exists() ) {
            val isDeleted = file.delete()
            Timber.e("isDeleted--->$isDeleted")
        } else {
            Timber.e("File not exist")
        }
    }

    fun getVitalParameterData(parameter: String, context: Context): VitalParameter {
        val vitalParameter = VitalParameter()

        when (parameter) {

            context.resources.getString(R.string.FT) -> {
                vitalParameter.unit = context.resources.getString(R.string.FT)
                vitalParameter.minRange = 4
                vitalParameter.maxRange = 7
            }

            context.resources.getString(R.string.CM) -> {
                vitalParameter.unit = context.resources.getString(R.string.CM)
                vitalParameter.minRange = 120
                vitalParameter.maxRange = 240
            }

            context.resources.getString(R.string.LBS) -> {
                vitalParameter.unit = context.resources.getString(R.string.LBS)
                vitalParameter.minRange = 64
                vitalParameter.maxRange = 550
            }

            context.resources.getString(R.string.KG) -> {
                vitalParameter.unit = context.resources.getString(R.string.KG)
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

    fun Activity.getRootView(): View {
        return findViewById<View>(android.R.id.content)
    }

    fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
    }

    fun Activity.isKeyboardOpen(): Boolean {
        val visibleBounds = Rect()
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = getRootView().height - visibleBounds.height()
        val marginOfError = Math.round(this.convertDpToPx(50F))
        return heightDiff > marginOfError
    }

    fun Activity.isKeyboardClosed(): Boolean {
        return !this.isKeyboardOpen()
    }

    fun showFullImageWithBitmap(bitmap: Bitmap, context:Context, isImage:Boolean) {
        try {
            val  dialog = DialogFullScreenView(context,isImage,"",bitmap)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showFullImageWithImgUrl(imgUrl:String,context:Context,isImage:Boolean) {
        try {
            val  dialog = DialogFullScreenView(context,isImage,imgUrl,null)
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLanguageDescription(item: String, languageCode: String): String? {
        var result:String = ""
        if (languageCode.equals("en",true)) {
            when (item.toUpperCase()) {
                "HEIGHT","WEIGHT","SYSTOLIC","DIASTOLIC","HIP","WAIST","PULSE" -> {
                    result = item
                }
            }
        }else{
            when (item.toUpperCase()) {
                "HEIGHT" -> {
                    result = "CHI???U CAO"
                }
                "WEIGHT" -> {
                    result = "C??N N???NG"
                }
                "SYSTOLIC" -> {
                    result = "T??m thu"
                }
                "DIASTOLIC" -> {
                    result = "T??m tr????ng"
                }
                "HIP" -> {
                    result = "H??NG"
                }
                "WAIST" -> {
                    result = "V??ng eo"
                }
                "PULSE" -> {
                    result = "Xung"
                }
            }
        }
        return result
    }

    fun redirectToChrome(url:String,context: Context) {
        if ( !isNullOrEmpty(url) ) {
            if ( url.contains("https://") || url.contains("http://") ) {
                launchChrome(url,context)
            } else {
                launchChrome("https://" + url,context)
            }
        }
    }

    fun launchChrome(url:String,context: Context) {
        try {
            Timber.e("url--->$url")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome is probably not installed
                // Try with the default browser
                intent.setPackage(null);
                context.startActivity(intent)

                // Chrome browser presumably not installed and open Kindle Browser
                //intent.setPackage("com.amazon.cloud9")
                //context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getParameterNameByCode(context: Context,code: String,desc: String): String {
        Timber.e("code--->$code")
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var parameterName : String = desc
        when(code) {
            "HEIGHT" -> parameterName = localResource.getString(R.string.HEIGHT)
            "WEIGHT" -> parameterName = localResource.getString(R.string.WEIGHT)

            "BP_SYS" -> parameterName = localResource.getString(R.string.SYSTOLIC)
            "BP_DIA" -> parameterName = localResource.getString(R.string.DIASTOLIC)

            "HIP" -> parameterName = localResource.getString(R.string.HIP)
            "WAIST" -> parameterName = localResource.getString(R.string.WAIST)

            "DIAB_FS" -> parameterName = localResource.getString(R.string.GLUCOSE_FASTING)
            "DIAB_RA" -> parameterName = localResource.getString(R.string.GLUCOSE_RANDOM)
            "CHOL_TOTAL" -> parameterName = localResource.getString(R.string.TOTAL_CHOLESTEROL)
            "CHOL_TRY" -> parameterName = localResource.getString(R.string.TRIGLYCERIDES)

            "RBC" -> parameterName = localResource.getString(R.string.RBC)
            "PLATELET" -> parameterName = localResource.getString(R.string.PLATELET_COUNT)
            "PCV" -> parameterName = localResource.getString(R.string.PCV)
            "NEUTROPHILS" -> parameterName = localResource.getString(R.string.NEUTROPHILS)
            "MONOCYTE" -> parameterName = localResource.getString(R.string.MONOCYTES)
            "MCV" -> parameterName = localResource.getString(R.string.MCV)
            "MCHC" -> parameterName = localResource.getString(R.string.MCHC)
            "LYMPHOCYTE" -> parameterName = localResource.getString(R.string.LYMPHOCYTES)
            "ESR" -> parameterName = localResource.getString(R.string.ESR)
            "EOSINOPHILS" -> parameterName = localResource.getString(R.string.EOSINOPHILS)
            "BASOPHILS" -> parameterName = localResource.getString(R.string.BASOPHILS)

            "SODIUMSERUM" -> parameterName = localResource.getString(R.string.SODIUM)
            "SERUMURICACID" -> parameterName = localResource.getString(R.string.URIC_ACID)
            "SERUMCREATININE" -> parameterName = localResource.getString(R.string.CREATININE)
            "POTASSIUMSERUM" -> parameterName = localResource.getString(R.string.POTASSIUM)
            "PHOSPHATE" -> parameterName = localResource.getString(R.string.PHOSPHATE)
            "CHLORIDEURINE" -> parameterName = localResource.getString(R.string.CHLORIDE)
            "CALCIUM" -> parameterName = localResource.getString(R.string.CALCIUM)
            "BLOODUREANITROGEN" -> parameterName = localResource.getString(R.string.UREA)

            "ALKALINE_PHOSPHATASE" -> parameterName = localResource.getString(R.string.ALP)
            "TOTAL_BILIRUBIN" -> parameterName = localResource.getString(R.string.TOTAL_BILIRUBIN)
            "PROTEINS" -> parameterName = localResource.getString(R.string.TOTAL_PROTEIN)
            "SGOT" -> parameterName = localResource.getString(R.string.SGOT)

            "URN_APPEARANCE" -> parameterName = localResource.getString(R.string.APPEARANCE)
            "URN_ALBUMIN_PROTEIN" -> parameterName = localResource.getString(R.string.URINE_PROTEIN)
            "URINESUGAR" -> parameterName = localResource.getString(R.string.URINE_GLUCOSE)
            "SPECIFIC_GRAVITY" -> parameterName = localResource.getString(R.string.SPECIFIC_GRAVITY)
            "PHVAL" -> parameterName = localResource.getString(R.string.PH_VALUE)
            "KETONE_BODIES" -> parameterName = localResource.getString(R.string.URINE_KETONE)
            "EPITHELIAL_CELLS" -> parameterName = localResource.getString(R.string.EPITHELIAL_CELLS)
            "CRYSTALS" -> parameterName = localResource.getString(R.string.CRYSTALS)
            "BACTERIA" -> parameterName = localResource.getString(R.string.BACTERIA)
        }
        return parameterName
    }

/*    fun redirectToChrome(url:String,context: Context) {
        //val urlString = "http://mysuperwebsite"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome is probably not installed
            // Try with the default browser
            intent.setPackage(null);
            context.startActivity(intent);

            // Chrome browser presumably not installed and open Kindle Browser
            //intent.setPackage("com.amazon.cloud9")
            //context.startActivity(intent)
        }
    }*/

}