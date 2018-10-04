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
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel : Messages.Model, CompositeDisposableManager() {
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(peerId: Int, callback: (HistoryResponse) -> Unit,
                                   failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setOffset(offset)
                .setCallbacks(callback as (Response) -> Unit, failureHandler)
                .getHistoryRequest()
                .build()
        addToCollection(request.getDisposable())
    }

    override fun sendMessage(peerId: Int, message: String, callback: (SendMessageResponse) -> Unit,
                             failureHandler: (Throwable) -> Unit) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setCallbacks(callback as (Response) -> Unit, failureHandler)
                .setMessage(message)
                .sendMessageRequest()
                .build()
        addToCollection(request.getDisposable())
    }

    override fun getPhoto(url: String, image: ImageView) = picasso.load(url).circle().into(image)

    override fun disposeRequests() = disposeAllRequests()

}