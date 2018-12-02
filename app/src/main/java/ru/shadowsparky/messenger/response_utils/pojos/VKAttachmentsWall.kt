package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKAttachmentsWall(
    val id: Int,
    val from_id: Int,
    val to_id: Int,
    val date: Int,
    val post_type: String,
    val text: String,
    val market_as_ads: Int,
    val attachments: ArrayList<VKAttachments>
) : Serializable