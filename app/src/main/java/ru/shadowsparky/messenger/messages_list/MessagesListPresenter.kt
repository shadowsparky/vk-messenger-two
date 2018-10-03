/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.ResponseHandler
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils

// TODO: Нужен нормальный DI. Зачем я Dagger ставил АЛЛО.
class MessagesListPresenter(
        override val view: MessagesList.View,
        private val log: Logger,
        preferencesUtils: SharedPreferencesUtils
) : MessagesList.Presenter, ResponseHandler(view) {
    val model = MessagesListModel(log, preferencesUtils)

    override fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int)
            = view.navigateToHistory(id, user_data, url, online_status)

    override fun onActivityOpen() {
        view.setLoading(true)
        model.getAllDialogs(::onSuccessResponse, ::onFailureResponse)
    }

    override fun onScrollFinished(currentOffset: Int) {
        model.getAllDialogs(::onSuccessResponse, ::onFailureResponse, currentOffset)
    }

    override fun onSuccessResponse(response: Response) {
        view.setAdapter(response as MessagesResponse, ::onScrollFinished, ::onItemClicked)
        view.setLoading(false)
    }

    override fun disposeRequests() {
        model.disposeRequests()
    }
}