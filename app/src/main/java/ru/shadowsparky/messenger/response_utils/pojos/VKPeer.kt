package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKPeer(
        val id: Int?,
        val type: String?,
        val local_id: Int?
) : Response