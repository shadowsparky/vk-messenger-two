package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

class MessagesListPresenter(
        private val view: MessagesList.View,
        private val log: Logger,
        preferencesUtils: SharedPreferencesUtils
) : MessagesList.Presenter {
    override fun onItemClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val model = MessagesListModel(log, preferencesUtils)
    override fun onActivityOpen() {
        view.setLoading(true)
        model.getAllDialogs(this::onResponseHandled)
    }

    override fun onScrollFinished(currentOffset: Int) {
        model.getAllDialogs(this::onResponseHandled, currentOffset)
    }

    override fun onResponseHandled(response: MessagesResponse?) {
        if (response != null) {
            log.print("COUNT: ${response.response.count}")
            log.print("PROFILES: ${response.response.profiles}")
            log.print("ITEMS: ${response.response.items}")
            view.setAdapter(response, this::onScrollFinished, this::onItemClicked)
        } else {
            view.showError()
        }
        view.setLoading(false)
    }
}