/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel(
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger,
        private val peer_id: Int
) : Messages.Model {

    @Inject
    lateinit var retrofit: Retrofit

    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(user_id: Int, callback: (HistoryResponse?) -> Unit, offset: Int) {
        retrofit
                .create(VKApi::class.java)
                .getHistory(offset, 20, user_id, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
                .subscribeOn(Schedulers.computation())
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

    override fun sendMessage(message: String, callback: (SendMessageResponse?) -> Unit) {
        retrofit.create(VKApi::class.java)
                .sendMessage(peer_id, message, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            log.print("${it.raw().request().url()}")
                            log.print("Send message was successfully executed.")
                            callback(it.body())
                        },
                        onError = {
                            log.print("Send message was unsuccessfully executed: $it")
                            callback(null)
                        }
                )
    }

    override fun getPhoto(url: String, image: ImageView) {
        picasso.load(url).circle().into(image)
    }
}