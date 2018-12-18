/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import ru.shadowsparky.messenger.messages_list.MessagesList
import ru.shadowsparky.messenger.response_utils.pojos.VKError
import ru.shadowsparky.messenger.response_utils.responses.ErrorResponse
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEVICE_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIREBASE_TOKEN
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SQLite.DBListTableWrapper
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import ru.shadowsparky.messenger.utils.SQLite.DatabaseManager
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
    private var db: DatabaseManager? = null
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var retrofit: Retrofit
    @Inject protected lateinit var log: Logger
    private val TAG = this.javaClass.name

    init {
        App.component.inject(this)
    }

    fun setOffset(offset: Int) : RequestBuilder {
        this.offset = offset
        return this
    }

    private fun cacher(response: retrofit2.Response<*>) {
        log.print("CACHER", false, TAG)
        if ((db is DBViewTableWrapper) and (offset == 0)) {
            val db_view = (db as DBViewTableWrapper)
            val peer_id = db_view.getPeerID(response.body()!! as HistoryResponse)
            db_view.removeAllByUserID(peer_id)
        } else {
            if (offset == 0)
                db!!.removeAll()
        }
        db!!.writeToDB(response.body()!! as Response, response.raw().request().url().toString())
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
        log.print("Send message request...", true, TAG)
        request = retrofit
            .create(VKApi::class.java)
            .sendMessage(peerId!!, message!!, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
            .map {
                errorHandler(it)
                return@map it
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun getHistoryRequest() : RequestBuilder {
        log.print("Get history request...", true, TAG)
        db = DBViewTableWrapper()
        request = retrofit
            .create(VKApi::class.java)
            .getHistory(offset!!, 20, peerId!!, preferencesUtils.read(SharedPreferencesUtils.TOKEN))
            .map {
                errorHandlerWithCacher(it)
                return@map it
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun getDialogsRequest() : RequestBuilder {
        db = DBListTableWrapper()
        log.print("Get dialogs request...", true, TAG)
        request = retrofit
            .create(VKApi::class.java)
            .getDialogs(offset!!, 20, "all", preferencesUtils.read(TOKEN))
            .map {
                errorHandlerWithCacher(it)
                return@map it
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun errorHandlerWithCacher(it: Any) {
        errorHandler(it)
        cacher(it as retrofit2.Response<*>)
    }

    fun errorHandler(it: Any) {
        val error = parseError((it as retrofit2.Response<*>).body())
        if (error != null) {
            throw VKException(error)
        }
    }

    fun subscribeToPushRequest() : RequestBuilder {
        log.print("Subscribe to push request...", true, TAG)
        request = retrofit
            .create(VKApi::class.java)
            .subscribeToPush(
                preferencesUtils.read(TOKEN),
                preferencesUtils.read(FIREBASE_TOKEN),
                preferencesUtils.read(DEVICE_ID)
            )
            .doOnSuccess { /*check(it.body()!!.error)*/ }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }

    fun getLongPollServerRequest() : RequestBuilder {
        log.print("Subscribe to get long poll server request...", true, TAG)
        request = retrofit
                .create(VKApi::class.java)
                .getLongPollServer(preferencesUtils.read(TOKEN))
//                .doOnSuccess { /*check(it.body()!!.error)*/ }
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
        configureCallbacks()
        return this
    }


    private fun parseError(response: Any?) : VKError? {
        if (response != null){
            log.printError("${Gson().toJson(response)}", false, TAG)
            val error = Gson().fromJson(Gson().toJson(response), ErrorResponse::class.java)
            log.printError("$error and {${error.error}}", false, TAG)
            return error.error
        }
        return null
    }

    private fun configureCallbacks() {
        result = request!!.subscribeBy (
            onSuccess = {
            it as retrofit2.Response<*>
                log.print("Request successfully executed. url: ${it.raw().request().url()}", true, TAG)
                successCallback!!(it.body()!! as Response)
            } ,
            onError = {
                log.print("Request was unsuccessfully executed. $it", true, TAG)
                failureCallback!!(it)
            }
        )
    }

    fun build() : BuiltRequest = BuiltRequest(result!!)
}