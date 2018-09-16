package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKMessage(
        val id: Int?,
        val date: Int?,
        val peer_id: Int?,
        val from_id: Int?,
        val text: String?,
        val random_id: Int?,
        val important: Boolean?,
        val payload: String?
) : Response