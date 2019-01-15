package ru.shadowsparky.messenger.response_utils.requester

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.utils.Logger
import java.lang.NullPointerException
import javax.inject.Inject

class Requester  {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var disposables: CompositeDisposableManager
    @Inject protected lateinit var log: Logger
    private var mSuccessCallback: ((Response) -> Unit)? = null
    private var mFailureCallback: ((Throwable) -> Unit)? = null
    private val TAG = javaClass.name

    init {
        App.component.inject(this)
    }

    fun attachCallbacks(mSuccessCallback: ((Response) -> Unit), mFailureCallback: ((Throwable) -> Unit)) {
        this.mSuccessCallback = mSuccessCallback
        this.mFailureCallback = mFailureCallback
    }

    private fun checkCallbacks() {
        if ((mSuccessCallback == null) or (mFailureCallback == null))
            throw NullPointerException("Callbacks not initialized")
    }

    fun getByID(ids: String) {
        checkCallbacks()
        log.print("ids: $ids", false, TAG)
        val request = RequestBuilder()
                .setMessageIds(ids)
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .getById()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun getLongPollServer() {
        checkCallbacks()
        val request = RequestBuilder()
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .getLongPollServerRequest()
                .build()
                .getDisposable()
        disposables.addRequest(request!!)
    }

    fun subscrubeToPush() {
        checkCallbacks()
        val request = RequestBuilder()
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .subscribeToPushRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun getAllDialogs(offset: Int) {
        checkCallbacks()
        val request = RequestBuilder()
                .setOffset(offset)
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .getDialogsRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun getMessageHistory(peerId: Int, offset: Int) {
        checkCallbacks()
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setOffset(offset)
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .getHistoryRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun sendMessage(peerId: Int, message: String) {
        checkCallbacks()
        val request = RequestBuilder()
                .setPeerId(peerId)
                .setMessage(message)
                .setCallbacks(mSuccessCallback!!, mFailureCallback!!)
                .sendMessageRequest()
                .build()
        disposables.addRequest(request.getDisposable())
    }

    fun disposeRequests() = disposables.disposeAllRequests()
}