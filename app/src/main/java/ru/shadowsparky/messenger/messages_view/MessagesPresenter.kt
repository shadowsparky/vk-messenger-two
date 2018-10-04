/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class MessagesPresenter(
        private val peerId: Int,
        override val view: Messages.View
) : ResponseHandler(view), Messages.Presenter {
    @Inject protected lateinit var model: MessagesModel

    init {
        App.component.inject(this)
    }

    override fun onSendMessage(message: String) =
            model.sendMessage(peerId, message, ::onMessageSuccessfullySent, ::onFailureResponse)

    override fun onGetPhoto(url: String, image: ImageView) = model.getPhoto(url, image)

    override fun onGetMessageHistoryRequest() =
            model.getMessageHistory(peerId,::onSuccessResponse, ::onFailureResponse)

    override fun onScrollFinished(position: Int) {
        model.getMessageHistory(peerId,::onSuccessResponse, ::onFailureResponse, position)
    }

    override fun onSuccessResponse(response: Response) = view.setAdapter(response as HistoryResponse, ::onScrollFinished)

    override fun onMessageSuccessfullySent(response: SendMessageResponse) {
        view.disposeAdapter()
        onGetMessageHistoryRequest()
        view.clearMessageText()
    }

    override fun disposeRequests() = model.disposeRequests()

}