package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

class MessagesListPresenter(
        private val view: MessagesList.View,
        log: Logger,
        preferencesUtils: SharedPreferencesUtils
) : MessagesList.Presenter {
    val model = MessagesListModel(log, preferencesUtils)

    override fun onActivityOpen() {
        view.setLoading(true)
        val callback: (MessagesResponse?) -> Unit = {
            if (it != null) {

            } else {

            }
            view.setLoading(false)
        }
        model.getAllDialogs(callback)
    }

}