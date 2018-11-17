package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessagesListDao {
    @Query("SELECT id, response FROM MessagesListTable")
    fun getAll() : List<MessagesListTable>

    @Insert
    fun insert(data: MessagesListTable)

    @Query("DELETE FROM MessagesListTable")
    fun removeAll()
}