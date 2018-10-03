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
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MessagesModel(
        private val preferencesUtils: SharedPreferencesUtils,
        private val log: Logger,
        private val peerId: Int
) : Messages.Model {

    @Inject
    lateinit var retrofit: Retrofit
    private var disposables = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    override fun getMessageHistory(callback: (HistoryResponse) -> Unit,
                                   failureHandler: (Throwable) -> Unit, offset: Int) {
        val request = retrofit
                .create(VKApi::class.java)
                .getHistory(offset, 20, peerId, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy (
                        onSuccess = {
                            log.print("${it.raw().request().url()}")
                            log.print("Get message history was successfully executed.")
                            callback(it.body()!!)
                        },
                        onError = {
                            log.print("Get message history was unsuccessfully executed: $it")
                            failureHandler(it)
                        }
                )
        disposables.add(request)
    }

    override fun sendMessage(message: String, callback: (SendMessageResponse) -> Unit,
                             failureHandler: (Throwable) -> Unit) {
        val request = retrofit
                .create(VKApi::class.java)
                .sendMessage(peerId, message, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            log.print("${it.raw().request().url()}")
                            log.print("Send message was successfully executed.")
                            callback(it.body()!!)
                        },
                        onError = {
                            log.print("Send message was unsuccessfully executed: $it")
                            failureHandler(it)
                        }
                )
        disposables.add(request)
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