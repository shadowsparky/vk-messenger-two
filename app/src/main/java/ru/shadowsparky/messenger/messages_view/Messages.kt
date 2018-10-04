/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.DisplayError
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import java.lang.Exception

interface Messages {

    interface View : DisplayError {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit)
        fun disposeAdapter()
        fun clearMessageText()
    }

    interface Presenter {
        fun onGetMessageHistoryRequest()
        fun onScrollFinished(position: Int)
        fun onGetPhoto(url: String, image: ImageView)
        fun onSendMessage(message: String)
        fun onMessageSuccessfullySent(response: SendMessageResponse)
        fun disposeRequests()
    }

    interface Model {
        fun getMessageHistory(peerId: Int, callback: (HistoryResponse) -> Unit,
                              failureHandler: (Throwable) -> Unit, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
        fun sendMessage(peerId: Int, message: String, callback: (SendMessageResponse) -> Unit,
                        failureHandler: (Throwable) -> Unit)
        fun disposeRequests()
    }
}