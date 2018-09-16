package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

class MessagesListPresenter(
        private val view: MessagesList.View,
        private val log: Logger,
        preferencesUtils: SharedPreferencesUtils
) : MessagesList.Presenter {
    val model = MessagesListModel(log, preferencesUtils)

    override fun onActivityOpen() {
        view.setLoading(true)
        val callback: (MessagesResponse?) -> Unit = {
            if (it != null) {
                log.print("COUNT: ${it.response.count}")
                log.print("PROFILES: ${it.response.profiles}")
                log.print("ITEMS: ${it.response.items}")
            } else {
                view.showError()
            }
            view.setLoading(false)
        }
        model.getAllDialogs(callback)
    }
}