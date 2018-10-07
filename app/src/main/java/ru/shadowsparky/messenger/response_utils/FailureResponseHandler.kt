/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import android.content.Context
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.Constansts.Companion.CLASS_CAST_EXCEPTION_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.CONNECTION_ERROR_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.UNHANDLED_EXCEPTION_CODE
import ru.shadowsparky.messenger.utils.ToastUtils
import java.net.UnknownHostException
import javax.inject.Inject

class FailureResponseHandler {
    private var context: Context? = null
    @Inject protected lateinit var toast: ToastUtils

    init {
        App.component.inject(this)
    }

    fun attach(context: Context) { this.context = context }

    private fun showError(code: Int) {
        when(code) {
            CONNECTION_ERROR_CODE -> toast.error(context!!, "При соединении произошла ошибка. Проверьте свое интернет соединение")
            CLASS_CAST_EXCEPTION_CODE -> toast.error(context!!, "Результат был получен, но был неправильно обработан")
            else -> toast.error(context!!, "Произошла неизвестная ошибка")
        }
    }

    fun onFailureResponse(reason: Throwable) {
        when (reason) {
            is UnknownHostException -> showError(CONNECTION_ERROR_CODE)
            is ClassCastException -> showError(CLASS_CAST_EXCEPTION_CODE)
            else -> showError(UNHANDLED_EXCEPTION_CODE)
        }
    }
}