package ru.shadowsparky.messenger.dagger

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.utils.SQLite.DBListTableWrapper
import ru.shadowsparky.messenger.utils.SQLite.MessagesDB
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {
    @Provides
    @Singleton
    fun provideDB() : MessagesDB = Room.databaseBuilder(context, MessagesDB::class.java, "db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDbManager() : DBListTableWrapper = DBListTableWrapper()
}