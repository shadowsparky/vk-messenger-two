/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKAttachmentsPhoto (
        val pid: Int,
        val owner_id: Int,
        val src: Int,
        val src_big: Int
)