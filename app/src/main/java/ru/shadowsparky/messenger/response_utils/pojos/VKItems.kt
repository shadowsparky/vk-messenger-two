/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKItems(
    val conversation: VKConversation,
    val last_message: VKMessage
) : Serializable
