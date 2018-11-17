package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessagesListTable::class], version = 1)
abstract class MessagesDB : RoomDatabase() {
    abstract fun MessagesListDao() : MessagesListDao
}