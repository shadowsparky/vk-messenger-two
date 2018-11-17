package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessagesListDao {
    @Query("SELECT id, response, url FROM MessagesListTable")
    fun getAll() : List<MessagesListTable>

    @Query("SELECT id, response, url FROM MessagesListTable WHERE id = :id")
    fun getByID(id: Long) : MessagesListTable

    @Query("SELECT id FROM MessagesListTable ORDER BY id DESC LIMIT 1")
    fun getLastID() : Long

    @Insert
    fun insert(data: MessagesListTable)

    @Query("DELETE FROM MessagesListTable")
    fun removeAll()
}