/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable

data class VKRequestParams (
    val key: String,
    val value: String
) : Serializable