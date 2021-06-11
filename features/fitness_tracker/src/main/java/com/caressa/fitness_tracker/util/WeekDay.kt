package com.caressa.fitness_tracker.util

import java.util.*

data class WeekDay (
    var dayOfWeek: String = "",
    var dayOfMonth: String = "",
    var dateString: String = "",
    var date: Date = Date(),
    var isToday: Boolean = false
)