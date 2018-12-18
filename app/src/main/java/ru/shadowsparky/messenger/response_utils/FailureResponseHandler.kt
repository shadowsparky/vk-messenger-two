/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import android.content.Context
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.shadowsparky.messenger.response_utils.pojos.VKError
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

    private fun showError(message: String) = toast.error(context!!, message)

    fun onFailureResponse(reason: Throwable) {
        when (reason) {
            is UnknownHostException -> showError("При соединении с сервером произошла ошибка. Проверьте ваше интернет соединение")
            is ClassCastException -> showError("При получении результата от сервера произошла критическая ошибка")
            is VKException -> showError("При соединении с сервером произошла ошибка. Причина: ${reason.error!!.error_msg}")
            else -> showError("Произошла неизвестная ошибка")
        }
    }
}