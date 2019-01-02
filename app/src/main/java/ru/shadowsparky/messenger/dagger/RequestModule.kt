/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import android.util.Log
import android.util.Log.DEBUG
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Requester
import ru.shadowsparky.messenger.response_utils.pojos.VKError
import ru.shadowsparky.messenger.response_utils.responses.ErrorResponse
import javax.inject.Singleton

@Module
class RequestModule {
    @Provides fun provideOkHttpClient() : OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor {
                    val request = it.request()
                    val response = it.proceed(request)
                    response.body()
//                    request.body()
//                    Log.println(DEBUG, "MAIN_TAG", "${response.request().url()}")
//                    }
                    return@addInterceptor response
                }
                .build()
    }

    @Provides fun provideRequester() : Requester = Requester()

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://api.vk.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideErrorHandler() : FailureResponseHandler = FailureResponseHandler()
}