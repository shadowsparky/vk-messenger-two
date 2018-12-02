package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKPhotoSize(
    val type: String,
    val url: String,
    val width: Int,
    val height: Int
) : Serializable