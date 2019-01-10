/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPoll
import ru.shadowsparky.messenger.response_utils.responses.*
import ru.shadowsparky.messenger.utils.Constansts.Companion.LAST_SEEN_FIELD
import ru.shadowsparky.messenger.utils.Constansts.Companion.LONG_POLL_VERSION
import ru.shadowsparky.messenger.utils.Constansts.Companion.VK_API_VERSION

interface VKApi {
    @GET("method/messages.getConversations")
    fun getDialogs(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("filter") filter: String,
        @Query("access_token") token: String,
        @Query("fields") fields: String = LAST_SEEN_FIELD + ", online, photo_100",
        @Query("v") version: Double = VK_API_VERSION,
        @Query("extended") extended: Boolean = true
    ) : Single<Response<MessagesResponse>>

    @GET("method/messages.getHistory")
    fun getHistory(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("user_id") user_id: Int,
        @Query("access_token") token: String,
        @Query("v") version: Double = VK_API_VERSION,
        @Query("extended") extended: Boolean = true
    ) : Single<Response<HistoryResponse>>

    @GET("method/messages.send")
    fun sendMessage (
        @Query("peer_id") peer_id: Int,
        @Query("message") message: String,
        @Query("access_token") token: String,
        @Query("v") version: Double = VK_API_VERSION
    ) : Single<Response<SendMessageResponse>>

    @GET("method/account.registerDevice")
    fun subscribeToPush (
        @Query("access_token") access_token: String,
        @Query("token") token: String,
        @Query("device_id") device_id: String,
        @Query("provider") provider: String = "fcm",
        @Query("v") version: Double = VK_API_VERSION,
        @Query("settings") settings: String = "{\"msg\":\"on\", \"chat\":\"on\"}"
    ) : Single<Response<VKPushResponse>>

    @GET("method/messages.getLongPollServer")
    fun getLongPollServer (
        @Query("access_token") access_token: String,
        @Query("need_pts") need_pts: Boolean = true,
        @Query("lp_version") lp_version: Int = LONG_POLL_VERSION,
        @Query("v") version: Double = VK_API_VERSION
    ) : Single<Response<LongPollServerResponse>>

    @GET("/{method}")
    fun getLongPoll (
        @Path("method") method: String,
        @Query("key") key: String,
        @Query("ts") ts: Long,
        @Query("wait") wait: Long = 25,
        @Query("mode") mode: Long = 8,
        @Query("version") version: Long = 3,
        @Query("act") a_check: String = "a_check"
    ) : Single<Response<VKLongPoll>>

    @GET("method/messages.getById")
    fun getById (
        @Query("access_token") access_token: String,
        @Query("message_ids") message_ids: String,
        @Query("extended") extended : Boolean = true,
        @Query("v") version: Double = VK_API_VERSION
    ) : Single<Response<GetByIDResponse>>
}