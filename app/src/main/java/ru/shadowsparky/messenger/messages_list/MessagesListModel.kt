/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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

    override fun getAllDialogs(callback: (MessagesResponse?) -> Unit, offset: Int) {
        retrofit.create(VKApi::class.java)
                .getDialogs(offset, 20, "all", preferencesUtils.read(TOKEN))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            log.print("${it.raw().request().url()}")
                            log.print("Get all dialogs was successfully executed.")
                            callback(it.body())
                        },
                        onError = {
                            log.print("Get all dialogs was unsuccessfully executed. $it")
                            callback(null)
                        }
                )

    }
}