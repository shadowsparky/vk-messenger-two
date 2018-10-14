/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

interface RequestHandler {
    fun onSuccessResponse(response: Response)
    fun onFailureResponse(error: Throwable)
}