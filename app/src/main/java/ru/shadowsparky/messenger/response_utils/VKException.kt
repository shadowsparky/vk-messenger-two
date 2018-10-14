/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import ru.shadowsparky.messenger.response_utils.pojos.VKError
import ru.shadowsparky.messenger.response_utils.responses.ErrorResponse

class VKException(val error: VKError?) : RuntimeException(error?.error_msg)