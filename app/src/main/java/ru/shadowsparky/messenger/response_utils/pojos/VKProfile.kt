package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKProfile(
        val id: Int?,
        val first_name: String?,
        val last_name: String?,
        val deactivated: String?
) : Response