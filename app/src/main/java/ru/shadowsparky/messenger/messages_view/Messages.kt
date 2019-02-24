/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.response_utils.RequestHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse)
        fun setLoading(result: Boolean)
        fun setStatus(status: String)
        fun disposeAdapter()
        fun photoTouched(image: ImageView, url: String)
        fun clearMessageText()
    }

    interface Presenter : RequestHandler, HistoryAdapter.ActionListener {
        fun attachPeerID(peerId: Int) : MessagesPresenter
        fun onGetMessageHistoryRequest()
        fun attachView(view: MessagesView)
        fun onSendMessage(message: String)
        fun onActivityDestroying()
    }

    interface Model {
        fun attachCallbacks(successCallback: (Response) -> Unit, failureCallback: (Throwable) -> Unit)
        fun getMessageHistory(peerId: Int, offset: Int = 0)
        fun getCachedHistory(callback: (Response) -> Unit, user_id: Long)
        fun sendMessage(peerId: Int, message: String)
        fun disposeRequests()
    }
}