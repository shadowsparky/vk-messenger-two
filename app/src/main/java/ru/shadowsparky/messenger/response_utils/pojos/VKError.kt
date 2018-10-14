/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import com.google.gson.annotations.SerializedName
import ru.shadowsparky.messenger.response_utils.Response

data class VKError(
    @SerializedName("error_code")
    val error_code: Int,
    @SerializedName("error_msg")
    val error_msg:String,
    val request_params: List<VKRequestParams>
)