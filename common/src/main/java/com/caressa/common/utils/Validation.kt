package com.caressa.common.utils

import android.util.Log
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

object Validation {

    fun validation_atRegister(
        moblie_no: String,
        email_id: String?
    ): BooleanArray {
        val validation = BooleanArray(2)
        // MOBILE VALIDATION FOR REGISTRATION CLIENT END
        if (moblie_no.length > 3) validation[0] = true else validation[0] = false
        // EMAIL VALIDATION  FOR REGISTRATION CLIENT END
        if (isValidEmail(email_id)) validation[1] = true else validation[1] = false
        return validation
    }

    fun isValidEmail(email: String?): Boolean {
        /*val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        val isValid = matcher.matches()
        Timber.d("isValidEmail--->$isValid")*/

        var isValid = false
        if (!email.isNullOrEmpty() && email.contains('@',true) && email.length > 3)
        {
            isValid = true
        }

        return isValid
    }

    /**
     * Check valid name with space.
     *
     * @param name
     * @return
     */
    fun isValidName(name: String): Boolean {
        var isValidName = false
        val namePattern: Regex = "^[a-zA-Z\\s]*\$".toRegex()
        if (!Utilities.isNullOrEmpty(name) && name.length > 3 && name.matches(namePattern)) {
            isValidName = true
        }
        return isValidName
    }
/*    fun isValidName(name: String): Boolean {
        var isValidName = false
        //		final String Regex = "[a-zA-Z]+";
        val Regex = "^[a-zA-Z][a-zA-Z ]+$".toRegex()
        isValidName = name.matches(Regex)
        return isValidName
    }*/

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val Regex = "[^\\d]"
        val PhoneDigits = phoneNumber.replace(Regex.toRegex(), "")
        val isValid = PhoneDigits.length > 8 && !phoneNumber.startsWith("0")
        Timber.d("isValidPhoneNumber--->$isValid")
        return isValid
        /*String Regex = "[^\\d]";
        String PhoneDigits = phoneNumber.replaceAll(Regex, "");
		return (PhoneDigits.length()!=10);*/
    }

    /**
     * isValidPhoneNumberCountryBased
     *
     * @param phoneNumber
     * @param strDialingCode
     * @return
     */
    fun isValidPhoneNumberDialingCodeBased(
        phoneNumber: String,
        strDialingCode: String
    ): Boolean {
        var isValidNumber = false
        val Regex = getDialingCodeBasedRegExp(strDialingCode).toRegex()
        Timber.d("Validation" + "Regex$Regex")
        isValidNumber = if (phoneNumber.matches(Regex)) {
            true
        } else {
            false
        }
        return isValidNumber
    }

    /**
     * is Valid Password
     * Explanation:
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     */
/*    fun isValidPassword(password: String): Boolean { //String password = "aaZZa44@";
        //String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()
        Timber.d("isValidPassword-->" + password.matches(pattern))
        return password.matches(pattern)
    }*/

    fun isValidPassword(password: String): Boolean {
        var isPassword = false
        if (!Utilities.isNullOrEmpty(password)) {
            if (password.length > 5) {
                isPassword = true
            }
        }
        return isPassword
    }

    fun isEmpty(etText: String): Boolean {
        return if (etText.trim { it <= ' ' }.length > 0) {
            false
        } else {
            true
        }
    }

    /**
     * get Dialing Code
     *
     * @param strDialingCode
     */
    fun getDialingCodeBasedRegExp(strDialingCode: String): String {
        Log.d("Validation", "strDialingCode  $strDialingCode")
        var strRegExp = ""
        if (!strDialingCode.isNullOrEmpty()) {
            if (strDialingCode.equals("+971", ignoreCase = true)) {
                strRegExp = "^[{0,1}]?(?:2|3|4|6|7|9|50|51|52|55|56)[0-9]{7}$"
                //strRegExp = "^(?:\\+971|00971|0)?(?:50|51|52|55|56|2|3|4|6|7|9)\\d{7}$";
            } else if (strDialingCode.equals("+61", ignoreCase = true)) {
                strRegExp = "^(?:\\(0\\)[23478]|\\(?0?[23478]\\)?)\\d{8}$"
            } else if (strDialingCode.equals("+91", ignoreCase = true)) {
                strRegExp = "^[789]\\d{9}$"
            }
        }
        return strRegExp
    }
}