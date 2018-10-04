/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

open class MessagesModel(
        private val peerId: Int
) : Messages.Model {
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var log: Logger
    @Inject lateinit var retrofit: Retrofit
    private var disposables = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(callback: (HistoryResponse) -> Unit,
                                   failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setOffset(offset)
                .setCallbacks(callback as (Response) -> Unit, failureHandler)
                .getHistoryRequest()
                .build()
        disposables.add(request.getDisposable())
    }

    override fun sendMessage(message: String, callback: (SendMessageResponse) -> Unit,
                             failureHandler: (Throwable) -> Unit) {
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setCallbacks(callback as (Response) -> Unit, failureHandler)
                .setMessage(message)
                .sendMessageRequest()
                .build()
        disposables.add(request.getDisposable())
    }

    override fun getPhoto(url: String, image: ImageView) {
        picasso.load(url).circle().into(image)
    }

    override fun disposeRequests() {
        if ((disposables.size() > 0) and (!disposables.isDisposed)) {
            log.print("Requests disposed. Size: ${disposables.size()}")
            disposables.dispose()
        }
    }
}