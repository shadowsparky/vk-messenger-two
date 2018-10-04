/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import io.reactivex.Single
import io.reactivex.disposables.Disposable

class BuiltRequest(private val request: Disposable) {

    fun getDisposable() : Disposable = request
}