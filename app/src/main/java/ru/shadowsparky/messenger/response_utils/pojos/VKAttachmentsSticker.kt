package ru.shadowsparky.messenger.response_utils.pojos

data class VKAttachmentsSticker(
    val product_id: Int,
    val sticker_id: Int,
    val images: ArrayList<VKPhotoSize>
)