/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.response_utils.responses.VKPushResponse

interface VKApi {
    @GET("method/messages.getConversations")
    fun getDialogs(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("filter") filter: String,
        @Query("access_token") token: String,
        @Query("v") version: Double = 5.85,
        @Query("extended") extended: Boolean = true
    ) : Single<Response<MessagesResponse>>

    @GET("method/messages.getHistory")
    fun getHistory(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("user_id") user_id: Int,
        @Query("access_token") token: String,
        @Query("v") version: Double = 5.85,
        @Query("extended") extended: Boolean = true
    ) : Single<Response<HistoryResponse>>

    @GET("method/messages.send")
    fun sendMessage(
        @Query("peer_id") peer_id: Int,
        @Query("message") message: String,
        @Query("access_token") token: String,
        @Query("v") version: Double = 5.85
    ) : Single<Response<SendMessageResponse>>

    @GET("method/account.registerDevice")
    fun subscribeToPush(
        @Query("access_token") access_token: String,
        @Query("token") token: String,
        @Query("device_id") device_id: String,
        @Query("push_provider") push_provider: String = "fcm",
        @Query("v") version: Double = 5.85
    ) : Single<Response<VKPushResponse>>
}