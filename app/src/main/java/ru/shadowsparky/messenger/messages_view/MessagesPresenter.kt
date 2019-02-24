/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView // FIXME Зависимость андроида в Presenter.
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.pojos.VKMessage
import ru.shadowsparky.messenger.response_utils.requester.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_TOOLBAR
import ru.shadowsparky.messenger.utils.Constansts.Companion.SELECTED_TOOLBAR
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class MessagesPresenter : Messages.Presenter {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var model: Messages.Model
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    @Inject protected lateinit var log: Logger
    private var view: MessagesView? = null
    private var peerId: Int? = null
    private var loadingError = false
    private val TAG = javaClass.name

    init {
        App.component.inject(this)
        model.attachCallbacks(::onSuccessResponse, ::onFailureResponse)
    }

    override fun onItemSelected(map: HashMap<Int, VKMessage>) {
        if (map.size > 0) {
            view!!.setSelectionActionMenu("")
//            view!!.setToolbar(DEFAULT_TOOLBAR)
        } else {
//            view!!.setToolbar(SELECTED_TOOLBAR)
        }
    }

    override fun onScroll(position: Int) {
        view!!.setLoading(true)
        model.getMessageHistory(peerId!!, position)
    }

    override fun onPhotoClicked(image: ImageView, url: String) {
        view!!.photoTouched(image, url)
    }


    override fun attachPeerID(peerId: Int) : MessagesPresenter {
        this.peerId = peerId
        return this
    }

    override fun attachView(view: MessagesView) {
        this.view = view
        errorUtils.attach(view)
    }


    override fun onGetMessageHistoryRequest() {
        view!!.setLoading(true)
        view!!.disposeAdapter()
        model.getMessageHistory(peerId!!)
    }

    override fun onSendMessage(message: String) {
        view!!.setLoading(true)
        model.sendMessage(peerId!!, message)
    }

    override fun onFailureResponse(error: Throwable) {
        val onErrorCallback: (response: Response) -> Unit = {
            loadingError = true
            view!!.setAdapter(it as HistoryResponse)
        }
        if (!loadingError) {
            view!!.setLoading(true)
            model.getCachedHistory(onErrorCallback, peerId!!.toLong())
            view!!.disposeAdapter()
        }
        view!!.setLoading(false)
        errorUtils.onFailureResponse(error)
    }

    override fun onActivityDestroying() = model.disposeRequests()

    override fun onSuccessResponse(response: Response) {
        when (response) {
            is HistoryResponse -> view!!.setAdapter(response)
            is SendMessageResponse -> view!!.clearMessageText()
            else -> onFailureResponse(ClassCastException())
        }
        loadingError = false
    }
}