/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response
import java.io.Serializable

data class VKConversation(
    val peer: VKPeer,
    val in_read: Int,
    val out_read: Int,
    val unread_count: Int,
    val important: Boolean,
    val unanswered: Boolean,
    val push_settings: VKPushSettings,
    val chat_settings: VKChatSettings,
    val can_write: VKCanWrite
) : Serializable