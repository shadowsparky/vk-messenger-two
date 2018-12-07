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
    private var status = false

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

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        super.unregisterReceiver(receiver)
        disposables.disposeAllRequests()
        status = true
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        return super.registerReceiver(receiver, filter)
        status = false
    }

    private fun failureCallback(e: Throwable) {
        log.printError(e.toString())
        getLongPollServer()
    }

    private fun longPollServerHandler(response: Response) {
        if (response is LongPollServerResponse) {
            val response = response.response
            val path = response.server.split('/')
            if (path.size > 1){
                initLongPoll(path[0])
                getLongPoll(path[1], response)
            }
        } else {
            failureCallback(RuntimeException("Unrecognized result. Result should be LongPollServerResponse"))
        }
    }

    private fun getLongPoll(path: String, response: VKLongPollServer) {
        long_poll!!.create(VKApi::class.java)
            .getLongPoll(path, response.key, response.ts)
            .subscribeBy(
                onSuccess = {
                    log.print("${it.raw().request().url()}", true, TAG)
                    if (it.body()!!.updates.size > 0) {
                        for (element in it.body()!!.updates)
                            if (element[0] is Double) {
                                if ((element[0] == 4.0) or (element[0] == 5.0) or (element[0] == 2.0) ) {
                                    initBroadcast()
                                    broadcast!!.putExtra("test", true)
                                    sendBroadcast(broadcast)
                                }
                            }
                    }
                },
                onError = { log.print("ERROR $it", true, TAG)}
            )
        if (!status)
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