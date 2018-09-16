package ru.shadowsparky.messenger.messages_list

import io.reactivex.Observable
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

    init {
        App.component.inject(this)
    }

    override fun getAllDialogs(callback: (MessagesResponse?) -> Unit) {
        log.print("Dialogs Loading...")
        Observable.just("")
                .observeOn(Schedulers.io())
                .map {
                    retrofit.create(VKApi::class.java)
                        .getDialogs(0, 20, "all", preferencesUtils.read(TOKEN))
                        .blockingFirst()
                }
                .subscribeBy(
                        onNext = {
                            log.print("URL: ${it.raw().request().url()}")
                            callback(it.body())
                        },
                        onError = {
                            log.print("ERROR: $it")
                            callback(null)
                        }
                )
    }

}