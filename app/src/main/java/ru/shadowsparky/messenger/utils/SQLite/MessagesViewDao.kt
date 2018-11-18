package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessagesViewDao {
    @Query("SELECT id, response, url, user_id FROM MessagesViewTable")
    fun getAll() : List<MessagesViewTable>

    @Query("SELECT id, response, url, user_id FROM MessagesViewTable WHERE id = :id")
    fun getByID(id: Long) : List<MessagesViewTable>

    @Query("SELECT id, response, url, user_id FROM MessagesViewTable WHERE user_id = :user_id")
    fun getByUserID(user_id: Long) : List<MessagesViewTable>

    @Query("SELECT id FROM MessagesViewTable ORDER BY id DESC LIMIT 1")
    fun getLastID() : Long

    @Insert
    fun insert(data: MessagesViewTable)

    @Query("DELETE FROM MessagesViewTable")
    fun removeAll()

    @Query("DELETE FROM MessagesViewTable WHERE user_id = :user_id")
    fun removeAllByUserID(user_id: Long)
}