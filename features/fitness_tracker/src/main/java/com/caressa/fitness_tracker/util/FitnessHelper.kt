package com.caressa.fitness_tracker.util

import android.content.Context
import com.caressa.common.utils.DateHelper
import com.caressa.fitness_tracker.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class FitnessHelper(private val context: Context) {

    fun getWeeklyDayList() : MutableList<WeekDay> {
        val list : MutableList<WeekDay> = mutableListOf()
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val date = WeekDay()
            date.dayOfWeek = getFormattedValue("EEE", cal.time)
            date.dayOfMonth = getFormattedValue("dd", cal.time)
            date.dateString = getFormattedValue("yyyy-MM-dd", cal.time)
            date.date = cal.time
            // i=0 -> true, else false
            date.isToday = i == 0
            list.add(date)
        }
        return list
    }

    fun getMonthlyDayList() : MutableList<WeekDay> {
        val list : MutableList<WeekDay> = mutableListOf()
        val cal = Calendar.getInstance()
        for (i in 29 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val date = WeekDay()
            date.dayOfWeek = getFormattedValue("EEE", cal.time)
            date.dayOfMonth = getFormattedValue("dd", cal.time)
            date.dateString = getFormattedValue("yyyy-MM-dd", cal.time)
            date.date = cal.time
            // i=0 -> true, else false
            date.isToday = i == 0
            list.add(date)
        }
        return list
    }

    fun getActiveTime( stepCount : Int) : String {
        Timber.i("stepCount---> $stepCount")
        val timeFactor = 100 //avg 100 steps per minute
        //val activeTime = ( stepCount / activityTimeFactor)
        //activeTime = if (activeTime <= 0) 1 else activeTime // At least 1 min
        //return activeTime.toString() + " " + context.resources.getString(R.string.MIN)
        val at1 = (stepCount/timeFactor).toString()
        val at2 = (stepCount%timeFactor).toString()
        return DateHelper.getHourMinFromStrMinutes("$at1.$at2")
    }

    fun getCaloriesWithUnit(calories : String) : String {
        return calories + " " + context.resources.getString(R.string.KCAL)
    }

    private fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }


}