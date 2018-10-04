/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

open class MessagesListModel : MessagesList.Model {
    @Inject protected lateinit var retrofit: Retrofit
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    private var disposables = CompositeDisposable()
    init {
        App.component.inject(this)
    }

    override fun getAllDialogs(callback: (MessagesResponse) -> Unit, failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = RequestBuilder()
                .setOffset(offset)
                .setCallbacks(callback as (Response) -> Unit, failureHandler)
                .getDialogsRequest()
                .build()
        disposables.add(request.getDisposable())
    }

    override fun disposeRequests() {
        if ((disposables.size() > 0) and (!disposables.isDisposed)) {
            log.print("Requests disposed. Size: ${disposables.size()}")
            disposables.dispose()
        }
    }
}