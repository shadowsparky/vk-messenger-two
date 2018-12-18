/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPoll
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPollServer
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    private var response: VKLongPollServer? = null
    private var path: List<String>? = null
    private var Request_Flag = true
    private var request: Disposable? = null

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
    }

    private fun initBroadcast() {
        broadcast = Intent()
        broadcast!!.action = BROADCAST_RECEIVER_CODE
    }

    private fun initLongPoll(path: String) {
        if (long_poll == null) {
            long_poll = Retrofit.Builder().baseUrl("https://$path/")
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .client(client)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.disposeAllRequests()
        log.print("$TAG dead. Rest in Peace", true, TAG)
    }


    private fun failureCallback(e: Throwable) {
        log.printError(e.toString())
        Thread.sleep(5000)
        Request_Flag = true
    }


    private fun splitServerPath(data: VKLongPollServer) : List<String>? {
        if (data.server != null) {
            val path = data.server.split('/')
            if (path.size > 1) {
                return path
            } else
                failureCallback(RuntimeException("Request error: Path size too small"))
        } else
            failureCallback(RuntimeException("Request error: Server is null"))
        return null
    }

    private fun longPollServerHandler(data: Response) {
        if (data is LongPollServerResponse) {
            val data = data.response
            if (data != null) {
                path = splitServerPath(data)
                if (path != null) {
                    response = data
                    initLongPoll(path!![0])
                    getLongPoll(path!![1], response!!)
                } else
                    failureCallback(RuntimeException("Request error: Path is null"))
            } else
                failureCallback(RuntimeException("Request error: Response is null"))
        } else
            failureCallback(RuntimeException("Request error: Unrecognized result. Result should be LongPollServerResponse"))
    }

    private fun getLongPoll(path: String, data: VKLongPollServer) {
        request = long_poll!!.create(VKApi::class.java)
            .getLongPoll(path, data.key, data.ts)
            .subscribeBy(
                onSuccess = { longPollHandler(it) },
                onError = { failureCallback(it) }
            )
        disposables.addRequest(request!!)
    }

    private fun longPollHandler(data: retrofit2.Response<VKLongPoll>) {
        log.print("${data.raw().request().url()}", true, TAG)
        response!!.ts = data.body()!!.ts.toLong()
        if (data.body()!!.updates != null) {
            if (data.body()!!.updates.size > 0) {
                for (element in data.body()!!.updates)
                    if (element[0] is Double) {
                        if ((element[0] == 4.0) or (element[0] == 5.0) or (element[0] == 2.0) or (element[0] == 6.0) or (element[0] == 7.0)) {
                            initBroadcast()
                            broadcast!!.putExtra("test", true)
                            sendBroadcast(broadcast)
                        }
                    } else
                        failureCallback(RuntimeException("Request Error: First element unrecognized"))
            } else
                failureCallback(RuntimeException("Request Error: Updates size is 0"))
        } else
            failureCallback(RuntimeException("Request Error: Updates is null"))
        Request_Flag = true
    }

    private fun sendRequest() {
        if ((response == null) or (path == null))
            getLongPollServer()
        else
            getLongPoll(path!![1], response!!)
    }

    private fun getLongPollServer() {
        request = RequestBuilder()
            .setCallbacks(::longPollServerHandler, ::failureCallback)
            .getLongPollServerRequest()
            .build()
            .getDisposable()
        disposables.addRequest(request!!)
    }

    override fun onHandleIntent(intent: Intent?) {
        while (true) {
            if (Request_Flag) {
                Request_Flag = false
                sendRequest()
            }
        }
    }
}