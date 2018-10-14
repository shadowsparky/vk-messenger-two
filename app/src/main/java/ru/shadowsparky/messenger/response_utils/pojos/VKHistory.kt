/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKHistory(
        val count: Int?,
        val items: ArrayList<VKMessage>?,
        val conversations: ArrayList<VKConversation>?,
        val profiles: ArrayList<VKProfile>?
)