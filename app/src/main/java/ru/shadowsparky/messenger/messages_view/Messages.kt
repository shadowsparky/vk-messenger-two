/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import java.lang.Exception

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit)
        fun showError(code: Int)
        fun disposeAdapter()
        fun clearMessageText()
    }

    interface Presenter {
        fun onGetMessageHistoryRequest()
        fun onSuccessResponse(response: HistoryResponse)
        fun onFailureResponse(exception: Throwable)
        fun onScrollFinished(position: Int)
        fun onGetPhoto(url: String, image: ImageView)
        fun onSendMessage(message: String)
        fun onMessageSuccessfullySent(response: SendMessageResponse)
    }

    interface Model {
        fun getMessageHistory(user_id: Int, callback: (HistoryResponse) -> Unit,
                              failureHandler: (Throwable) -> Unit, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
        fun sendMessage(message: String, callback: (SendMessageResponse) -> Unit, failureHandler: (Throwable) -> Unit)
    }
}