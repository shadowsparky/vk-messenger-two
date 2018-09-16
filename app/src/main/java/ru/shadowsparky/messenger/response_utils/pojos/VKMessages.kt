package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response


data class VKMessages (
        val count: Int?,
        val items: List<VKItems>?,
        val unread_count: Int?,
        val profiles: List<VKProfile>?,
        val groups: List<VKGroup>?
) : Response