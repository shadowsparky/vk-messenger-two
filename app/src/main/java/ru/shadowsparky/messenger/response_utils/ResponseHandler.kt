/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import androidx.appcompat.app.AppCompatActivity
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.ToastUtils
import java.net.UnknownHostException

abstract class ResponseHandler(
        open val view: DisplayError
) {

    abstract fun onSuccessResponse(response: Response)

    open fun onFailureResponse(reason: Throwable) {
        if (reason is UnknownHostException) {
            view.showError(Constansts.CONNECTION_ERROR_CODE)
        } else {
            view.showError(Constansts.UNHANDLED_EXCEPTION_CODE)
        }
    }
}