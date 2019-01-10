/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.*
import ru.shadowsparky.messenger.response_utils.api.VKApi
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPoll
import ru.shadowsparky.messenger.response_utils.pojos.VKLongPollServer
import ru.shadowsparky.messenger.response_utils.requester.Requester
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEAD
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_SERVER_SPLITTER
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_SLEEP_TIME_ON_ERROR
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_TIMEOUT
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.LAST_SEEN_FIELD
import ru.shadowsparky.messenger.utils.Constansts.Companion.RESPONSE
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_OFFLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_ONLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_LONG_POLL_STATUS_CHANGED
import ru.shadowsparky.messenger.utils.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class SynchronizingService : IntentService("Synchronizing Service"), RequestHandler {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var requester: Requester
    @Inject protected lateinit var log: Logger
    private var broadcast: Intent? = null
    private val TAG = javaClass.name
    private var long_poll: Retrofit? = null
    private var response: VKLongPollServer? = null
    private var path: List<String>? = null
    private var Request_Flag = true
    private var request: Disposable? = null
    private val USER_ONLINE = 8.0
    private val USER_OFFLINE = 9.0

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
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor {
                        val request = it.request()
                        val response = it.proceed(request)
                        response.body()
                        return@addInterceptor response
                    }
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .build()
            long_poll = Retrofit.Builder().baseUrl("https://$path/")
                .addCallAdapterFactory(retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.disposeAllRequests()
        requester.disposeRequests()
        log.print("$TAG $DEAD", false, TAG)
    }

    override fun onSuccessResponse(response: Response) {
        when(response) {
            is LongPollServerResponse -> longPollServerHandler(response)
            is MessagesResponse -> getByIDHandler(response)
        }
    }

    override fun onFailureResponse(error: Throwable) {
        log.printError(error.toString(), false, TAG)
        Thread.sleep(DEFAULT_SLEEP_TIME_ON_ERROR)
        Request_Flag = true
    }


    private fun splitServerPath(data: VKLongPollServer) : List<String>? {
        if (data.server != null) {
            val path = data.server.split(DEFAULT_SERVER_SPLITTER)
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
        var ids = EMPTY_STRING
        for (i in 0 until updates.size) {
            val element = updates[i]
            if (element[0] is Double) {
                when {
                    (element[0] == 4.0) or (element[0] == 5.0) or (element[0] == 2.0)
                            or (element[0] == 6.0) or (element[0] == 7.0) ->
                        ids = userMessagesChangedCallback(element, updates, i)
                    element[0] == USER_ONLINE -> userOnlineCallback(element)
                    element[0] == USER_OFFLINE -> userOfflineCallback(element)
                }
            } else
                onFailureResponse(IllegalArgumentException("Request Error: First element unrecognized"))
        }
        if (ids != EMPTY_STRING)
            requester.getByID(ids)
    }

    private fun userMessagesChangedCallback(element: ArrayList<Any>, updates: ArrayList<ArrayList<Any>>, i: Int) =
            if (i != updates.size - 1)
                "${element[1]}, "
            else
                element[1].toString()

    private fun userOnlineCallback(element: ArrayList<Any>) {
        initBroadcast()
        broadcast!!.putExtra(USER_LONG_POLL_STATUS_CHANGED, STATUS_ONLINE)
        broadcast!!.putExtra(USER_ID, abs((element[1] as Double).toInt()))
        sendBroadcast(broadcast)
    }

    private fun userOfflineCallback(element: ArrayList<Any>) {
        initBroadcast()
        broadcast!!.putExtra(USER_LONG_POLL_STATUS_CHANGED, STATUS_OFFLINE)
        broadcast!!.putExtra(USER_ID, abs((element[1] as Double).toInt()))
        broadcast!!.putExtra(LAST_SEEN_FIELD, (element[3] as Double).toInt())
        sendBroadcast(broadcast)
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