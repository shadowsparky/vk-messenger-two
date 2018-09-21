package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse

interface Messages {

    interface View {
        fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit)
    }

    interface Presenter {
        fun onGetMessageHistoryRequest()
        fun onResponseHandled(response: HistoryResponse?)
        fun onScrollFinished(position: Int)
        fun onGetPhoto(url: String, image: ImageView)
    }

    interface Model {
        fun getMessageHistory(user_id: Int, callback: (HistoryResponse?) -> Unit, offset: Int = 0)
        fun getPhoto(url: String, image: ImageView)
    }
}