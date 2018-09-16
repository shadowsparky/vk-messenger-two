package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse


interface MessagesList {
    interface View {
        fun setLoading(result: Boolean)
        fun showError()
    }

    interface Presenter {
        fun onActivityOpen()
    }

    interface Model {
        fun getAllDialogs(callback: (MessagesResponse?) -> Unit, offset: Int = 0)
    }
}