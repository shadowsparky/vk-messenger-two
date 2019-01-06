/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.response_utils

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.shadowsparky.messenger.dagger.RequestModule
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.ToastUtils
import java.net.UnknownHostException
import javax.inject.Inject

class FailureResponseHandler {
    private var context: Context? = null
    @Inject protected lateinit var toast: ToastUtils
    private var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://translate.yandex.net/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(RequestModule().provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private var api: YandexApi

    init {
        App.component.inject(this)
        api = retrofit.create(YandexApi::class.java)
    }

    fun attach(context: Context) { this.context = context }

    private fun showError(message: String) = toast.error(context!!, message)

    fun onFailureResponse(reason: Throwable) {
        when (reason) {
            is UnknownHostException -> showError("При соединении с сервером произошла ошибка. Проверьте ваше интернет соединение")
            is ClassCastException -> showError("Сервер вернул неизвестный результат")
            is VKException -> {
                api.translate(reason.error!!.error_msg)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = { showError(it.text[0]) },
                        onError = { showError("Произошла неизвестная ошибка ${it}") }
                    )
            }
            else -> showError("Произошла неизвестная ошибка")
        }
    }
}