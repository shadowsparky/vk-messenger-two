/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKHistory
import java.io.Serializable

data class HistoryResponse(
    val response: VKHistory,
    val error: Any?
) : Response, Serializable