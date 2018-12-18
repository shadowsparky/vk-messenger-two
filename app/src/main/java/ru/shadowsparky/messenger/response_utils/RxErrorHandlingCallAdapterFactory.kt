package ru.shadowsparky.messenger.response_utils

import android.R.raw
import retrofit2.adapter.rxjava2.Result.response
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException


/*class RxErrorHandlingCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original: RxJavaCallAdapterFactory

    init {
        original = RxJavaCallAdapterFactory.create()
    }

    fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*> {
        return RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit))
    }

    private class RxCallAdapterWrapper(private val retrofit: Retrofit, private val wrapped: CallAdapter<*>) : CallAdapter<Observable<*>> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        fun <R> adapt(call: Call<R>): Observable<*> {
            return (wrapped.adapt(call) as Observable).onErrorResumeNext(object : Func1<Throwable, Observable>() {
                fun call(throwable: Throwable): Observable {
                    return Observable.error(asRetrofitException(throwable))
                }
            })
        }

        private fun asRetrofitException(throwable: Throwable): RetrofitException {
            // We had non-200 http error
            if (throwable is HttpException) {
                val httpException = throwable as HttpException
                val response = httpException.response()
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit)
            }
            // A network error happened
            return if (throwable is IOException) {
                RetrofitException.networkError(throwable as IOException)
            } else RetrofitException.unexpectedError(throwable)

            // We don't know what happened. We need to simply convert to an unknown error

        }
    }

    companion object {

        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }
}*/