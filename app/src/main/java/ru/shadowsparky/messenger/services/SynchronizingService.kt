/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import android.os.Message
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.*
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPoll
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPollServer
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.RESPONSE
import ru.shadowsparky.messenger.utils.Logger
import java.lang.ClassCastException
import java.lang.Error
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SynchronizingService : IntentService("Synchronizing Service"), RequestHandler {
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var requester: Requester
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
        requester.attachCallbacks(::onSuccessResponse, ::onFailureResponse)
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
        requester.disposeRequests()
        log.print("$TAG dead. Rest in Peace", true, TAG)
    }

    override fun onSuccessResponse(response: Response) {
        when(response) {
            is LongPollServerResponse -> longPollServerHandler(response)
            is MessagesResponse -> getByIDHandler(response)
        }
    }

    override fun onFailureResponse(error: Throwable) {
        log.printError(error.toString(), false, TAG)
        Thread.sleep(5000)
        Request_Flag = true
    }


    private fun splitServerPath(data: VKLongPollServer) : List<String>? {
        if (data.server != null) {
            val path = data.server.split('/')
            if (path.size > 1) {
                return path
            } else
                onFailureResponse(RuntimeException("Request error: Path size too small"))
        } else
            onFailureResponse(RuntimeException("Request error: Server is null"))
        return null
    }

    private fun longPollServerHandler(data: LongPollServerResponse) {
        val data = data.response
        if (data != null) {
            path = splitServerPath(data)
            if (path != null) {
                response = data
                initLongPoll(path!![0])
                getLongPoll(path!![1], response!!)
            } else
                onFailureResponse(RuntimeException("Request error: Path is null"))
        } else
            onFailureResponse(RuntimeException("Request error: Response is null"))
    }

    private fun getLongPoll(path: String, data: VKLongPollServer) {
        request = long_poll!!.create(VKApi::class.java)
            .getLongPoll(path, data.key, data.ts)
            .subscribeBy(
                onSuccess = { longPollHandler(it) },
                onError = { onFailureResponse(it) }
            )
        disposables.addRequest(request!!)
    }

    private fun parseResult(updates: ArrayList<ArrayList<Any>>) {
        var ids = ""
        for (i in 0 until updates.size) {
            val element = updates[i]
            if (element[0] is Double) {
                if ((element[0] == 4.0) or (element[0] == 5.0) or (element[0] == 2.0) or (element[0] == 6.0) or (element[0] == 7.0)) {
                    ids += if (i != updates.size - 1)
                        "${element[1]}, "
                    else
                        element[1].toString()
                }
            } else
                onFailureResponse(IllegalArgumentException("Request Error: First element unrecognized"))
        }
        if (ids != "")
            requester.getByID(ids)
    }

    private fun longPollHandler(data: retrofit2.Response<VKLongPoll>) {
        log.print("${data.raw().request().url()}", true, TAG)
        response!!.ts = data.body()!!.ts.toLong()
        if (data.body()!!.updates != null) {
            if (data.body()!!.updates.size > 0) {
                parseResult(data.body()!!.updates)
            } else
                onFailureResponse(RuntimeException("Request Error: Updates size is 0"))
        } else
            onFailureResponse(NullPointerException("Request Error: Updates is null"))
        Request_Flag = true
    }

    fun getByIDHandler(it: MessagesResponse) {
        if (it.response != null) {
            initBroadcast()
            log.print("$it", false, TAG)
            broadcast!!.putExtra("test", true)
            broadcast!!.putExtra(RESPONSE, it.response)
            sendBroadcast(broadcast)
        } else {
            onFailureResponse(RuntimeException("Get By ID Response is null"))
        }
    }

    private fun sendRequest() {
        if ((response == null) or (path == null))
            requester.getLongPollServer()
        else
            getLongPoll(path!![1], response!!)
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