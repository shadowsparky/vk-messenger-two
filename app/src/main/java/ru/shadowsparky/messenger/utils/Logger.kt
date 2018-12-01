/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.util.Log
import android.util.Log.DEBUG
import android.util.Log.ERROR

open class Logger {
    fun print(message: String, threadDump: Boolean = true) {
        if (threadDump) {
            Log.println(DEBUG, "MAIN_TAG", "Thread: ${Thread.currentThread().name}. $message")
        } else {
            Log.println(DEBUG, "MAIN_TAG", message)
        }
    }

    fun printError(message: String, threadDump: Boolean = true) {
        if (threadDump) {
            Log.println(ERROR, "MAIN_TAG", "Thread: ${Thread.currentThread().name}. $message")
        } else {
            Log.println(ERROR, "MAIN_TAG", message)
        }
    }
}