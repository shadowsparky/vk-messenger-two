package ru.shadowsparky.messenger.messages_view

import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

class MessagesPresenter(
        private val user_id: Int,
        private val view: Messages.View,
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger
) : Messages.Presenter {
    private val model = MessagesModel(preferencesUtils, log)

    override fun onGetMessageHistoryRequest() {
        model.getMessageHistory(user_id, this::onResponseHandled)
    }

    override fun onScrollFinished(position: Int) {
        model.getMessageHistory(user_id, this::onResponseHandled, position)
    }

    override fun onResponseHandled(response: HistoryResponse?) {
        if (response != null) {
            log.print("Response is not null")
            view.setAdapter(response, this::onScrollFinished)

        } else {
            log.print("Response is null")
        }
    }
}