/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKHistory

data class HistoryResponse(
    val response: VKHistory//,
//    val error: ErrorResponse?
) : Response