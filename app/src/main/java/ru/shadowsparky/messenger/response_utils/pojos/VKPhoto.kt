package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKPhoto(
    val photo_50: String,
    val photo_100: String,
    val photo_200: String
) : Serializable