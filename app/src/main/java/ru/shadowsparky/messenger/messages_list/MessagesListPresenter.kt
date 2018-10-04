/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.response_utils.responses.VKPushResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesListPresenter : MessagesList.Presenter {
    @Inject protected lateinit var model: MessagesList.Model
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    private var view: MessagesList.View? = null
    init {
        App.component.inject(this)
    }

    override fun onPushSubscribing() {
        val good: (VKPushResponse) -> Unit = {
            log.print("Вы подписались на пуш уведомления")
        }
        val fail: (Throwable) -> Unit = {
            log.print("Error: $it")
        }
        model.subscribeToPush(good, fail)
    }

    override fun onFailureResponse(error: Throwable) = errorUtils.onFailureResponse(error)

    override fun attachView(view: MessagesList.View) {
        this.view = view
        errorUtils.attachView(view)
    }

    override fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int)
            = view!!.navigateToHistory(id, user_data, url, online_status)

    override fun onActivityOpen() {
        view!!.setLoading(true)
        model.getAllDialogs(::onSuccessResponse, ::onFailureResponse)
    }

    override fun onScrollFinished(currentOffset: Int) {
        model.getAllDialogs(::onSuccessResponse, ::onFailureResponse, currentOffset)
    }

    override fun onSuccessResponse(response: MessagesResponse) {
        view!!.setAdapter(response, ::onScrollFinished, ::onItemClicked)
        view!!.setLoading(false)
    }

    override fun disposeRequests() {
        model.disposeRequests()
    }
}