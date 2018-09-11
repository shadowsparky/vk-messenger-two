package ru.shadowsparky.messenger.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.ToastUtils
import javax.inject.Singleton

@Module
class AdditionalModule(val context: Context) {

    @Provides
    @Singleton
    fun provideLogger() : Logger = Logger()

    @Provides
    @Singleton
    fun provideToast() : ToastUtils = ToastUtils()

    @Provides
    @Singleton
    fun provideContext() : Context = context

    @Provides
    @Singleton
    fun provideSharedPreferences() : SharedPreferencesUtils = SharedPreferencesUtils(context)
}