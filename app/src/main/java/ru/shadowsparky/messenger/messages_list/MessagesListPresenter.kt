/*
 * Copyright Samsonov Eugene(c) 2018.
 */

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

    override fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int)
            = view.navigateToHistory(id, user_data, url, online_status)

    override fun onActivityOpen() {
        view.setLoading(true)
        model.getAllDialogs(::onResponseHandled)
    }

    override fun onScrollFinished(currentOffset: Int) {
        model.getAllDialogs(::onResponseHandled, currentOffset)
    }

    override fun onResponseHandled(response: MessagesResponse?) {
        if (response != null) {
            view.setAdapter(response, this::onScrollFinished, this::onItemClicked)
        } else {
            view.showError()
        }
        view.setLoading(false)
    }
}