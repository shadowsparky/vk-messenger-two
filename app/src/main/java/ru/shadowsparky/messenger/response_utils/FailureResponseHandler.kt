/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import ru.shadowsparky.messenger.utils.Constansts
import java.net.UnknownHostException

class FailureResponseHandler {
    private var view: DisplayError? = null

    fun attachView(view: DisplayError) { this.view = view }

    fun onFailureResponse(reason: Throwable) {
        if (reason is UnknownHostException) {
            view!!.showError(Constansts.CONNECTION_ERROR_CODE)
        } else {
            view!!.showError(Constansts.UNHANDLED_EXCEPTION_CODE)
        }
    }
}