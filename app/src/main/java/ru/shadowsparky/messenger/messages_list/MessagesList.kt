package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse


interface MessagesList {
    interface View {
        fun setLoading(result: Boolean)
        fun showError()
        fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit, touch_callback: () -> Unit)
    }

    interface Presenter {
        fun onActivityOpen()
        fun onResponseHandled(response: MessagesResponse?)
        fun onScrollFinished(currentOffset: Int)
        fun onItemClicked()
    }

    interface Model {
        fun getAllDialogs(callback: (MessagesResponse?) -> Unit, offset: Int = 0)
    }
}