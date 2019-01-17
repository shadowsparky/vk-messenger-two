/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.requester.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.response_utils.responses.VKPushResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import javax.inject.Inject

class MessagesListPresenter : MessagesList.Presenter {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var model: MessagesList.Model
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    @Inject protected lateinit var db: DBViewTableWrapper
    private var view: MessagesList.View? = null
    private var loadingError = false

    init {
        App.component.inject(this)
        model.attachCallbacks(::onSuccessResponse, ::onFailureResponse)
    }

    override fun attachView(view: MessagesListView) {
        this.view = view
        errorUtils.attach(view)
    }

    override fun onCardClicked(peer_id: Int, user_data: String, photo: String, user_status: Int, time: Int) {
        view!!.navigateToHistory(peer_id, user_data, photo, user_status, time)
    }

    override fun onScroll(position: Int) {
        if (position == 0)
            view!!.disposeAdapter()
        model.getAllDialogs(position)
        view!!.setLoading(true)
    }


    override fun onPushSubscribing() = model.subscribeToPush()

    override fun onFailureResponse(error: Throwable) {
        val callback: (response: Response) -> Unit = {
            val mResponse = it as MessagesResponse
            if ((mResponse.error == null) and (mResponse.response != null)) {
                view!!.setAdapter(it)
                loadingError = true
            }
        }
        if (!loadingError) {
            model.getCachedDialogs(callback)
            view!!.disposeAdapter()
        }
        view!!.setLoading(false)
        errorUtils.onFailureResponse(error)
    }

    override fun onActivityDestroying() = model.disposeRequests()

    override fun onSuccessResponse(response: Response) {
        when(response) {
            is MessagesResponse -> view!!.setAdapter(response)
            is VKPushResponse -> log.print("Вы подписались на пуш уведомления")
            else -> onFailureResponse(ClassCastException())
        }
        view!!.setLoading(false)
        loadingError = false
    }
}