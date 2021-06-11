package com.caressa.common.utils

import android.text.TextUtils
import android.util.Log
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object DateHelper {
    val DATETIMEFORMAT = "dd-MMM-yyyy HH:mm:ss"
    val DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss"
    var SERVER_DATE_YYYYMMDD = "yyyy-MM-dd"
    var DISPLAY_DATE_DDMMMYYYY = "dd-MMM-yyyy"
    var DATEFORMAT_DDMMMYYYY_NEW = "dd MMM yyyy"
    var DATEFORMAT_MATERIAL = "MMM dd, yyyy"
    var DISPLAY_DATE_DDMMYYYY = "dd/MM/yyyy"
    var LONG_DATE_FORMAT = "EEE, dd MMM"
    var FULL_DISPLAY_DATE_FORMAT = "EEEE, dd MMM yyyy"
    var SHORT_DATE_FORMAT = "dd MMM"

    //note: doesn't check for null
    val utCdatetimeAsDate: Date?
        get() = stringDateToDate(currentUTCdatetimeAsString)

    val currentDate: Date?
        get() = utCdatetimeAsDate

    /* Current Date : */ val currentTimeAs_hh_mm: String
        get() {

            val mcurrentTime = Calendar.getInstance()
            var hour = "" + mcurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = "" + mcurrentTime.get(Calendar.MINUTE)


            if (hour.length == 1) {
                hour = "0$hour"
            }
            if (minute.length == 1) {
                minute = "0$minute"
            }
            return "$hour:$minute"
        }

    val currentTimeAs_hh_mm_ss: String
        get() {

            val mcurrentTime = Calendar.getInstance()
            var hour = "" + mcurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = "" + mcurrentTime.get(Calendar.MINUTE)
            var second = "" + mcurrentTime.get(Calendar.SECOND)

            if (hour.length == 1) {
                hour = "0$hour"
            }
            if (minute.length == 1) {
                minute = "0$minute"
            }
            if (second.length == 1) {
                second = "0$second"
            }
            return "$hour:$minute:$second"
        }

    /* Current Date : */ val currentDateAsStringyyyyMMdd: String
        get() {
            val calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy-MM-dd")
            return df.format(calendar.time)
        }


    /* Current Date : */ val currentDateAsStringddMMMyyyy: String
        get() {
            val calendar = Calendar.getInstance()
            val df = SimpleDateFormat("dd-MMM-yyyy")
            return df.format(calendar.time)
        }

    /* Current Date : */ val currentDateAsStringddMMMyyyyNew: String
        get() {
            val calendar = Calendar.getInstance()
            val df = SimpleDateFormat("dd MMM yyyy")
            return df.format(calendar.time)
        }

    /* Current Date : */ val currentDateTime: String
        get() {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

            return calendar.timeInMillis.toString()
        }

    val currentUTCdatetimeAsString: String
        get() {
            val sdf = SimpleDateFormat(DISPLAY_DATE_DDMMMYYYY)
            //sdf.timeZone = TimeZone.getTimeZone("UTC")

            return sdf.format(Date())
        }

    val currentUTCDatetimeInMillisecAsString: String
        get() {
            val sdf = SimpleDateFormat(DATE_FORMAT_UTC)
            //sdf.timeZone = TimeZone.getTimeZone("UTC")

            return sdf.format(Date())
        }

    /**
     * gets Current Time
     *
     * @return
     */
    val currentTime: Long
        get() {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            return if (!TextUtils.isEmpty(calendar.timeInMillis.toString() + "")) {
                calendar.timeInMillis
            } else {
                0
            }
        }

    fun getTimeIn12HrFormatAmOrPm(time: String): String {
        var timeInAmPm = ""
        try {
            val _24HourSDF = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val _12HourSDF = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val _24HourDt = _24HourSDF.parse(time)
            timeInAmPm = _12HourSDF.format(_24HourDt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeInAmPm
    }

    fun addHoursToDate(date: Date, hours: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR, hours)
        return calendar.time
    }

    fun getDateBeforeOrAfterGivenDays(date: String, days: Int): String {
        var newDate = ""
        try {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val c = Calendar.getInstance()
            //Setting the date to the given date
            c.time = sdf.parse(date)
            c.add(Calendar.DATE, days - 1)
            newDate = sdf.format(c.time)
            println("DateBeforeOrAfter=>$newDate")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return newDate
    }

    fun getDateDifference(dateBeforeString: String, dateAfterString: String): Long {
        var difference_In_Days: Long = 0
        try {
            if (!Utilities.isNullOrEmpty(dateBeforeString) && !Utilities.isNullOrEmpty(dateBeforeString)) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val d1 = sdf.parse(dateBeforeString)
                val d2 = sdf.parse(dateAfterString)
                val difference_In_Time = d2.time - d1.time
                difference_In_Days = difference_In_Time / (1000 * 60 * 60 * 24) % 365
                println("difference_In_Days=>$difference_In_Days")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return difference_In_Days
    }

    fun getDateAs_ddmmyy(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("dd/MM/yyyy")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()
        }

        return RecordDatetemp
    }

    fun getDateAs_yyyy_mm_dd(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("yyyy-MM-dd")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }

    fun getDateTimeAs_EEE_MMM_yyyy(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("EEEE, MMM")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()

        }


        return RecordDatetemp
    }

    fun convertGMTToyyyymmdd(date: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        try {
            val milliSeconds = java.lang.Long.parseLong(date)
            calendar.timeInMillis = milliSeconds
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formatter.format(calendar.time)
    }

    fun getDateAs_ddMMyyyy(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("dd/MM/yyyy")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }

    fun getTimeDifference(t1: String, t2: String): Int {

        val simpleDateFormat = SimpleDateFormat("HH:mm")
        var min = 0
        try {
            val date1 = simpleDateFormat.parse(t1)
            val date2 = simpleDateFormat.parse(t2)

            val difference = date2.time - date1.time
            val days = (difference / (1000 * 60 * 60 * 24)).toInt()
            var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
            min =
                (difference - (1000 * 60 * 60 * 24 * days).toLong() - (1000 * 60 * 60 * hours).toLong()).toInt() / (1000 * 60)
            hours = if (hours < 0) -hours else hours
            Log.i("======= Hours", " :: $hours")

        } catch (e: ParseException) {

            e.printStackTrace()
        }

        return min
    }

    fun convertDateTimeValue(
        dateValue: String?,
        sourceFormat: String,
        destinationFormat: String
    ): String? {
        var dateValue = dateValue
        /* Current Date : */
        val dateFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())

        val date1: Date
        try {
            if (dateValue != null && dateValue != "") {
                date1 = dateFormat.parse(dateValue)
                val df = SimpleDateFormat(destinationFormat, Locale.getDefault())
                dateValue = df.format(date1)
            } else {
                dateValue = ""
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }

        return dateValue
    }

    fun convertMaterialPickerDateToServerdate(dateValue: String): String {
        var strDate = ""
        val dateFormat = SimpleDateFormat(DATEFORMAT_MATERIAL, Locale.getDefault())
        var date1 = Calendar.getInstance().time
        try {
            if ( !Utilities.isNullOrEmpty(dateValue) ) {
                date1 = dateFormat.parse(dateValue)
            }

            if (date1 != null) {
                val df = SimpleDateFormat(SERVER_DATE_YYYYMMDD, Locale.getDefault())
                strDate = df.format(date1)
            } else {
                strDate = ""
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strDate
    }

    fun convertStringDateToDate(dateValue: String?, sourceFormat: String): Date {
        /* Current Date : */
        val dateFormat = SimpleDateFormat(sourceFormat, Locale.getDefault())
        var date1 = Calendar.getInstance().time
        try {
            if (dateValue != null && dateValue != "") {
                date1 = dateFormat.parse(dateValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return date1
    }

    fun convertDateToStr(dateValue: Date?, destinationFormat: String): String {
        var strDate = ""
        try {
            if (dateValue != null) {
                val df = SimpleDateFormat(destinationFormat, Locale.getDefault())
                strDate = df.format(dateValue)
            } else {
                strDate = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strDate
    }

    fun getFormattedTime(hour: String, minute: String): String {
        var hour = hour
        var minute = minute
        if (hour.length == 1) {
            hour = "0$hour"
        }
        if (minute.length == 1) {
            minute = "0$minute"
        }
        return "$hour:$minute"
    }

    fun getDateTimeAs_ddMMMyyyy_ToMilliSec(date: String): String {

        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat(DISPLAY_DATE_DDMMMYYYY)

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            RecordDatetemp = date1.time.toString()
            //            System.out.println("Date in milli :: " + RecordDatetemp);

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }

    //Get date time to millisceons

    fun getDateTimeAs_ddMMMyyyy_FromUtcToMilliSec(date: String): String {

        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat(DATE_FORMAT_UTC)

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            RecordDatetemp = date1.time.toString()
            //            System.out.println("Date in milli :: " + RecordDatetemp);

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }

    fun getDateTimeAs_ddMMMyyyy(date: String?): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val date1: Date
        try {
            if (date != null && date.length == 11) {
                val df = SimpleDateFormat("dd-MMM-yyyy")
                date1 = df.parse(date)
                RecordDatetemp = df.format(date1)
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                date1 = dateFormat.parse(date)
                val df = SimpleDateFormat("dd-MMM-yyyy")
                RecordDatetemp = df.format(date1)
            }
        } catch (e: ParseException) {

            e.printStackTrace()
        }
        return RecordDatetemp
    }

    fun getDateTimeAs_ddMMMyyyyNew(date: String?): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val date1: Date
        try {
            if (date != null && date.length == 11) {
                val df = SimpleDateFormat("dd MMM yyyy")
                date1 = df.parse(date)
                RecordDatetemp = df.format(date1)
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                date1 = dateFormat.parse(date)
                val df = SimpleDateFormat("dd MMM yyyy")
                RecordDatetemp = df.format(date1)
            }
        } catch (e: ParseException) {

            e.printStackTrace()
        }
        return RecordDatetemp
    }

    fun getDateTimeAs_yyyyMMdd(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("yyyy-MM-dd")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()

        }


        return RecordDatetemp
    }

    fun getDifferenceInDays(startDate: String, endDate: String): Int {

        val myFormat = SimpleDateFormat(SERVER_DATE_YYYYMMDD,Locale.getDefault())
        var daysBetween = 0f
        try {
            val dateBefore = myFormat.parse(startDate)
            val dateAfter = myFormat.parse(endDate)
            val difference = dateAfter.time - dateBefore.time
            daysBetween = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS).toFloat()
            Timber.e("DateDifference--->daysBetween")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return daysBetween.toInt()
    }

    fun getDateDifferenceInDays(dateBeforeString: String, dateAfterString: String): Int {

        val date1 = dateBeforeString.replace("/".toRegex(), " ")
        val date2 = dateAfterString.replace("/".toRegex(), " ")

        val myFormat = SimpleDateFormat("dd MM yyyy")

        var daysBetween = 0f
        try {
            val dateBefore = myFormat.parse(date1)
            val dateAfter = myFormat.parse(date2)
            val difference = dateAfter.time - dateBefore.time
            // float daysBetween = (difference / (1000*60*60*24));
            //     You can also convert the milliseconds to days using this method
            daysBetween = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS).toFloat()
            //System.out.println("Number of Days between dates: " + daysBetween);
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return daysBetween.toInt()
    }

    fun getDateTimeAs_ddmmyy(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("dd/MM/yyyy")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }

    fun stringDateToDate(format: String, strDate: String): Date {
        var dateToReturn: Date? = null
        val dateFormat = SimpleDateFormat(format)


        val formatter = SimpleDateFormat(format)

        try {
            // dateToReturn = (Date) dateFormat.parse(strDate);
            dateToReturn = formatter.parse(strDate) as Date
            Timber.e("Converted date is--->$dateToReturn")

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateToReturn!!
    }

    fun stringDateToDate(StrDate: String): Date? {
        var dateToReturn: Date? = null
        val dateFormat = SimpleDateFormat(DISPLAY_DATE_DDMMMYYYY)

        try {
            dateToReturn = dateFormat.parse(StrDate) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dateToReturn
    }

    fun getDate(stringDate: String, format: String): Date {
        val date = stringDateToDate(format, stringDate)
        //Timber.e("formatDateValue--->$date")
        return date
    }

    fun dateToString(date: Date, format: String): String {
        var dateToReturn = ""
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())

        try {
            dateToReturn = dateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dateToReturn
    }

    fun formatDateValue(dateAsString: String): String? {
        val newformat = SimpleDateFormat("dd-MMM-yyyy")
        try {

            if (dateAsString.contains("T")) {
                if (dateAsString.indexOf("-") <= 3) {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("dd-MM-yyyy")
                    return newformat.format(oldformat.parse(datestring))
                } else {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("yyyy-MM-dd")
                    return newformat.format(oldformat.parse(datestring))
                }
            } else {
                if (dateAsString.indexOf("-") <= 3) {
                    val oldformat = SimpleDateFormat("dd-MMM-yyyy")
                    return newformat.format(oldformat.parse(dateAsString))
                } else if (Integer.parseInt(dateAsString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) > 13) {
                    val oldformat = SimpleDateFormat("yyyy-MM-dd")
                    return newformat.format(oldformat.parse(dateAsString))
                } else {
                    val oldformat = SimpleDateFormat("MM-dd-yyyy")
                    return newformat.format(oldformat.parse(dateAsString))
                }
            }
        } catch (e: Exception) {
            println("formatDateValue: " + e.stackTrace)
            println("formatDateValue: " + e.toString())
        }

        return null
    }

    fun convertServerDateToTime(dateAsString: String): String {
        val newformat = SimpleDateFormat("dd-MMM-yyyy")
        var dateVal: Date? = null
        try {
            if (TextUtils.isEmpty(dateAsString) == false) {
                if (dateAsString.contains("T")) {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("yyyy-MM-dd")
                    dateVal = oldformat.parse(dateAsString)
                } else {
                    if (Integer.parseInt(dateAsString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) > 13) {
                        val oldformat = SimpleDateFormat("yyyy-MM-dd")
                        dateVal = oldformat.parse(dateAsString)
                    } else {
                        val oldformat = SimpleDateFormat("MM-dd-yyyy")
                        dateVal = oldformat.parse(dateAsString)
                    }
                }
                return dateVal!!.time.toString()
            }
        } catch (e: Exception) {
            return ""
        }

        return ""
    }

    fun convertStrToDateOBJ(dateAsString: String): Date? {
        var dateVal: Date? = null
        try {
            if (!TextUtils.isEmpty(dateAsString)) {
                if (dateAsString.contains("T")) {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateVal = oldformat.parse(datestring)
                } else {
                    if (Integer.parseInt(dateAsString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) > 13) {
                        val oldformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateVal = oldformat.parse(dateAsString)
                    } else {
                        val oldformat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                        dateVal = oldformat.parse(dateAsString)
                    }
                }
            }
        } catch (e: Exception) {
            return null
        }

        return dateVal
    }

    fun convertServerDateToDBDate(dateAsString: String): String {
        val dbFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        var dateVal: Date? = null
        try {
            if (!TextUtils.isEmpty(dateAsString)) {
                if (dateAsString.contains("T")) {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateVal = oldformat.parse(datestring)
                } else {
                    if (Integer.parseInt(dateAsString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) > 13) {
                        val oldformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateVal = oldformat.parse(dateAsString)
                    } else {
                        val oldformat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                        dateVal = oldformat.parse(dateAsString)
                    }
                }
                return dbFormat.format(dateVal!!.time)
            }
        } catch (e: Exception) {
            return ""
        }

        return ""
    }

    fun formatDateValue(format: String, dateAsString: String): String? {
        val newformat = SimpleDateFormat(format)
        try {
            if (dateAsString.contains("T")) {
                if (dateAsString.indexOf("-") <= 3) {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("dd-MM-yyyy")
                    return newformat.format(oldformat.parse(datestring))
                } else {
                    val datestring =
                        dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val oldformat = SimpleDateFormat("yyyy-MM-dd")
                    return newformat.format(oldformat.parse(datestring))
                }
            } else {
                if (dateAsString.indexOf("-") <= 3) {
                    val oldformat = SimpleDateFormat("dd-MMM-yyyy")
                    return newformat.format(oldformat.parse(dateAsString))
                } else if (Integer.parseInt(dateAsString.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) > 13) {
                    val oldformat = SimpleDateFormat("yyyy-MM-dd")
                    return newformat.format(oldformat.parse(dateAsString))
                } else {
                    val oldformat = SimpleDateFormat("MM-dd-yyyy")
                    return newformat.format(oldformat.parse(dateAsString))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun convertTimeValueToDate(timevalue: String): String {
        /* Current Date : */
        var datetemp = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        val date1: Date
        try {
            if (TextUtils.isEmpty(timevalue) == false) {
                val date = Date()
                date.time = java.lang.Long.parseLong(timevalue)
                datetemp = dateFormat.format(date)
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        return datetemp
    }

    fun convertDateToDayName(dateValue: String, sourceFormat: String): String{
        val date = convertStringDateToDate(dateValue,sourceFormat)
//        println(SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()))
        return date.date.toString()+"\n"+ SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime())
    }

    fun convertUpdateTimeValueToDate(timevalue: String): String {
        /* Current Date : */
        var datetemp = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        println("Changed updated time :$timevalue")

        val date1: Date
        try {
            if (TextUtils.isEmpty(timevalue) == false) {
                var date = Date()
                date.time = java.lang.Long.parseLong(timevalue)
                datetemp = dateFormat.format(date)


                val sdf = SimpleDateFormat("yyyy:MM:dd:HH:mm")
                // String currentDateandTime = sdf.format(new Date());

                date = dateFormat.parse(timevalue)
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.HOUR, 1)

                println("Time here " + calendar.time)

                date.time = java.lang.Long.parseLong(calendar.time.toString())
                datetemp = dateFormat.format(date)

            }


        } catch (e: Exception) {

            e.printStackTrace()
        }


        return datetemp
    }

/*    fun calculateYears(dateAsString: String): Int {
        val newformat = SimpleDateFormat("dd-MMM-yyyy")
        var date: Date? = null
        try {
            if (dateAsString.contains("T")) {
                val datestring = dateAsString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                val oldformat = SimpleDateFormat("yyyy-MM-dd")
                date = oldformat.parse(datestring)
            } else {
                if (dateAsString.length > 10) {
                    val oldformat = SimpleDateFormat("dd-MMM-yyyy")
                    date = oldformat.parse(dateAsString)
                } else {
                    val oldformat = SimpleDateFormat("MM-dd-yyyy")
                    date = oldformat.parse(dateAsString)
                }
            }
            if (date != null) {
                return calculateYears(date.year, date.month, date.day)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //            return 0;
        }

        return 0
    }*/

    fun calculatePersonAge(dateOfBirth: String): String {
        var dateOfBirth = dateOfBirth
        try {

            dateOfBirth = formatDateValue(DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
            val dob = formatDateValue(SERVER_DATE_YYYYMMDD, dateOfBirth)!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var years = Integer.parseInt(dob[0])
            var months = Integer.parseInt(dob[1])
            var days = Integer.parseInt(dob[2])

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val birthDate = sdf.parse("$days/$months/$years") // my date of birth :-)

            //create calendar object for birth day
            val birthDay = Calendar.getInstance()
            birthDay.timeInMillis = birthDate.time

            //create calendar object for current day
            val currentTime = System.currentTimeMillis()
            val now = Calendar.getInstance()
            now.timeInMillis = currentTime

            //Get difference between years
            years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)

            val currMonth = now.get(Calendar.MONTH) + 1
            val birthMonth = birthDay.get(Calendar.MONTH) + 1

            //Get difference between months
            months = currMonth - birthMonth

            //if month difference is in negative then reduce years by one and calculate the number of months.
            if (months < 0) {
                years--
                months = 12 - birthMonth + currMonth
                if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                    months--
            } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                years--
                months = 11
            }

            //Calculate the days
            if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
                days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE)
            else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                val today = now.get(Calendar.DAY_OF_MONTH)
                now.add(Calendar.MONTH, -1)
                days =
                    now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today
            } else {
                days = 0
                if (months == 12) {
                    years++
                    months = 0
                }
            }
            //Create new Age object
            println("Age: $years $months $days")
            if (years > 0) {
                return if (years == 1) {
                    "$years Year"
                } else {
                    "$years Years"
                }
            } else if (years <= 0 && months == 1) {
                return if (now.get(Calendar.DATE) == birthDay.get(Calendar.DATE)) {
                    "1 Month"
                } else if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE)) {
                    "$months Month"
                } else {
                    if (days == 1) {
                        "$days Day"
                    } else {
                        "$days Days"
                    }
                }
            } else if (years <= 0 && months > 1) {
                return "$months Months"
            } else if (years <= 0 && months <= 0 && days > 0) {
                return if (days == 1) {
                    "$days Day"
                } else {
                    "$days Days"
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0.toString() + " Years"
    }

    fun calculateYears(year: Int, month: Int, day: Int): Int {
        val cal = GregorianCalendar()
        val y: Int
        val m: Int
        val d: Int
        var a: Int

        y = cal.get(Calendar.YEAR)
        m = cal.get(Calendar.MONTH)
        d = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(year, month, day)
        a = y - cal.get(Calendar.YEAR)
        if (m < cal.get(Calendar.MONTH) || m == cal.get(Calendar.MONTH) && d < cal
                .get(Calendar.DAY_OF_MONTH)
        ) {
            --a
        }
        if (a < 0)
            a = 0
        Log.d("DateHelper", "calculateYears  --> $a")
        return a
    }

    /**
     * Calculates age from birthdate in months and days.
     *
     * @param dateOfBirth
     * @return
     */
    fun calculateDOB(dateOfBirth: String): String {
        try {
            val startCalendar = GregorianCalendar()
            val sdf = SimpleDateFormat("dd-MMM-yyyy")
            val date = sdf.parse(dateOfBirth)
            startCalendar.time = date
            val endCalendar = GregorianCalendar()
            endCalendar.time = endCalendar.time

            val years = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
            val months = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
            val days =
                endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH)

            println("AgeNew: $years $months $days")
            if (years > 0) {
                return years.toString() + " Years"
            } else if (years <= 0 && months > 0) {
                return months.toString() + " Months"
            } else if (years <= 0 && months <= 0 && days > 0) {
                return days.toString() + " Days"
            } else if (years <= 0 && months <= 0 && days >= 0) {
                return "1 Day"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0.toString() + " Years"
    }

    fun isDateAbove18Years(dateIn_ddMMMyyyy: String): Boolean {
        val givenDate = Calendar.getInstance()
        var date: Date? = null
        try {

            if (dateIn_ddMMMyyyy.contains("T")) {
                val datestring =
                    dateIn_ddMMMyyyy.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                val oldformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                date = oldformat.parse(datestring)
            } else {
                if (dateIn_ddMMMyyyy.length > 10) {
                    val oldformat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                    date = oldformat.parse(dateIn_ddMMMyyyy)
                } else {
                    val oldformat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                    date = oldformat.parse(dateIn_ddMMMyyyy)
                }
            }

            //date = sdf.parse(dateIn_ddMMMyyyy);
            givenDate.time = date
            val currentDate = Calendar.getInstance()
            currentDate.time = currentDate.time

            val yearsDifference = currentDate.get(Calendar.YEAR) - givenDate.get(Calendar.YEAR)
            Log.d("DateHelper", "isDateAbove18Years - yearsDifference-->  $yearsDifference")
            return yearsDifference >= 18
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Calculates age from birthdate in months and days.
     *
     * @param years
     * @param months
     * @param days
     * @return
     */
    fun calculateAge(years: Int, months: Int, days: Int): String {
        var years = years
        var months = months
        var days = days
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val birthDate =
                sdf.parse(days.toString() + "/" + months + "/" + years) //Yeh !! It's my date of birth :-)
            //create calendar object for birth day
            val birthDay = Calendar.getInstance()
            birthDay.timeInMillis = birthDate.time
            //create calendar object for current day
            val currentTime = System.currentTimeMillis()
            val now = Calendar.getInstance()
            now.timeInMillis = currentTime
            //Get difference between years
            years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
            val currMonth = now.get(Calendar.MONTH) + 1
            val birthMonth = birthDay.get(Calendar.MONTH) + 1
            //Get difference between months
            months = currMonth - birthMonth
            //if month difference is in negative then reduce years by one and calculate the number of months.
            if (months < 0) {
                years--
                months = 12 - birthMonth + currMonth
                if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                    months--
            } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                years--
                months = 11
            }
            //Calculate the days
            if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
                days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE)
            else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                val today = now.get(Calendar.DAY_OF_MONTH)
                now.add(Calendar.MONTH, -1)
                days =
                    now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today
            } else {
                days = 0
                if (months == 12) {
                    years++
                    months = 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Create new Age object
        println("Age: $years $months $days")
        return if (years > 0) {
            years.toString() + " Years"
        } else if (years <= 0 && months > 0) {
            months.toString() + " Months"
        } else if (years <= 0 && months <= 0 && days > 0) {
            days.toString() + " Days"
        } else {
            0.toString() + ""
        }
    }

    fun getDateTimeAs_ddMMMyyyy_FromUtc(date: String): String {
        /* Current Date : */
        var RecordDatetemp = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        val date1: Date
        try {
            date1 = dateFormat.parse(date)
            val df = SimpleDateFormat("dd-MMM-yyyy")
            RecordDatetemp = df.format(date1)

        } catch (e: ParseException) {

            e.printStackTrace()
        }


        return RecordDatetemp
    }


    fun getTimeDiffrenceAsLong(previous: String): Long {
        if (previous.equals("", ignoreCase = true)) {
            return 1000000
        } else {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            val time = "0 seconds ago"
            var minuteTime: Long = 0
            val difference = calendar.timeInMillis - java.lang.Long.parseLong(previous)
            var x = difference / 1000L
            val seconds = x % 60L
            if (x > 0L) {
                x /= 60L
                if (x > 0L) {
                    minuteTime = x
                    x /= 60L
                }
            }
            return minuteTime
        }
    }

    fun getTimeDiffrence(previous: String): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        var time = "0 seconds ago"
        val difference = calendar.timeInMillis - java.lang.Long.parseLong(previous)
        var x = difference / 1000
        val seconds = x % 60
        if (x > 0) {
            time = seconds.toString() + " seconds ago"
            x /= 60
            val minutes = x % 60
            if (x > 0) {
                time = minutes.toString() + " minutes ago"
                x /= 60
                val hours = x % 24

                if (x > 0) {
                    time = hours.toString() + " hours ago"
                    x /= 24
                    val days = x
                    if (x > 0) {
                        time = days.toString() + " days ago"
                    }

                }

            }
        }

        return time
    }

    fun getHH_MM_FromMinutes(time: Int): String {
        if (time > 0) {
            val hours = time / 60 //since both are ints, you get an int
            val minutes = time % 60
            return String.format("%02d:%02d", hours, minutes)
        }
        return "0"
    }

    fun getHourMinFromStrMinutes(strTime: String): String {
        if ( !Utilities.isNullOrEmptyOrZero(strTime) ) {
            val time = strTime.toDouble().roundToInt()
            val hours = time / 60 //since both are ints, you get an int
            val minutes = time % 60
            return if (hours > 0) {
                hours.toString() + "h" + " " + minutes + "m"
            } else {
                if (minutes <= 1) {
                    "$minutes min"
                } else {
                    "$minutes mins"
                }
            }
        }
        return "0 min"
    }

    fun getHourMinFromMinutes(time: Int?): String {
        if ( time != null && time > 0) {
            val hours = time / 60 //since both are ints, you get an int
            val minutes = time % 60
            return if (hours > 0) {
                hours.toString() + "h" + " " + minutes + "m"
            } else {
                if (minutes <= 1) {
                    "$minutes min"
                } else {
                    "$minutes mins"
                }
            }
        }
        return "0 min"
    }

    fun getDayText(dayVal: String): String {
        return if (dayVal.equals("1", ignoreCase = true)) {
            "Sunday"
        } else if (dayVal.equals("2", ignoreCase = true)) {
            "Monday"
        } else if (dayVal.equals("3", ignoreCase = true)) {
            "Tuesday"
        } else if (dayVal.equals("4", ignoreCase = true)) {
            "Wednesday"
        } else if (dayVal.equals("5", ignoreCase = true)) {
            "Thursday"
        } else if (dayVal.equals("6", ignoreCase = true)) {
            "Friday"
        } else if (dayVal.equals("7", ignoreCase = true)) {
            "Saturday"
        } else {
            ""
        }
    }

    fun getDayOfMonthSuffix(day: Int): String {
        var suffix = day.toString() + ""
        if (day in 1..31) {

            if (day in 11..13) {
                suffix = day.toString() + "th"
            } else {
                when (day % 10) {
                    1 -> suffix = day.toString() + "st"
                    2 -> suffix = day.toString() + "nd"
                    3 -> suffix = day.toString() + "rd"
                    else -> suffix = day.toString() + "th"
                }
            }
        }
        return suffix
    }

    fun convertStringToDate(dobInddmmyy: String): Date? {
        val format = SimpleDateFormat(DISPLAY_DATE_DDMMMYYYY)
        var date: Date? = null
        try {
            date = format.parse(dobInddmmyy)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun differenceInDays(startDate: Date, endDate: Date): Int {
        val duration = startDate.time - endDate.time
        return (TimeUnit.MILLISECONDS.toDays(duration) + 1).toInt()
    }

    fun dateToCalendar(date: Date?): Calendar? {
        if (date != null) {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal
        } else {
            return null
        }
    }
}
