/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import ru.shadowsparky.messenger.response_utils.Response

data class VKPushSettings (
        val disabled_until: Int?,
        val disabled_forever: Boolean?,
        val no_sound: Boolean?
) : Response