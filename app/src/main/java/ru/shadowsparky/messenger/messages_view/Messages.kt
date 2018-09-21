package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit)
        fun showError()
        fun disposeAdapter()
        fun clearMessageText()
    }

    interface Presenter {
        fun onGetMessageHistoryRequest()
        fun onResponseHandled(response: HistoryResponse?)
        fun onScrollFinished(position: Int)
        fun onGetPhoto(url: String, image: ImageView)
        fun onSendMessage(message: String)
    }

    interface Model {
        fun getMessageHistory(user_id: Int, callback: (HistoryResponse?) -> Unit, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
        fun sendMessage(message: String, callback: (SendMessageResponse?) -> Unit)
    }
}