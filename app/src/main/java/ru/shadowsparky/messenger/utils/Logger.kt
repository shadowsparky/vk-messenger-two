package ru.shadowsparky.messenger.utils

import android.util.Log
import android.util.Log.DEBUG

open class Logger {
    fun print(message: String) = Log.println(DEBUG, "MAIN_TAG", message)
}