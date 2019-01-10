package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKLongPollItems(
    val date: Int,
    val from_id: Int,
    val id: Int,
    val out: Int,
    val peer_id: Int,
    val text: String,
    val conversation_message_id: Int
) : Serializable