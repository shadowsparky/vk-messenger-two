package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKCanWrite(
        val allowed: Boolean?,
        val reason: Int?
) : Response