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
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

class MessagesListModel(
        private val log: Logger,
        private val preferencesUtils: SharedPreferencesUtils
) : MessagesList.Model {
    @Inject lateinit var retrofit: Retrofit
    private var disposables = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    override fun getAllDialogs(callback: (MessagesResponse) -> Unit, failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = retrofit.create(VKApi::class.java)
                .getDialogs(offset, 20, "all", preferencesUtils.read(TOKEN))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            log.print("${it.raw().request().url()}")
                            log.print("Get all dialogs was successfully executed.")
                            callback(it.body()!!)
                        },
                        onError = {
                            log.print("Get all dialogs was unsuccessfully executed. $it")
                            failureHandler(it)
                        }
                )
        disposables.add(request)
    }

    override fun disposeRequests() {
        if ((disposables.size() > 0) and (!disposables.isDisposed)) {
            log.print("Requests disposed. Size: ${disposables.size()}")
            disposables.dispose()
        }
    }
}