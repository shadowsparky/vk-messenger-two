/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKMessages
import java.io.Serializable

data class MessagesResponse(
    val response: VKMessages?,
    val error: ErrorResponse?
) : Response, Serializable













