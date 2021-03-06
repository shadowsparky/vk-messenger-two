/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

class ToastUtils {
    fun error(context: Context, message: String) = Toasty.error(context, message, Toast.LENGTH_SHORT, true).show()

    fun warning(context: Context, message: String) = Toasty.warning(context, message, Toast.LENGTH_SHORT, true).show()

    fun info(context: Context, message: String) = Toasty.info(context, message, Toast.LENGTH_SHORT, true).show()

    fun success(context: Context, message: String) = Toasty.success(context, message, Toast.LENGTH_SHORT, true).show()

    fun normal(context: Context, message: String) = Toasty.normal(context, message, Toast.LENGTH_SHORT).show()
}