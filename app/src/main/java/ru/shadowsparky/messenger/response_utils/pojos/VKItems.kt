package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKItems(
        val conversation: VKConversation?,
        val last_message: VKMessage?
) : Response