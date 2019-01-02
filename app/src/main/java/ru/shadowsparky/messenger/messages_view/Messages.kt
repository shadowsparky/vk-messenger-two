/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.RequestHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.SQLite.MessagesViewTable

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit, photo_touch_callback: (ImageView, String) -> Unit)
        fun setLoading(result: Boolean)
        fun disposeAdapter()
        fun clearMessageText()
    }

    interface Presenter : RequestHandler {
        fun attachPeerID(peerId: Int) : MessagesPresenter
        fun onGetMessageHistoryRequest()
        fun onScrollFinished(position: Int)
        fun onPhotoTouched(image: ImageView, url: String)
        fun attachView(view: MessagesView)
        fun onGetPhoto(url: String, image: ImageView)
        fun onSendMessage(message: String)
        fun onActivityDestroying()
    }

    interface Model {
        fun attachCallbacks(successCallback: (Response) -> Unit, failureCallback: (Throwable) -> Unit)
        fun getMessageHistory(peerId: Int, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
        fun getCachedHistory(callback: (Response) -> Unit, user_id: Long)
        fun sendMessage(peerId: Int, message: String)
        fun disposeRequests()
    }
}