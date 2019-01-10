package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKGetByIDMessages(
    val count: Int,
    val items: ArrayList<VKLongPollItems>,
    val profiles: ArrayList<VKProfile>,
    val groups: ArrayList<VKGroup>
) : Serializable