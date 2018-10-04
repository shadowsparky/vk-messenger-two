/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.R
import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

class TextColorUtils(val context: Context) {

    fun getBlackText(text: String) : SpannableString {
        val spanText = SpannableString(text)
        spanText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, text.length, 0)
        return spanText
    }
}