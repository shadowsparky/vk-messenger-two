package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKLongPollServer(
    val key: String,
    val server: String,
    val ts: Long,
    val pts: Long
) : Serializable