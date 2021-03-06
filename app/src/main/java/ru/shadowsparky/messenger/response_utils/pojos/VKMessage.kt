/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKMessage(
    val id: Int?,
    val user_id: Int?,
    val date: Long?,
    val read_state: Int?,
    val out: Int?,
    val conversation_message_id: Int?,
    val is_hidden: Boolean?,
    val attachments: ArrayList<VKAttachments>,
    val peer_id: Int?,
    val from_id: Int?,
    val text: String?,
    val random_id: Int?,
    val important: Boolean?,
    val payload: String?,
    val reply_message: VKMessage?,
    val fwd_messages: ArrayList<VKMessage>?,
    var isSelected: Boolean = false
) : Serializable