/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class MessagesPresenter : Messages.Presenter {
    @Inject protected lateinit var model: Messages.Model
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    private var view: Messages.View? = null
    private var peerId: Int? = null

    init {
        App.component.inject(this)
    }

    override fun attachPeerID(peerId: Int) : MessagesPresenter {
        this.peerId = peerId
        return this
    }

    override fun onSendMessage(message: String) =
            model.sendMessage(peerId!!, message, ::onMessageSuccessfullySent, ::onFailureResponse)

    override fun onGetPhoto(url: String, image: ImageView) = model.getPhoto(url, image)

    override fun onGetMessageHistoryRequest() =
            model.getMessageHistory(peerId!!,::onSuccessResponse, ::onFailureResponse)

    override fun onScrollFinished(position: Int) {
        model.getMessageHistory(peerId!!,::onSuccessResponse, ::onFailureResponse, position)
    }

    override fun attachView(view: Messages.View) {
        this.view = view
        errorUtils.attachView(view)
    }

    override fun onFailureResponse(error: Throwable) = errorUtils.onFailureResponse(error)


    override fun onSuccessResponse(response: HistoryResponse) = view!!.setAdapter(response, ::onScrollFinished)

    override fun onMessageSuccessfullySent(response: SendMessageResponse) {
        view!!.disposeAdapter()
        onGetMessageHistoryRequest()
        view!!.clearMessageText()
    }

    override fun disposeRequests() = model.disposeRequests()

}