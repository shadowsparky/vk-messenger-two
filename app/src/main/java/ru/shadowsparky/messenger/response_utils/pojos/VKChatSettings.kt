package ru.shadowsparky.messenger.response_utils.pojos

data class VKChatSettings(
        val title: String,
        val members_count: Int,
        val state: String,
        val photo: VKPhoto
)