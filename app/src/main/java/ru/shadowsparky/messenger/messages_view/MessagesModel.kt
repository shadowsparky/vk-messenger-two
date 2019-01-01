/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import ru.shadowsparky.messenger.utils.SQLite.MessagesViewTable
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel : Messages.Model {
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var db: DBViewTableWrapper

    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(peerId: Int, callback: (Response) -> Unit,
       failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setOffset(offset)
                .setCallbacks(callback, failureHandler)
                .getHistoryRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    override fun getCachedHistory(callback: (Response) -> Unit, user_id: Long) =
        db.getAllByIDWithCallback(callback, user_id)

    override fun sendMessage(peerId: Int, message: String, callback: (Response) -> Unit,
        failureHandler: (Throwable) -> Unit) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setMessage(message)
                .setCallbacks(callback, failureHandler)
                .sendMessageRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    override fun getPhoto(url: String, image: ImageView) = picasso.load(url).circle().into(image)

    override fun disposeRequests() = disposables.disposeAllRequests()
}