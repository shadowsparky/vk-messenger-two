/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKAttachmentsVideo (
        val vid: Int,
        val owner_id: Int,
        val title: String,
        val description: String,
        val duration: String,
        val image: String,
        val image_big: String,
        val image_small: String,
        val views: Int,
        val date: Long
)