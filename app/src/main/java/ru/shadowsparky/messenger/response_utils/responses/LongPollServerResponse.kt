package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPollServer
import java.io.Serializable

data class LongPollServerResponse(
    val response: VKLongPollServer
) : Response, Serializable