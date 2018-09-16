package ru.shadowsparky.messenger.response_utils

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse

interface VKApi {
    @GET("method/messages.getConversations")
    fun getDialogs(
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("filter") filter: String,
        @Query("access_token") token: String,
        @Query("v") version: Double = 5.85
    ) : Observable<retrofit2.Response<MessagesResponse>>
}