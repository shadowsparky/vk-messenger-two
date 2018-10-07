/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.content.Context
import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.DisplayError
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import java.lang.Exception

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit)
        fun disposeAdapter()
        fun clearMessageText()
    }

    interface Presenter {
        fun attachPeerID(peerId: Int) : MessagesPresenter
        fun onGetMessageHistoryRequest()
        fun onScrollFinished(position: Int)
        fun attachView(view: MessagesView)
        fun onGetPhoto(url: String, image: ImageView)
        fun onSendMessage(message: String)
        fun onSuccessResponse(response: Response)
        fun onFailureResponse(error: Throwable)
        fun onActivityDestroying()
    }

    interface Model {
        fun getMessageHistory(peerId: Int, callback: (Response) -> Unit,
                              failureHandler: (Throwable) -> Unit, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
        fun sendMessage(peerId: Int, message: String, callback: (Response) -> Unit,
                        failureHandler: (Throwable) -> Unit)
        fun disposeRequests()
    }
}