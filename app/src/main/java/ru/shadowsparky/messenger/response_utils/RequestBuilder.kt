/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.response_utils.responses.ErrorResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEVICE_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIREBASE_TOKEN
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import java.lang.RuntimeException
import javax.inject.Inject

class RequestBuilder {
    private var request: Single<*>? = null
    private var result: Disposable? = null
    private var offset: Int? = null
    private var peerId: Int? = null
    private var message: String? = null
    private var successCallback: ((Response) -> Unit)? = null
    private var failureCallback: ((Throwable) -> Unit)? = null
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var retrofit: Retrofit
    @Inject protected lateinit var log: Logger

    init {
        App.component.inject(this)
    }

    fun setOffset(offset: Int) : RequestBuilder {
        this.offset = offset
        return this
    }

    fun setPeerId(peerId: Int) : RequestBuilder {
        this.peerId = peerId
        return this
    }

    fun setMessage(message: String) : RequestBuilder {
        this.message = message
        return this
    }

    fun setCallbacks(callback: (Response) -> Unit, failureHandler: (Throwable) -> Unit) : RequestBuilder {
        successCallback = callback
        failureCallback = failureHandler
        return this
    }

    fun sendMessageRequest() : RequestBuilder {
        log.print("Send message request...")
        request = retrofit
            .create(VKApi::class.java)
            .sendMessage(peerId!!, message!!, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
            .doOnSuccess { check(it.body()!!.error) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun getHistoryRequest() : RequestBuilder {
        log.print("Get history request...")
        request = retrofit
            .create(VKApi::class.java)
            .getHistory(offset!!, 20, peerId!!, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
            .doOnSuccess { check(it.body()!!.error) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun getDialogsRequest() : RequestBuilder {
        log.print("Get dialogs request...")
        request = retrofit
            .create(VKApi::class.java)
            .getDialogs(offset!!, 20, "all", preferencesUtils.read(TOKEN))
            .doOnSuccess { check(it.body()!!.error) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun subscribeToPushRequest() : RequestBuilder {
        log.print("Subscribe to push request...")
        request = retrofit
            .create(VKApi::class.java)
            .subscribeToPush(
                preferencesUtils.read(TOKEN),
                preferencesUtils.read(FIREBASE_TOKEN),
                preferencesUtils.read(DEVICE_ID)
            )
            .doOnSuccess { check(it.body()!!.error) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    private fun check(error: ErrorResponse?) {
        if (error != null) {
            log.print("ERROR OBJECT: $error")
            throw VKException(error.error)
        }
    }

    private fun configureCallbacks() {
        result = request!!.subscribeBy (
            onSuccess = {
                it as retrofit2.Response<*>
                successCallback!!(it.body()!! as Response)
                log.print("Request successfully executed.")
            },
            onError = {
                log.print("Request was unsuccessfully executed. $it")
                failureCallback!!(it)
            }
        )
    }

    fun build() : BuiltRequest = BuiltRequest(result!!)
}