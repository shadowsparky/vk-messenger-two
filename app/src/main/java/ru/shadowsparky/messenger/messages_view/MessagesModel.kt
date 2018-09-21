package ru.shadowsparky.messenger.messages_view

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel(
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger
) : Messages.Model {

    @Inject
    lateinit var retrofit: Retrofit

    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(user_id: Int, callback: (HistoryResponse?) -> Unit, offset: Int) {
        Observable.just(20)
                .observeOn(Schedulers.io())
                .map {
                    retrofit.create(VKApi::class.java)
                            .getHistory(offset, it, user_id,
                                    preferencesUtils.read(SharedPreferencesUtils.TOKEN)).blockingFirst()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy (
                        onNext = {
                            log.print("${it.raw().request().url()}")
                            log.print("Get message history was successfully executed.")
                            callback(it.body())
                        },
                        onError = {
                            log.print("Get message history was unsuccessfully executed: $it")
                            callback(null)
                        }
                )
    }
}