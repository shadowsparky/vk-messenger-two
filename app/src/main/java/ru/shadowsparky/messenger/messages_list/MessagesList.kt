/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.DisplayError
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse


interface MessagesList {
    interface View : DisplayError {
        fun setLoading(result: Boolean)
        fun disposeAdapter()
        fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit,
                       touch_callback: (Int, String, String, online_status: Int) -> Unit)
        fun navigateToHistory(id: Int, user_data: String, url: String, online_status: Int)
    }

    interface Presenter {
        fun onActivityOpen()
        fun onScrollFinished(currentOffset: Int)
        fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int)
        fun disposeRequests()
    }

    interface Model {
        fun getAllDialogs(callback: (MessagesResponse) -> Unit,
                          failureHandler: (Throwable) -> Unit, offset: Int = 0)
        fun disposeRequests()
    }
}