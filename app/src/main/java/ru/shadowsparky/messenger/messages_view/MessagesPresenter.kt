/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
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

    override fun attachView(view: MessagesView) {
        this.view = view
        errorUtils.attach(view)
    }

    override fun onGetMessageHistoryRequest() =
            model.getMessageHistory(peerId!!, ::onSuccessResponse, ::onFailureResponse)

    override fun onScrollFinished(position: Int) =
            model.getMessageHistory(peerId!!,::onSuccessResponse, ::onFailureResponse, position)

    override fun onSendMessage(message: String) =
            model.sendMessage(peerId!!, message, ::onSuccessResponse, ::onFailureResponse)

    override fun onFailureResponse(error: Throwable) {
        model.getCachedDialogs(::onSuccessResponse, peerId!!.toLong())
        view!!.disposeAdapter()
        errorUtils.onFailureResponse(error)
    }

    override fun onGetPhoto(url: String, image: ImageView) = model.getPhoto(url, image)

    override fun onActivityDestroying() = model.disposeRequests()

    override fun onSuccessResponse(response: Response) {
        when (response) {
            is HistoryResponse -> view!!.setAdapter(response, ::onScrollFinished)
            is SendMessageResponse -> {
                view!!.run {
                    disposeAdapter()
                    clearMessageText()
                }
                onGetMessageHistoryRequest()
            }
            else -> onFailureResponse(ClassCastException())
        }
    }
}