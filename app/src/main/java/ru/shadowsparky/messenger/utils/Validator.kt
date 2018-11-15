/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.subscribeBy

class Validator {

    fun verifyText(text: EditText, callback: (Boolean) -> Unit) {
        RxTextView.textChanges(text)
            .map { it.isNotBlank() }
            .subscribeBy (
                onNext = { callback(it) }
            )
    }
}