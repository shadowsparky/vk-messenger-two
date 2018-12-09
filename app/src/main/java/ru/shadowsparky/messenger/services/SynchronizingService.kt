/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks
import android.content.Intent
import android.content.IntentFilter
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.VKLongPollApi
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPoll
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPollServer
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// FIXME Переполнение стека.
class SynchronizingService : IntentService("Synchronizing Service") {
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var log: Logger
    private var broadcast: Intent? = null
    private val TAG = "SYNCHRONIZING_SERVICE"
    private var long_poll: Retrofit? = null
    private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    private var count = 0

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
    }

    private fun reInitDisposables() {
        disposables.disposeAllRequests()
        disposables = CompositeDisposableManager()
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

    override fun onDestroy() {
        super.onDestroy()
        disposables.disposeAllRequests()
        log.print("$TAG dead. Rest in Peace", true, TAG)
    }


    private fun failureCallback(e: Throwable) {
        log.printError(e.toString())
        reInitDisposables()
        Thread.sleep(5000)
        getLongPollServer()
    }

    private fun longPollServerHandler(response: Response) {
        if (response is LongPollServerResponse) {
            val response = response.response
            if (response.server != null) {
                val path = response.server.split('/')
                if (path.size > 1) {
                    initLongPoll(path[0])
                    getLongPoll(path[1], response)
                }
                else {
                    failureCallback(RuntimeException("Request error: Path size too small"))
                }
            } else {
                failureCallback(RuntimeException("Request error: Server is null"))
            }
        } else {
            failureCallback(RuntimeException("Request error: Unrecognized result. Result should be LongPollServerResponse"))
        }
    }

    private fun getLongPoll(path: String, response: VKLongPollServer) {
        val request = long_poll!!.create(VKApi::class.java)
            .getLongPoll(path, response.key, response.ts)
            .subscribeBy(
                onSuccess = { longPollHandler(it) },
                onError = { failureCallback(it) }
            )
        disposables.addRequest(request)
    }

    private fun longPollHandler(data: retrofit2.Response<VKLongPoll>) {
        log.print("${data.raw().request().url()}", true, TAG)
        count++
        log.print("COUNT $count", false, TAG)
        if (data.body()!!.updates.size > 0) {
            for (element in data.body()!!.updates)
                if (element[0] is Double) {
                    if ((element[0] == 4.0) or (element[0] == 5.0) or (element[0] == 2.0) ) {
                        initBroadcast()
                        broadcast!!.putExtra("test", true)
                        sendBroadcast(broadcast)
                    }
                }
        }
        reInitDisposables()
        getLongPollServer()
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