/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import com.google.gson.annotations.SerializedName
import ru.shadowsparky.messenger.response_utils.pojos.VKError

data class ErrorResponse(
    @SerializedName("error")
    val error: VKError
)