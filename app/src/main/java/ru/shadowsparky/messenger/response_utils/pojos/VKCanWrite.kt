/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response
import java.io.Serializable

data class VKCanWrite(
        val allowed: Boolean,
        val reason: Int
)  : Serializable