/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.RequestHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse


interface MessagesList {
    interface View {
        fun setLoading(result: Boolean)
        fun disposeAdapter()
        fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit,
                       touch_callback: (Int, String, String, online_status: Int) -> Unit)
        fun navigateToHistory(id: Int, user_data: String, url: String, online_status: Int)
    }

    interface Presenter : RequestHandler {
        fun onPushSubscribing()
        fun onScrollFinished(currentOffset: Int = 0)
        fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int)
        fun attachView(view: MessagesListView)
        fun onActivityDestroying()
    }

    interface Model {
        fun getAllDialogs(callback: (Response) -> Unit,
                          failureHandler: (Throwable) -> Unit, offset: Int = 0)
        fun subscribeToPush(callback: (Response) -> Unit,
                            failureHandler: (Throwable) -> Unit)
        fun getCachedDialogs(callback: (Response) -> Unit)
        fun disposeRequests()
    }
}