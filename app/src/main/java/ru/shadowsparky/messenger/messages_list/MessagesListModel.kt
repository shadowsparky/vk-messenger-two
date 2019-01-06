/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.requester.Requester
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SQLite.DBListTableWrapper
import javax.inject.Inject

class MessagesListModel : MessagesList.Model {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var requester: Requester
    @Inject protected lateinit var db: DBListTableWrapper

    init {
        App.component.inject(this)
    }

    override fun attachCallbacks(successCallback: (Response) -> Unit, failureCallback: (Throwable) -> Unit) =
            requester.attachCallbacks(successCallback, failureCallback)
    override fun subscribeToPush() = requester.subscrubeToPush()
    override fun getAllDialogs(offset: Int) = requester.getAllDialogs(offset)
    override fun getCachedDialogs(successCallback: (Response) -> Unit) = db.getAllWithCallback(successCallback)
    override fun disposeRequests() = requester.disposeRequests()
}