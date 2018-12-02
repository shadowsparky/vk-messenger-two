/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKAttachments(
    val type: String,
    val photo: VKAttachmentsPhoto,
    val sticker: VKAttachmentsSticker,
    val wall: VKAttachmentsWall
) : Serializable