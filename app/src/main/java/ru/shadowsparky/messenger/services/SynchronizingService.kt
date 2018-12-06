package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SynchronizingService() : IntentService("Synchronizing Service") {
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var log: Logger
    private var broadcast: Intent? = null
    private val TAG = "SYNCHRONIZING_SERVICE"
    private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    private var long_poll: Retrofit? = null

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
    }

    private fun initBroadcast() {
        broadcast = Intent()
        broadcast!!.action = BROADCAST_RECEIVER_CODE
    }

    fun initLongPoll(path: String) {
        long_poll = Retrofit.Builder().baseUrl("https://$path/")
            .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    private fun failureCallback(e: Throwable) = log.printError(e.toString())

    private fun longPollServerHandler(response: Response) {
        if (response is LongPollServerResponse) {
            val response = response.response
            val path = response.server.split('/')
            if (path.isNotEmpty()) {
                initLongPoll(path[0])
            }
        } else {
            failureCallback(RuntimeException("Unrecognized result. Result should be LongPollServerResponse"))
        }
    }

    private fun getLongPollServer() {
        val disposable = RequestBuilder()
            .setCallbacks(this::longPollServerHandler, this::failureCallback)
            .getLongPollServerRequest()
            .build()
        disposables.addRequest(disposable.getDisposable())
    }

    override fun onHandleIntent(intent: Intent?) {
        getLongPollServer()
    }
}