/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    fun fromUnixToTimeString(unix: Long) : String {
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(fromUnixToDate(unix))
    }

    fun fromUnixToDateString(unix: Long) : String {
        val formatter = SimpleDateFormat("yyyy.MM.dd")
        return formatter.format(fromUnixToDate(unix))
    }

    fun fromUnixToDateAndTime(unix: Long) : String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return formatter.format(fromUnixToDate(unix))
    }

    fun fromUnixToDateAndTimeCalendar(unix: Long) : Calendar {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return formatter.calendar
    }

    fun fromUnixToStrictDate(unix: Long) : Date {
        val formatter = SimpleDateFormat("yyyy.MM.dd")
        return formatter.parse(fromUnixToDateString(unix))
    }

    protected fun fromUnixToDate(unix: Long) : Date = Date(unix*1000L)
}