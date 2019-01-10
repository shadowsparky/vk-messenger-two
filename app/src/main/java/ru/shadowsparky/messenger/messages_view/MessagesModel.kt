/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.requester.Requester
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel : Messages.Model {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var db: DBViewTableWrapper
    @Inject protected lateinit var requester: Requester

    init {
        App.component.inject(this)
    }

    override fun attachCallbacks(successCallback: (Response) -> Unit, failureCallback: (Throwable) -> Unit) =
            requester.attachCallbacks(successCallback, failureCallback)
    override fun getMessageHistory(peerId: Int, offset: Int) = requester.getMessageHistory(peerId, offset)
    override fun getCachedHistory(callback: (Response) -> Unit, user_id: Long) =
            db.getAllByIDWithCallback(callback, user_id)
    override fun sendMessage(peerId: Int, message: String) = requester.sendMessage(peerId, message)
    override fun disposeRequests() = requester.disposeRequests()
}