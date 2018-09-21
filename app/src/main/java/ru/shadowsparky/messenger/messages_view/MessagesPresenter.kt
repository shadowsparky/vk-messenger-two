/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import com.jakewharton.rxbinding2.widget.RxTextView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

class MessagesPresenter(
        private val peer_id: Int,
        private val view: Messages.View,
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger
) : Messages.Presenter {

    override fun onSendMessage(message: String) {
        val callback: (SendMessageResponse?) -> Unit = {
            if (it != null) {
                view.disposeAdapter()
                onGetMessageHistoryRequest()
                view.clearMessageText()
            } else {
                view.showError()
            }
        }
        model.sendMessage(message, callback)
    }

    override fun onGetPhoto(url: String, image: ImageView) {
        model.getPhoto(url, image)
    }

    private val model = MessagesModel(preferencesUtils, log, peer_id)

    override fun onGetMessageHistoryRequest() {
        model.getMessageHistory(peer_id, this::onResponseHandled)
    }

    override fun onScrollFinished(position: Int) {
        model.getMessageHistory(peer_id, this::onResponseHandled, position)
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