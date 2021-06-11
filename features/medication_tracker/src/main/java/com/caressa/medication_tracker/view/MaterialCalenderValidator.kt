package com.caressa.medication_tracker.view

import android.os.Parcel
import android.os.Parcelable
import com.caressa.common.utils.Utilities
import com.google.android.material.datepicker.CalendarConstraints
import java.util.*

class MaterialCalenderValidator (private val minDate:Long)
    : CalendarConstraints.DateValidator {

    private val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    override fun isValid(date: Long): Boolean {
        return date >= minDate
/*        utc.timeInMillis = date
        val dayOfWeek = utc[Calendar.DAY_OF_WEEK]
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY*/
/*        if ( !Utilities.isNullOrEmptyOrZero(minDate.toString())
            && !Utilities.isNullOrEmptyOrZero(maxDate.toString()) ) {
            return (date >= minDate || date <= maxDate)
        } else if ( !Utilities.isNullOrEmptyOrZero(minDate.toString()) ) {
            return date >= minDate
        } else if ( !Utilities.isNullOrEmptyOrZero(maxDate.toString()) ) {
            return date <= maxDate
        } else {
            return true
        }*/
    }

    val CREATOR: Parcelable.Creator<MaterialCalenderValidator?> =
        object : Parcelable.Creator<MaterialCalenderValidator?> {
            override fun createFromParcel(source: Parcel): MaterialCalenderValidator {
                return MaterialCalenderValidator(minDate)
            }

            override fun newArray(size: Int): Array<MaterialCalenderValidator?> {
                return arrayOfNulls(size)
            }
        }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>()
        return hashedFields.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MaterialCalenderValidator

        if (minDate != other.minDate) return false
        if (utc != other.utc) return false
        if (CREATOR != other.CREATOR) return false

        return true
    }
}