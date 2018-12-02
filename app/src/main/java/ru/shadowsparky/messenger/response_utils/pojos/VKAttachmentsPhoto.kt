/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response
import java.io.Serializable

data class VKAttachmentsPhoto (
    val id: Int,
    val album_id: Int,
    val owner_id: Int,
    val src_big: Int,
    val sizes: ArrayList<VKPhotoSize>
)  : Serializable