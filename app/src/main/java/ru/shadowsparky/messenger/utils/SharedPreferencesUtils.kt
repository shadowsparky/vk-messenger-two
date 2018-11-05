/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPreferencesUtils(val context: Context) {
    companion object {
        val TOKEN = "TOKEN"
    }
    val preferences: SharedPreferences = context.getSharedPreferences("", MODE_PRIVATE)

    fun write(key: String, content: String) : Boolean = preferences.edit().putString(key, content).commit()
    fun remove(key: String) : Boolean = preferences.edit().remove(key).commit()
    fun removeAll() : Boolean = preferences.edit().clear().commit()
    fun read(key: String) : String = preferences.getString(key, "")!!
}