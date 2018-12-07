package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKLongPoll(
    val ts: Int,
    val updates: ArrayList<ArrayList<Any>>
) : Serializable