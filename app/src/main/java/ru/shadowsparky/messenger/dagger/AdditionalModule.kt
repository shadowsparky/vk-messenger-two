/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.utils.*
import ru.shadowsparky.messenger.utils.SQLite.MessagesDB
import javax.inject.Singleton

@Module
class AdditionalModule(val context: Context) {
    @Provides
    @Singleton
    fun provideDB() : MessagesDB = Room.databaseBuilder(context, MessagesDB::class.java, "db")
            .fallbackToDestructiveMigration()
            .build()

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

    @Provides
    @Singleton
    fun provideValidator() : Validator = Validator()

    @Provides
    @Singleton
    fun provideTextColor() : TextColorUtils = TextColorUtils(context)
}