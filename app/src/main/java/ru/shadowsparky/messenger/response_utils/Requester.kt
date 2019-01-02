package ru.shadowsparky.messenger.response_utils

import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import java.lang.NullPointerException
import javax.inject.Inject

class Requester  {
    @Inject protected lateinit var disposables: CompositeDisposableManager
    private var successCallback: ((Response) -> Unit)? = null
    private var failureCallback: ((Throwable) -> Unit)? = null
    private val TAG = javaClass.name
    init {
        App.component.inject(this)
    }

    fun attachCallbacks(successCallback: ((Response) -> Unit), failureCallback: ((Throwable) -> Unit)) {
        this.successCallback = successCallback
        this.failureCallback = failureCallback
    }

    private fun checkCallbacks() {
        if ((successCallback == null) or (failureCallback == null))
            throw NullPointerException("Callbacks not initialized")
    }

    fun subscrubeToPush() {
        checkCallbacks()
        val request = RequestBuilder()
                .setCallbacks(successCallback!!, failureCallback!!)
                .subscribeToPushRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun getAllDialogs(offset: Int) {
        checkCallbacks()
        val request = RequestBuilder()
                .setOffset(offset)
                .setCallbacks(successCallback!!, failureCallback!!)
                .getDialogsRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun getMessageHistory(peerId: Int, offset: Int) {
        checkCallbacks()
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setOffset(offset)
                .setCallbacks(successCallback!!, failureCallback!!)
                .getHistoryRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun sendMessage(peerId: Int, message: String) {
        checkCallbacks()
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setMessage(message)
                .setCallbacks(successCallback!!, failureCallback!!)
                .sendMessageRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun disposeRequests() = disposables.disposeAllRequests()

}