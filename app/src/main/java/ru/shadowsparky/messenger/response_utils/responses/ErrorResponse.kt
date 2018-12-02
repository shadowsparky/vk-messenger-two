/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.responses

import com.google.gson.annotations.SerializedName
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKError
import java.io.Serializable

data class ErrorResponse(
    val error: VKError
) : Response, Serializable