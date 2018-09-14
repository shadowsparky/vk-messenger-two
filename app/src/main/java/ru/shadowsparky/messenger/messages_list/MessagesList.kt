package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.Message

interface MessagesList {
    interface View {
        fun setLoading(result: Boolean)
    }

    interface Presenter {
        fun onActivityOpen()
    }

    interface Model {
        fun getAllDialogs(callback: (ArrayList<Message>?) -> Unit)
    }
}