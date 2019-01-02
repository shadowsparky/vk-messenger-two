package ru.shadowsparky.messenger.response_utils.pojos

import okhttp3.internal.platform.ConscryptPlatform

data class VKLastSeen(
    val time: Int,
    val platform: Int
)