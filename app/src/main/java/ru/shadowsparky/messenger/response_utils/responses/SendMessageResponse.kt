/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import java.io.Serializable

data class SendMessageResponse(
    val response: Int//,
//    val error: ErrorResponse?
) : Response, Serializable