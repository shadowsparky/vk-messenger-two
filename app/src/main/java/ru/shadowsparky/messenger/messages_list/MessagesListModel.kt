/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.response_utils.responses.VKPushResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesListModel : MessagesList.Model {
    @Inject protected lateinit var disposables: CompositeDisposableManager

    init {
        App.component.inject(this)
    }

    override fun subscribeToPush(callback: (Response) -> Unit,
                                 failureHandler: (Throwable) -> Unit) {
        val request = RequestBuilder()
                .setCallbacks(callback, failureHandler)
                .subscribeToPushRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    override fun getAllDialogs(callback: (Response) -> Unit,
                               failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = RequestBuilder()
                .setOffset(offset)
                .setCallbacks(callback, failureHandler)
                .getDialogsRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    override fun disposeRequests() = disposables.disposeAllRequests()
}