/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.Constansts.Companion.CONNECTION_ERROR_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.UNHANDLED_EXCEPTION_CODE
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import java.net.UnknownHostException

class MessagesPresenter(
        private val peer_id: Int,
        private val view: Messages.View,
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger
) : Messages.Presenter {

    private val model = MessagesModel(preferencesUtils, log, peer_id)

    override fun onSendMessage(message: String) =
            model.sendMessage(message, ::onMessageSuccessfullySent, ::onFailureResponse)

    override fun onGetPhoto(url: String, image: ImageView) = model.getPhoto(url, image)

    override fun onGetMessageHistoryRequest() =
            model.getMessageHistory(peer_id, ::onSuccessResponse, ::onFailureResponse)

    override fun onScrollFinished(position: Int) {
        model.getMessageHistory(peer_id, ::onSuccessResponse, ::onFailureResponse, position)
    }

    override fun onFailureResponse(exception: Throwable) {
        if (exception is UnknownHostException) {
            view.showError(CONNECTION_ERROR_CODE)
        } else {
            view.showError(UNHANDLED_EXCEPTION_CODE)
        }
    }

    override fun onSuccessResponse(response: HistoryResponse) = view.setAdapter(response, ::onScrollFinished)

    override fun onMessageSuccessfullySent(response: SendMessageResponse) {
        view.disposeAdapter()
        onGetMessageHistoryRequest()
        view.clearMessageText()
    }
}