/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

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
        val payload: String?
)