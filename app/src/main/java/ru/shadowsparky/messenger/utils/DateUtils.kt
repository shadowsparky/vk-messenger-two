package ru.shadowsparky.messenger.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    fun fromUnixToDateString(unix: Long) : String {
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(fromUnixToDate(unix))
    }

    fun fromUnixToDate(unix: Long) : Date = Date(unix*1000L)
}