/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response
import java.io.Serializable

data class VKPeer(
        val id: Int?,
        val type: String?,
        val local_id: Int?
) : Serializable