package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKLongPollServer(
    var key: String,
    var server: String,
    var ts: Long,
    var pts: Long
) : Serializable