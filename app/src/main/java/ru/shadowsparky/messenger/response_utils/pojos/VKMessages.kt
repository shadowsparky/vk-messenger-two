/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.pojos

import java.io.Serializable


data class VKMessages (
    val count: Int,
    val items: ArrayList<VKItems>,
    val profiles: ArrayList<VKProfile>,
    val groups: ArrayList<VKGroup>
) : Serializable