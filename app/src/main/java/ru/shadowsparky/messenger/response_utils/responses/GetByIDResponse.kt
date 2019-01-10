package ru.shadowsparky.messenger.response_utils.responses

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKGetByIDMessages
import ru.shadowsparky.messenger.response_utils.pojos.VKMessages
import java.io.Serializable

class GetByIDResponse(
    val response: VKGetByIDMessages?,
    val error: Any?
) : Response, Serializable