package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessagesListTable::class, MessagesViewTable::class], version = 3)
abstract class MessagesDB : RoomDatabase() {
    abstract fun MessagesListDao() : MessagesListDao
    abstract fun MessagesViewDao() : MessagesViewDao
}