package ru.shadowsparky.messenger.dagger

import android.content.Context
import android.provider.ContactsContract
import androidx.room.Database
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.utils.SQLite.DatabaseManager
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
    fun provideDbManager() : DatabaseManager = DatabaseManager()
}