package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import ru.shadowsparky.messenger.response_utils.RequestBuilder
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.LongPollServerResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class SynchronizingService() : IntentService("Synchronizing Service") {
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var log: Logger
    private var broadcast: Intent? = null
    private val TAG = "SYNCHRONIZING_SERVICE"

    override fun onCreate() {
        super.onCreate()
        App.component.inject(this)
        initBroadcast()
    }

    private fun initBroadcast() {
        broadcast = Intent()
        broadcast!!.action = BROADCAST_RECEIVER_CODE
    }

    private fun failureCallback(e: Throwable) = log.printError(e.toString())

    private fun longPollServerHandler(response: Response) {
        if (response is LongPollServerResponse) {
            log.print(response.toString(), true, TAG)
            broadcast!!.putExtra("test", response)
            sendBroadcast(broadcast)
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