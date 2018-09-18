package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.utils.DateUtils
import javax.inject.Singleton

@Module
class DateModule {

    @Provides
    @Singleton
    fun provideDateUtils() : DateUtils = DateUtils()
}